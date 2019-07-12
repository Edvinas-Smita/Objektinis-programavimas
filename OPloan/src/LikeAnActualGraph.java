import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LikeAnActualGraph extends JPanel implements Resizeable {
	private double partOfWidth, partOfHeight, offsetX, offsetY, borderPart = 0.05;
	private int frameWidth, frameHeight, width, height;
	private TheFrame frame;
	private JPanel innerPanel = new JPanel() {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			refreshDims();
			setBackground(allLines.isEmpty() ? Color.RED : frame.getBackground());
			g.setColor(Color.RED);
			g.setPaintMode();
			for (GraphLine line : allLines) {
				g.drawPolyline(line.getXPoints(), line.getYPoints(), line.getPointCount());
			}
		}
	};
	private JLabel minValLabel = new JLabel(), maxValLabel = new JLabel(), minCountLabel = new JLabel("0"), maxCountLabel = new JLabel();
	private List<GraphLine> allLines = new ArrayList<>();
	
	LikeAnActualGraph(TheFrame frame, double partOfWidth, double partOfHeight, double offsetX, double offsetY) {
		this.frame = frame;
		this.partOfWidth = partOfWidth;
		this.partOfHeight = partOfHeight;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
		this.frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
		refreshDims();
		
		innerPanel.setBounds((int) (borderPart * width), (int) (borderPart * height), (int) ((1 - 2 * borderPart) * width), (int) ((1 - 2 * borderPart) * height));
		innerPanel.setVisible(true);
		innerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		innerPanel.setLayout(null);
		
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
		setVisible(false);
		setLayout(null);
		
		minValLabel.setVisible(true);
		maxValLabel.setVisible(true);
		minCountLabel.setVisible(true);
		maxCountLabel.setVisible(true);
		
		add(innerPanel);
		add(minValLabel);
		add(maxValLabel);
		add(minCountLabel);
		add(maxCountLabel);
		
		frame.add(this);
	}
	
	public void setVis(boolean visible) {
		setVisible(visible);
	}
	
	public void resized() {
		refreshDims();
		setBounds((int) (offsetX * frameWidth), (int) (offsetY * frameHeight), width, height);
		innerPanel.setBounds((int) (borderPart * width), (int) (borderPart * height), (int) ((1 - 2 * borderPart) * width), (int) ((1 - 2 * borderPart) * height));
		
		minValLabel.setBounds(0, (int) ((1 - borderPart) * height) - 9, minValLabel.getText().length() * 7, 9);
		maxValLabel.setBounds(0, (int) (borderPart * height), maxValLabel.getText().length() * 7, 9);
		minCountLabel.setBounds((int) (borderPart * width), (int) ((1 - borderPart) * height), minCountLabel.getText().length() * 7, 9);
		maxCountLabel.setBounds((int) ((1 - borderPart) * width - maxCountLabel.getText().length() * 7), (int) ((1 - borderPart) * height), maxCountLabel.getText().length() * 7, 9);
		
		for (GraphLine line : allLines) {
			line.transformByViewPortSize(innerPanel.getWidth(), innerPanel.getHeight());
		}
	}
	
	private void refreshDims() {
		width = (int) ((frameWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right) * partOfWidth);
		height = (int) ((frameHeight = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom) * partOfHeight);
	}
	
	public GraphLine createLine(double[] values) {
		return new GraphLine(values, innerPanel.getWidth(), innerPanel.getHeight());
	}
	
	public void addLine(double[] values) {
		addLine(createLine(values));
	}
	
	public void addLine(GraphLine line) {
		double graphMin = line.getMinValue();
		double graphMax = line.getMaxValue();
		int graphValCount = line.getValueCount();
		boolean transformByAmps = false, transformByVals = false;
		for (GraphLine lineInList : allLines) {
			if (graphMin > lineInList.getMinValue()) {
				graphMin = lineInList.getMinValue();
				transformByAmps = true;
			}
			if (graphMax < lineInList.getMaxValue()) {
				graphMax = lineInList.getMaxValue();
				transformByAmps = true;
			}
			if (graphValCount < lineInList.getValueCount()) {
				graphValCount = lineInList.getValueCount();
				transformByVals = true;
			}
		}
		for (GraphLine lineInList : allLines) {
			if (transformByAmps && (graphMin != lineInList.getMinValue() || graphMax != lineInList.getMaxValue())) {
				lineInList.transformByAmplitude(graphMin, graphMax);
			}
			if (transformByVals && graphValCount != lineInList.getValueCount()) {
				lineInList.transformByValueCount(graphValCount);
			}
		}
		
		if (transformByAmps && (graphMin != line.getMinValue() || graphMax != line.getMaxValue())) {
			line.transformByAmplitude(graphMin, graphMax);
		}
		if (transformByVals && graphValCount != line.getValueCount()) {
			line.transformByValueCount(graphValCount);
		}
		allLines.add(line);
		
		minValLabel.setText(String.format("%.0f", graphMin));
		maxValLabel.setText(String.format("%.0f", graphMax));
		maxCountLabel.setText(Integer.toString(graphValCount));
		
		minValLabel.setBounds(0, (int) ((1 - borderPart) * height) - 9, minValLabel.getText().length() * 7, 9);
		maxValLabel.setBounds(0, (int) (borderPart * height), maxValLabel.getText().length() * 7, 9);
		minCountLabel.setBounds((int) (borderPart * width), (int) ((1 - borderPart) * height), minCountLabel.getText().length() * 7, 9);
		maxCountLabel.setBounds((int) ((1 - borderPart) * width - maxCountLabel.getText().length() * 7), (int) ((1 - borderPart) * height), maxCountLabel.getText().length() * 7, 9);
	}
	
	public void removeAllLines() {
		allLines.clear();
	}
}

class GraphLine {
	private double minValue, maxValue, amplitude;
	private int valueCount, pointCount, width, height;
	
	private int[] xPoints, yPoints;
	private double allValues[];
	
	GraphLine(double[] values, int viewPortWidth, int viewPortHeight) {
		allValues = values;
		pointCount = -2 + 2 * (valueCount = values.length);
		xPoints = new int[pointCount];
		yPoints = new int[pointCount];
		
		minValue = values[0];
		maxValue = values[0];
		for (int i = 1; i < values.length; ++i) {
			if (values[i] < minValue) minValue = values[i];
			if (values[i] > maxValue) maxValue = values[i];
		}
		amplitude = maxValue - minValue;
		
		transformByViewPortSize(viewPortWidth, viewPortHeight);
	}
	
	private void determineXValues() {
		for (int i = 0; i < valueCount - 1; ++i) {
			xPoints[2 * i] = (int) (i * width / (valueCount - 1));
			xPoints[2 * i + 1] = (int) ((i + 1) * width / (valueCount - 1));
		}
	}
	
	private void determineYValues() {
		for (int i = 0; i < valueCount - 1; ++i) {
			yPoints[2 * i] = (int) (height - (allValues[i] - minValue) * height / amplitude);
			yPoints[2 * i + 1] = (int) (height - (allValues[i + 1] - minValue) * height / amplitude);
			//System.out.println("[" + i + "] ~ " + String.format("%.0f", allValues[i]) + "-" + allValues[i + 1] + " ::: (" + xPoints[2 * i] + ";" + yPoints[2 * i] + ") ~~~ (" + xPoints[2 * i + 1] + ";" + yPoints[2 * i + 1] + ")");
		}
	}
	
	public void transformByViewPortSize(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;
		determineXValues();
		determineYValues();
	}
	
	public void transformByAmplitude(double newMin, double newMax) {
		minValue = newMin;
		maxValue = newMax;
		amplitude = maxValue - minValue;
		determineYValues();
	}
	
	public void transformByValueCount(int newValueCount) {
		pointCount = -2 + 2 * (valueCount = newValueCount);
		determineXValues();
	}
	
	
	public double getMinValue() {
		return minValue;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public int getValueCount() {
		return valueCount;
	}
	
	public int getPointCount() {
		return pointCount;
	}
	
	public int[] getXPoints() {
		return xPoints;
	}
	
	public int[] getYPoints() {
		return yPoints;
	}
}