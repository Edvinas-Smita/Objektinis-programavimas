import javax.swing.*;

public class TheBigOutputField extends JScrollPane implements Resizeable {
	private double partOfWidth, partOfHeight, offsetX, offsetY;
	private int frameWidth, frameHeight;
	private TheFrame frame;
	
	private JTextArea textArea = new JTextArea();
	
	TheBigOutputField(TheFrame frame, double partOfWidth, double partOfHeight, double offsetX, double offsetY) {
		this.frame = frame;
		this.partOfWidth = partOfWidth;
		this.partOfHeight = partOfHeight;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
		this.frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
		int width = (int) (frameWidth * partOfWidth), height = (int) (frameHeight * partOfHeight);
		
		textArea.setEditable(false);
		
		getViewport().add(textArea);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
		setVisible(false);
		
		frame.add(this);
		
		repaint();
	}
	
	public void setVis(boolean visible) {
		setVisible(visible);
	}
	
	public void resized() {
		int width = (int) ((frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right) * partOfWidth), height = (int) ((frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom) * partOfHeight);
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}
}
