import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
	private String given = "https://uk.kuehne-nagel.com/fileadmin/country_page_structure/WE/UK/European_Postcode_Map.pdf";
	private String test = "https://geology.com/google-earth/google-earth.jpg";
	private URL givenURL = null;
	private BufferedImage image = null;
	
	private static Point defaultViewPortSize = new Point(598, 449);
	private Rectangle mapRect = new Rectangle();
	private JPanel paintablePanel = null;
	
	private List<Pin> allPins = new ArrayList<>();
	
	@FXML
	private Pane mapPane;
	@FXML
	private SwingNode swingNode;
	@FXML
	private ScrollPane swingAnchor;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		URL testUrl = null;
		try
		{
			givenURL = new URL(given);
			testUrl = new URL(test);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}
		mapPane.setStyle("-fx-background-color: aqua; -fx-border-color: black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-image: url(" + test + ");");
		
		try
		{
			image = ImageIO.read(testUrl);
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		mapRect.setRect(0, 0, defaultViewPortSize.getX(), defaultViewPortSize.getY());
		paintablePanel = new JPanel()
		{
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				paintMapRect(g);
			}
			
			{
				setVisible(true);
				setLayout(null);
				setSize((int) defaultViewPortSize.getX(), (int) defaultViewPortSize.getY());
			}
		};
		swingAnchor.setOnScroll(event -> {
			resizeMapRect(event.getDeltaY());
			paintablePanel.repaint();
		});
		swingNode.setContent(paintablePanel);
		
		
		
		ContextMenu contextMenu = new ContextMenu(new MenuItem("Add point here")
		{{
			setOnAction(action -> {
				final int mapPaneXOffset = 8, mapPaneYOffset = 60;
				final Point mouseLocGlobal = MouseInfo.getPointerInfo()
													  .getLocation();
				final double mouseOnPaneX = mouseLocGlobal.getX() - swingAnchor.getScene()
																			   .getWindow()
																			   .getX() - mapPaneXOffset;
				final double mouseOnPaneY = mouseLocGlobal.getY() - swingAnchor.getScene()
																			   .getWindow()
																			   .getY() - mapPaneYOffset;
				final Point mouseLocMapPane = new Point((int) mouseOnPaneX, (int) mouseOnPaneY);
				final Point translatedPoint = new Point((int) ((mouseOnPaneX - mapRect.getX()) * defaultViewPortSize.getX() / mapRect.getWidth()), (int) ((mouseOnPaneY - mapRect.getY()) * defaultViewPortSize.getY() / mapRect.getHeight()));
				
				
				System.out.println("add point " + mouseLocMapPane + " ==> " + translatedPoint);
				allPins.add(new Pin(translatedPoint, "TEST"));
				paintablePanel.repaint();
			});
		}}, new MenuItem("Reset zoom")
		{{
			setOnAction(action -> {
				mapRect.setRect(0, 0, defaultViewPortSize.getX(), defaultViewPortSize.getY());
				paintablePanel.repaint();
			});
		}}, new MenuItem("test")
		{{
			setOnAction(action -> {
				mapRect.setRect(-defaultViewPortSize.getX(), 0, defaultViewPortSize.getX() * 2, defaultViewPortSize.getY() * 2);
				paintablePanel.repaint();
			});
		}});
		swingAnchor.setContextMenu(contextMenu);
		
		paintablePanel.repaint();
	}
	
	public void repaint()
	{
		paintablePanel.repaint();
		System.out.println("Pl0x");
	}
/*	@FXML
	private void mapContextPlease()
	{
		
		for (Pin pin : allPins)
		{
			if (translatedPoint.distanceSq(pin) < 9)
			{
				contextMenu.getItems()
						   .add(new MenuItem("View nearby pins")
						   {{
							   setOnAction(action -> {
								   System.out.println("view");
							   });
						   }});
				break;
			}
		}
	}*/
	
	private void paintMapRect(Graphics g)
	{
		g.drawImage(image, (int) mapRect.getX(), (int) mapRect.getY(), (int) mapRect.getWidth(), (int) mapRect.getHeight(), null);
		g.setColor(Color.RED);
		for (Pin pin : allPins)
		{
			System.out.println(pin + " => " + (pin.getX() * mapRect.getWidth() / defaultViewPortSize.getX() + mapRect.getX() - 2) + " " + (pin.getY() * mapRect.getHeight() / defaultViewPortSize.getY() + mapRect.getY() - 2));
			g.fillRect((int) (pin.getX() * mapRect.getWidth() / defaultViewPortSize.getX() + mapRect.getX() - 2), (int) (pin.getY() * mapRect.getHeight() / defaultViewPortSize.getY() + mapRect.getY() - 2), 5, 5);
		}
	}
	
	private void resizeMapRect(double delta)
	{
		System.out.println(mapRect + " " + delta);
		/*if (delta > 0 && (mapRect.getWidth() + delta > defaultViewPortSize.getX() || mapRect.getHeight() + delta > defaultViewPortSize.getY()))
		{
			mapRect.setRect(0, 0, defaultViewPortSize.getX(), defaultViewPortSize.getY());
			paintablePanel.repaint();
			return;
		}*/
		if (delta < 0 && (mapRect.getWidth() < delta || mapRect.getHeight() < delta))
		{
			return;
		}
		final int mapPaneXOffset = 8, mapPaneYOffset = 60;
		final Point mouseLocGlobal = MouseInfo.getPointerInfo()
											  .getLocation();
		final double mouseOnPaneX = mouseLocGlobal.getX() - swingAnchor.getScene()
																	   .getWindow()
																	   .getX() - mapPaneXOffset;
		final double mouseOnPaneY = mouseLocGlobal.getY() - swingAnchor.getScene()
																	   .getWindow()
																	   .getY() - mapPaneYOffset;
		mapRect.setRect(mapRect.getX() - delta * mouseOnPaneX / defaultViewPortSize.getX(), mapRect.getY() - delta * mouseOnPaneY / defaultViewPortSize.getY(), mapRect.getWidth() + delta, mapRect.getHeight() + delta);
	}
}
