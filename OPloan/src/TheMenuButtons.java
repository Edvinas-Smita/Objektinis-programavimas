public class TheMenuButtons extends javax.swing.JToggleButton implements Resizeable {
	private double partOfWidth, partOfHeight;
	private int frameWidth, frameHeight;
	private int index;
	private TheFrame frame;
	
	TheMenuButtons(TheFrame frame, String text, double partOfWidth, double partOfHeight, int index, HasAction action) {
		super(text, false);
		this.frame = frame;
		this.partOfWidth = partOfWidth;
		this.partOfHeight = partOfHeight;
		this.frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
		this.frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
		this.index = index;
		setBounds((int) (index * frameWidth * partOfWidth), 0, (int) (frameWidth * partOfWidth), (int) (frameHeight * partOfHeight));
		setVisible(true);
		frame.add(this);
		
		addActionListener(Action -> action.then());
		repaint();
	}
	
	public void resized() {
		setBounds((int) (index * (frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right) * partOfWidth), 0, (int) (frameWidth * partOfWidth), (int) ((frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom) * partOfHeight));
	}
}
