import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.net.URL;
import java.util.*;

public class StudentEditorController implements Initializable
{
	@FXML
	private TreeTableView<Student> treeTable;
	@FXML
	private Menu collapseMenu, showMenu, delYearMenu;
	
	private boolean haltDialog = false;
	private static String pdfFile = "PDFFile.pdf";
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		treeTable.setEditable(true);
		treeTable.setOnKeyTyped(value -> {
			if (value.getCharacter()
					 .equals("\b") || value.getCharacter()
										   .equals("\u007f"))
			{
				TreeItem<Student> currentItem = treeTable.getTreeItem(treeTable.getSelectionModel()
																			   .getFocusedIndex());
				if (currentItem.getValue()
							   .isFilter())
				{
					Dialog<ButtonType> dialog = new Dialog<>()
					{{
						setTitle("Need confirmation!");
						setContentText("Deleting this filter will also delete all student entries that are filtered by it. Continue?");
						setOnCloseRequest(event -> close());
						getDialogPane().getButtonTypes()
									   .add(new ButtonType("Yes", ButtonBar.ButtonData.YES));
						getDialogPane().getButtonTypes()
									   .add(new ButtonType("No", ButtonBar.ButtonData.NO));
					}};
					Optional<ButtonType> result = dialog.showAndWait();
					if (result.isPresent() && result.get()
													.getButtonData() == ButtonBar.ButtonData.YES)
					{
						currentItem.getParent()
								   .getChildren()
								   .remove(currentItem);
					}
				} else
				{
					currentItem.getParent()
							   .getChildren()
							   .remove(currentItem);
				}
			}
		});
		TreeItem<Student> rootItem = Student.makeFilterItem("Main filter");
		treeTable.setRoot(rootItem);
		
		TreeTableColumn<Student, String> filterColumn = new TreeTableColumn<>("Filter / Name")
		{{
			setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
			setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
			setOnEditCommit(value -> value.getRowValue()
										  .getValue()
										  .setName(value.getNewValue()));
		}};
		
		TreeTableColumn<Student, String> surnameColumn = new TreeTableColumn<>("Surname")
		{{
			setCellValueFactory(new TreeItemPropertyValueFactory<>("surname"));
			setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
			setOnEditCommit(value -> value.getRowValue()
										  .getValue()
										  .setSurname(value.getNewValue()));
		}};
		
		treeTable.getColumns()
				 .setAll(filterColumn, surnameColumn);
		
		PDFParser parser = null;
		COSDocument cosDocument = null;
		PDDocument pdDoc = null;
		PDFTextStripper pdfStripper = null;
		String parsedText = null;
		try
		{
			parser = new PDFParser(new RandomAccessFile(new File(pdfFile), "r"));
			parser.parse();
			cosDocument = parser.getDocument();
			pdDoc = new PDDocument(cosDocument);
			pdfStripper = new PDFTextStripper();
			pdfStripper.setStartPage(1);
			pdfStripper.setEndPage(5);
			parsedText = pdfStripper.getText(pdDoc);
			cosDocument.close();
			pdDoc.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		System.out.println(parsedText);
		String[] rows = parsedText.split("\n");
		TreeItem<Student> currentRoot = rootItem;
		int currentLvl = 0;
		for (int j = 0; j < rows.length; ++j)
		{
			String row = rows[j];
			String[] clmns = row.split(" ");
			for (int i = 0; i < clmns.length; ++i)
			{
				clmns[i] = clmns[i].trim();
			}
			TreeItem<Student> readStudentItem = null;
			if (j < rows.length - 1 && rows[j + 1].startsWith(indents(currentLvl)))
			{
				readStudentItem = Student.makeFilterItem(clmns[0].substring(currentLvl));
				currentRoot.getChildren()
						   .add(readStudentItem);
				currentRoot = readStudentItem;
				++currentLvl;
			}
			/*else if (j < rows.length - 1 && !rows[j + 1].startsWith(indents(currentLvl)))
			{
				readStudentItem = Student.makeFilterItem(clmns[0].substring(currentLvl));
				currentRoot.getChildren()
						   .add(readStudentItem);
				currentRoot = currentRoot.getParent();
				--currentLvl;
			}*/
			else
			{
				readStudentItem = Student.makeStudentItem(clmns[0].substring(currentLvl), clmns[1]);
				currentRoot.getChildren()
						   .add(readStudentItem);
			}
			
			for (int i = 2; i < clmns.length; ++i)
			{
				int parsedDay = 0;
				try
				{
					parsedDay = Integer.parseInt(clmns[i]);
				} catch (NumberFormatException e)
				{
					continue;
				}
				final int day = parsedDay;
				readStudentItem.getValue()
							   .setAbsent(day, true);
				
				if (treeTable.getColumns()
							 .filtered(column -> column instanceof YearColumn)
							 .filtered(column -> YearColumn.dateBelongsToStartYear(day, ((YearColumn) column).getStarYear()))
							 .isEmpty())
				{
					System.out.println("ADD" + YearColumn.getStartYearOfDate(day));
					final MenuItem deletDis = new MenuItem("Delete " + Integer.toString(YearColumn.getStartYearOfDate(day)) + " - " + Integer.toString(YearColumn.getStartYearOfDate(day) + 1));
					treeTable.getColumns()
							 .add(new YearColumn(YearColumn.getStartYearOfDate(day), collapseMenu, showMenu)
							 {
								 @Override
								 public void initDestructor()
								 {
									 deletDis.setOnAction(event -> destroy());
									 delYearMenu.getItems()
												.add(deletDis);
								 }
						
								 @Override
								 public void destroy()
								 {
									 delYearMenu.getItems()
												.remove(deletDis);
									 treeTable.getColumns()
											  .remove(this);
								 }
							 });
				}
			}
		}
		//sample(rootItem);
	}
	
	private void sample(TreeItem<Student> rootItem)
	{
		final MenuItem deletDis = new MenuItem("Delete " + Integer.toString(2018) + " - " + Integer.toString(2018 + 1));
		treeTable.getColumns()
				 .add(new YearColumn(2018, collapseMenu, showMenu)
				 {
					 @Override
					 public void initDestructor()
					 {
						 deletDis.setOnAction(event -> destroy());
						 delYearMenu.getItems()
									.add(deletDis);
					 }
			
					 @Override
					 public void destroy()
					 {
						 delYearMenu.getItems()
									.remove(deletDis);
						 treeTable.getColumns()
								  .remove(this);
					 }
				 });
		
		TreeItem<Student> i1 = Student.makeStudentItem(List.of(Integer.valueOf(20180901), Integer.valueOf(20180902), Integer.valueOf(20180903), Integer.valueOf(20180904), Integer.valueOf(20180905), Integer.valueOf(20180906), Integer.valueOf(20180907), Integer.valueOf(20190901)), "pOOPSIE");
		TreeItem<Student> i2 = Student.makeStudentItem("Vardenis", "Pavardenis");
		TreeItem<Student> filter = Student.makeFilterItem("Filter");
		TreeItem<Student> filteredStudent = Student.makeStudentItem("Useless", "student");
		rootItem.getChildren()
				.addAll(i1, i2, filter);
		filter.getChildren()
			  .add(filteredStudent);
		rootItem.getChildren()
				.add(Student.makeStudentItem("Straight"));
	}
	
	@FXML
	public void onAddSubFilter()
	{
		TreeItem<Student> currentItem = treeTable.getTreeItem(treeTable.getSelectionModel()
																	   .getFocusedIndex());
		if (currentItem.getValue()
					   .isFilter())
		{
			currentItem.getChildren()
					   .add(Student.makeFilterItem("New filter"));
		} else
		{
			new Dialog<>()
			{{
				setTitle("Can not do that!");
				setContentText("Selected item is not a filter - can not add sub-filter to student.");
				setOnCloseRequest(event -> close());
				getDialogPane().getButtonTypes()
							   .add(new ButtonType("Ok", ButtonBar.ButtonData.YES));
				show();
			}};
		}
	}
	
	@FXML
	public void onAddStudent()
	{
		TreeItem<Student> currentItem = treeTable.getTreeItem(treeTable.getSelectionModel()
																	   .getFocusedIndex());
		if (currentItem.getValue()
					   .isFilter())
		{
			currentItem.getChildren()
					   .add(Student.makeStudentItem("New Student"));
		} else
		{
			new Dialog<>()
			{{
				setTitle("Can not do that!");
				setContentText("Selected item is not a filter - can not add student to student.");
				setOnCloseRequest(event -> close());
				getDialogPane().getButtonTypes()
							   .add(new ButtonType("Ok", ButtonBar.ButtonData.YES));
				show();
			}};
		}
	}
	
	@FXML
	public void onAddYear()
	{
		TextInputDialog pop = new TextInputDialog()
		{{
			setTitle("Add a year column");
			setContentText("This will create a school year column populated with sub-columns for months and days");
			setHeaderText("Enter only the start year here");
			setOnCloseRequest(event -> {
				close();
				System.out.println(event);
			});
			getDialogPane().lookupButton(ButtonType.OK)
						   .setOnMouseEntered(event -> setHalt(true));
		}};
		do
		{
			setHalt(false);
			pop.showAndWait();
			try
			{
				final int yearMebe = Integer.parseInt(pop.getResult());
				final MenuItem deletDis = new MenuItem("Delete " + Integer.toString(yearMebe) + " - " + Integer.toString(yearMebe + 1));
				treeTable.getColumns()
						 .add(new YearColumn(yearMebe, collapseMenu, showMenu)
						 {
							 @Override
							 public void initDestructor()
							 {
								 deletDis.setOnAction(event -> destroy());
								 delYearMenu.getItems()
											.add(deletDis);
							 }
					
							 @Override
							 public void destroy()
							 {
								 delYearMenu.getItems()
											.remove(deletDis);
								 treeTable.getColumns()
										  .remove(this);
							 }
						 });
				return;
			} catch (NumberFormatException e)
			{
				pop.getEditor()
				   .setStyle("-fx-background-color: rgb(255, 0, 0);");
			}
		} while (haltDialog);
	}
	
	private void setHalt(boolean val)
	{
		haltDialog = val;
	}
	
	@FXML
	private void onSave()
	{
		
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);
		
		try
		{
			table(document, page, 700, 50, writeNorm());
			document.save(pdfFile);
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		writeNorm().forEach(subList -> {
			subList.forEach(string -> System.out.print(string + " "));
			System.out.println();
		});
	}
	
	private static void table(
			PDDocument document, PDPage page, float y, float margin, List<List<String>> content) throws IOException
	{
		final PDPageContentStream contentStream = new PDPageContentStream(document, page);
		
		final int rows = content.size();
		final int cols = content.stream()
								.max(Comparator.comparingInt(List::size))
								.get()
								.size();
		final float rowHeight = 20f;
		final float tableWidth = page.getMediaBox()
									 .getWidth() - (2 * margin);
		final float tableHeight = rowHeight * rows;
		final float colWidth = tableWidth / (float) cols;
		final float cellMargin = 5f;
		//draw the rows
		float nexty = y;
		for (int i = 0; i <= rows; i++)
		{
			contentStream.drawLine(margin, nexty, margin + tableWidth, nexty);
			nexty -= rowHeight;
		}
		
		//draw the columns
		float nextx = margin;
		for (int i = 0; i <= cols; i++)
		{
			contentStream.drawLine(nextx, y, nextx, y - tableHeight);
			nextx += colWidth;
		}
		
		//now add the text
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 11);
		
		float textx = margin + cellMargin;
		float texty = y - 15;
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < content.get(i)
									   .size(); j++)
			{
				String text = content.get(i)
									 .get(j);
				contentStream.beginText();
				contentStream.moveTextPositionByAmount(textx, texty);
				contentStream.drawString(text);
				contentStream.endText();
				textx += colWidth;
			}
			texty -= rowHeight;
			textx = margin + cellMargin;
		}
		contentStream.close();
	}
	
	private List<List<String>> writeNorm()
	{
		List<List<String>> metaList = new ArrayList<>();
		for (Object item : treeTable.getRoot()
									.getChildren())
		{
			List<String> subList = new ArrayList<>();
			TreeItem<Student> studentItem = ((TreeItem<Student>) item);
			if (studentItem.getValue()
						   .isFilter())
			{
				subList.add(studentItem.getValue()
									   .getName());
				subList.add(studentItem.getValue()
									   .getSurname() == null
							? "N/A"
							: studentItem.getValue()
										 .getSurname());
				metaList.add(subList);
				writeNormChildren(metaList, studentItem, 1);
			} else
			{
				subList.add(studentItem.getValue()
									   .getName());
				subList.add((studentItem.getValue()
										.getSurname() == null
							 ? "N/A"
							 : studentItem.getValue()
										  .getSurname()));
				subList.addAll(niceDates(studentItem.getValue()));
				metaList.add(subList);
			}
		}
		return metaList;
	}
	
	private void writeNormChildren(List<List<String>> metaList, TreeItem<Student> root, int indent)
	{
		for (Object child : root.getChildren())
		{
			List<String> subList = new ArrayList<>();
			TreeItem<Student> studentItem = ((TreeItem<Student>) child);
			if (studentItem.getValue()
						   .isFilter())
			{
				
				subList.add(indents(indent) + studentItem.getValue()
														 .getName());
				subList.add(studentItem.getValue()
									   .getSurname() == null
							? "N/A"
							: studentItem.getValue()
										 .getSurname());
				
				metaList.add(subList);
				writeNormChildren(metaList, studentItem, indent + 1);
			} else
			{
				subList.add(indents(indent) + studentItem.getValue()
														 .getName());
				subList.add((studentItem.getValue()
										.getSurname() == null
							 ? "N/A"
							 : studentItem.getValue()
										  .getSurname()));
				subList.addAll(niceDates(studentItem.getValue()));
				metaList.add(subList);
			}
		}
	}
	
	private String indents(int n)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < n; ++i)
		{
			builder.append('|');
		}
		return builder.toString();
	}
	
	private List<String> niceDates(Student student)
	{
		List<String> dates = new ArrayList<>();
		treeTable.getColumns()
				 .filtered(column -> column instanceof YearColumn)
				 .forEach(column -> {
					 //builder.append("\n" + indents(indent) + ((YearColumn) column).getStarYear() + ":");
					 for (Integer date : student.getAbsentDates())
					 {
						 if (YearColumn.dateBelongsToStartYear(date, ((YearColumn) column).getStarYear()))
						 {
							 //builder.append(" " + (date % 10000) / 100 + ":" + (date % 10000) % 100);
							 dates.add(date.toString());
						 }
					 }
				 });
		return dates;
	}
}
