public class TheSelector extends javax.swing.JRadioButton implements Resizeable {
	private double partOfWidth, partOfHeight, offsetX, offsetY;
	private int frameWidth, frameHeight;
	private TheFrame frame;
	
	TheSelector(TheFrame frame, String label, double partOfWidth, double partOfHeight, double offsetX, double offsetY, HasAction action) {
		super(label, false);
		this.frame = frame;
		this.partOfWidth = partOfWidth;
		this.partOfHeight = partOfHeight;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
		this.frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
		int width = (int) (frameWidth * partOfWidth), height = (int) (frameHeight * partOfHeight);
		
		
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
		setVisible(true);
		
		frame.add(this);
		
		addActionListener(Action -> action.then());
		repaint();
	}
	
	public void setVis(boolean visible) {
		setVisible(visible);
	}
	
	public void resized() {
		int width = (int) ((frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right) * partOfWidth), height = (int) ((frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom) * partOfHeight);
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
	}
}
