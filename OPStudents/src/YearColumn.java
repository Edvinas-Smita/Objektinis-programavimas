import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTreeTableCell;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class YearColumn extends TreeTableColumn<Student, String> implements Destructible
{
	private List<TreeTableColumn<Student, String>> allMonths = new ArrayList<>();
	private int starYear;
	
	public int getStarYear()
	{
		return starYear;
	}
	
	public YearColumn(int startYear, Menu collapseMenu, Menu showMenu)
	{
		super(Integer.toString(startYear) + " - " + Integer.toString(startYear + 1));
		this.starYear = startYear;
		String[] monthNames = new DateFormatSymbols().getMonths();
		
		Menu collapseYear = new Menu("Collapse " + Integer.toString(startYear) + " - " + Integer.toString(startYear + 1));
		collapseMenu.getItems()
					.add(collapseYear);
		Menu showYear = new Menu("Show " + Integer.toString(startYear) + " - " + Integer.toString(startYear + 1));
		showMenu.getItems()
				.add(showYear);
		
		TreeTableColumn<Student, String> tempVar;
		for (int i = 0; i < 9; ++i)
		{
			collapseYear.getItems()
						.add(new CollapseByMonthMenu(monthNames[(8 + i) % 12], this));
			showYear.getItems()
					.add(new ShowByMonthMenu(monthNames[(8 + i) % 12], this));
			
			tempVar = new TreeTableColumn<>(monthNames[(8 + i) % 12]);
			allMonths.add(tempVar);
			for (int j = 0; j < daysInMonth(startYear, 8 + i); ++j)
			{
				tempVar.getColumns()
					   .add(dayColumn(10000 * (startYear + (8 + i) / 12) + 100 * ((8 + i) % 12 + 1) + (j + 1)));
			}
		}
		getColumns().setAll(allMonths);
		initDestructor();
	}
	
	private int daysInMonth(int year, int month)
	{
		return new GregorianCalendar(year + month / 12, month % 12, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	private TreeTableColumn<Student, String> dayColumn(int day)
	{
		TreeTableColumn<Student, String> dayClmn = new TreeTableColumn<>(Integer.toString(day % 100));
		dayClmn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()
																			.getValue()
																			.isFilter()
																	   ? ""
																	   : param.getValue()
																			  .getValue()
																			  .getAbsent(day)
																		 ? "n"
																		 : " "));
		dayClmn.setCellFactory(value -> new ComboBoxTreeTableCell<>(" ", "n")
		{
			@Override
			public void updateItem(String item, boolean empty)
			{
				super.updateItem(item, empty);
				if ("".equals(item))
				{
					setDisable(true);
				} else
				{
					setDisable(false);
				}
			}
		});
		
		dayClmn.setOnEditCommit(event -> {
			if (" ".equals(event.getNewValue()))
			{
				event.getRowValue()
					 .getValue()
					 .setAbsent(day, false);
			} else if ("n".equals(event.getNewValue()))
			{
				event.getRowValue()
					 .getValue()
					 .setAbsent(day, true);
			}
		});
		
		return dayClmn;
	}
	
	private void monthVis(String monthName, boolean visible)
	{
		for (TreeTableColumn<Student, String> month : allMonths)
		{
			if (month.getText()
					 .equals(monthName))
			{
				month.setVisible(visible);
				//month.setStyle("visibility: " + (visible ? "visible" : "hidden"));
				//month.getColumns().forEach(day -> day.setStyle("visibility: " + (visible ? "visible" : "hidden")));
			}
		}
	}
	
	class CollapseByMonthMenu extends MenuItem
	{
		public CollapseByMonthMenu(String monthName, YearColumn year)
		{
			super("Collapse " + monthName);
			setOnAction(event -> year.monthVis(monthName, false));
		}
	}
	
	class ShowByMonthMenu extends MenuItem
	{
		public ShowByMonthMenu(String monthName, YearColumn year)
		{
			super("Show " + monthName);
			setOnAction(event -> year.monthVis(monthName, true));
		}
	}
	
	public static boolean dateBelongsToStartYear(int date, int year)
	{
		return year * 10000 + 900 < date && date < year * 10000 + 10600;
	}
	public static int getStartYearOfDate(int date)
	{
		return dateBelongsToStartYear(date, date / 10000) ? date / 10000 : date / 10000 + 1;
	}
}