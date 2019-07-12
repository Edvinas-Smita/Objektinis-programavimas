import javax.swing.*;

public class TheSlider extends JSlider implements Resizeable {
	private double partOfWidth, partOfHeight, offsetX, offsetY;
	private int frameWidth, frameHeight;
	private TheFrame frame;
	private JLabel ll;
	
	TheSlider(TheFrame frame, String label, double partOfWidth, double partOfHeight, double offsetX, double offsetY, HasAction action) {
		this.frame = frame;
		this.partOfWidth = partOfWidth;
		this.partOfHeight = partOfHeight;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
		this.frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
		int width = (int) (frameWidth * partOfWidth), height = (int) (frameHeight * partOfHeight);
		
		ll = new JLabel(label);
		ll.setLabelFor(this);
		ll.setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height / 2);
		ll.setVisible(true);
		
		setMinimum(0);
		setMaximum(120);
		setMinorTickSpacing(6);
		setMajorTickSpacing(12);
		setPaintTicks(true);
		setPaintLabels(true);
		setValue(1);
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight + height / 2), width, height / 2);
		setVisible(true);
		
		
		frame.add(ll);
		frame.add(this);
		
		addChangeListener(Action -> action.then());
		ll.repaint();
		repaint();
	}
	
	public void setVis(boolean visible) {
		ll.setVisible(visible);
		setVisible(visible);
	}
	
	public void resized() {
		int width = (int) ((frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right) * partOfWidth), height = (int) ((frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom) * partOfHeight);
		ll.setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height / 2);
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight + height / 2), width, height / 2);
	}
}
