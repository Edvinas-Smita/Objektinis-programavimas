import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

@FunctionalInterface
interface HasAction {
	void then();
}

interface Resizeable {
	void resized();
}

public class TheFrame extends JFrame {
	private boolean inputVis = true, outputVis = false, graphVis = false, badParams = false, annuityOrLinear = false;
	private TheMenuButtons inputSelect, outputSelect, graphSelect, saveSelect;
	private TheInputField loanSize, time, interestRate, fromMonth, toMonth;
	private TheSlider timeSlider;
	private TheSelector linear, annuity;
	
	private TheBigOutputField bigOutput;
	
	private LikeAnActualGraph theGraph;
	
	TheFrame() {
		super("Mortgage calculator");
		setSize(600, 480);
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	TheFrame(int width, int height) {
		super("Mortgage calculator");
		setSize(width, height);
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		final TheFrame frame = new TheFrame();
		frame.populate();
		frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				frame.resized();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			
			}
			
			@Override
			public void componentShown(ComponentEvent e) {
			
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			
			}
		});
		
		frame.validate();
	}
	
	private void populate() {
		
		inputSelect = new TheMenuButtons(this, "Input", 0.25, 0.25, 0, () -> inputPressed()) {{
			setSelected(true);
		}};
		outputSelect = new TheMenuButtons(this, "Output", 0.25, 0.25, 1, () -> outputPressed());
		graphSelect = new TheMenuButtons(this, "Graph", 0.25, 0.25, 2, () -> graphPressed());
		saveSelect = new TheMenuButtons(this, "Save to file...", 0.25, 0.25, 3, () -> savePressed());
		
		
		loanSize = new TheInputField(this, "Loan size:", 0.5, 0.25, 0, 0.25, () -> loanSizeUpdated()) {{
			doubleValue = 10000;
			setText(Double.toString(doubleValue));
		}};
		
		interestRate = new TheInputField(this, "Interest rate:", 0.5, 0.25, 0.5, 0.25, () -> interestRateUpdated()) {{
			doubleValue = 1;
			setText(Double.toString(doubleValue));
		}};
		
		
		timeSlider = new TheSlider(this, "Loan time (in months):", 0.75, 0.25, 0, 0.5, () -> timeSliderUpdated());
		
		time = new TheInputField(this, "Custom time:", 0.25, 0.25, 0.75, 0.5, () -> timeUpdated()) {{
			intValue = 36;
			setText(Integer.toString(intValue));
		}};
		
		fromMonth = new TheInputField(this, "From month:", 0.375, 0.25, 0, 0.75, () -> fromUpdated()) {{
			intValue = 1;
			setText(Integer.toString(intValue));
		}};
		toMonth = new TheInputField(this, "To month:", 0.375, 0.25, 0.375, 0.75, () -> toUpdated()) {{
			intValue = 36;
			setText(Integer.toString(intValue));
		}};
		
		
		linear = new TheSelector(this, "Linear", 0.25, 0.125, 0.75, 0.75, () -> linearSelected()) {{
			setSelected(true);
		}};
		annuity = new TheSelector(this, "Annuity:", 0.25, 0.125, 0.75, 0.875, () -> annuitySelected());
		
		
		bigOutput = new TheBigOutputField(this, 1, 0.75, 0, 0.25);
		
		
		theGraph = new LikeAnActualGraph(this, 1, 0.75, 0, 0.25);
		timeSlider.setValue(time.intValue);
		repaint();
	}
	
	private void resized() {
		java.util.stream.Stream.of(getContentPane().getComponents())
				.filter(component -> component instanceof Resizeable)
				.forEach(resizeable -> ((Resizeable) resizeable).resized());
	}
	
	private void toggleInputs(boolean vis) {
		if (inputVis != vis) {
			loanSize.setVis(vis);
			interestRate.setVis(vis);
			timeSlider.setVis(vis);
			time.setVis(vis);
			fromMonth.setVis(vis);
			toMonth.setVis(vis);
			linear.setVis(vis);
			annuity.setVis(vis);
			inputVis = vis;
		}
	}
	
	private void toggleOutputs(boolean vis) {
		if (outputVis != vis) {
			bigOutput.setVis(vis);
			outputVis = vis;
		}
	}
	
	private void toggleGraph(boolean vis) {
		if (graphVis != vis) {
			theGraph.setVis(vis);
			graphVis = vis;
		}
	}
	
	
	private void inputPressed() {
		if (inputSelect.isSelected()) {
			toggleInputs(true);
			toggleOutputs(false);
			toggleGraph(false);
			
			outputSelect.setSelected(false);
			graphSelect.setSelected(false);
		} else {
			inputSelect.setSelected(true);
		}
	}
	
	private void outputPressed() {
		if (outputSelect.isSelected()) {
			badParams = false;
			loanSizeUpdated();
			timeUpdated();
			interestRateUpdated();
			if (!badParams) {
				bigOutput.getTextArea().setText(annuityOrLinear ? annuityString() : linearString());
				
				toggleInputs(false);
				toggleOutputs(true);
				toggleGraph(false);
				
				inputSelect.setSelected(false);
				graphSelect.setSelected(false);
			} else {
				outputSelect.setSelected(false);
			}
		} else {
			outputSelect.setSelected(true);
		}
	}
	
	private void graphPressed() {
		if (graphSelect.isSelected()) {
			badParams = false;
			loanSizeUpdated();
			timeUpdated();
			interestRateUpdated();
			if (!badParams) {
				theGraph.removeAllLines();
				
				if (annuityOrLinear) {
					theGraph.addLine(annuityBases());
					theGraph.addLine(annuityInterests());
				} else {
					theGraph.addLine(linearInterests());
					theGraph.addLine(linearTotals());
				}
				
				toggleInputs(false);
				toggleOutputs(false);
				toggleGraph(true);
				
				inputSelect.setSelected(false);
				outputSelect.setSelected(false);
			} else {
				graphSelect.setSelected(false);
			}
		} else {
			graphSelect.setSelected(true);
		}
	}
	
	private void savePressed() {
		badParams = false;
		loanSizeUpdated();
		timeUpdated();
		interestRateUpdated();
		saveSelect.setSelected(false);
		if (!badParams) {
			if (annuityOrLinear) {
				new TheSaver(annuityString());
			}
			else {
				new TheSaver(linearString());
			}
		}
	}
	
	
	private void loanSizeUpdated() {
		if (!loanSize.parseDouble()) {
			badParams = true;
			return;
		}
		if (loanSize.doubleValue < 0) {
			loanSize.setText("0");
			loanSize.doubleValue = 1;
		}
	}
	
	private void timeUpdated() {
		if (!time.parseInteger()) {
			badParams = true;
			return;
		}
		if (time.intValue < 1) {
			time.setText("1");
			time.intValue = 1;
		}
		if (time.intValue < timeSlider.getMaximum() + 1) {
			timeSlider.setValue(time.intValue);
		}
		toUpdated();
	}
	
	private void interestRateUpdated() {
		if (!interestRate.parseDouble()) {
			badParams = true;
		}
	}
	
	private void timeSliderUpdated() {
		if (timeSlider.getValue() == 0) {
			timeSlider.setValue(1);
		}
		time.setText(Integer.toString(timeSlider.getValue()));
		timeUpdated();
	}
	
	private void fromUpdated() {
		if (!fromMonth.parseInteger()) {
			badParams = true;
			return;
		}
		if (fromMonth.intValue < 1) {
			fromMonth.setText("1");
			fromMonth.intValue = 1;
		}
		if (fromMonth.intValue > time.intValue) {
			fromMonth.setText(time.getText());
			fromMonth.intValue = time.intValue;
		}
		if (fromMonth.intValue > toMonth.intValue) {
			fromMonth.setText(toMonth.getText());
			fromMonth.intValue = toMonth.intValue;
		}
	}
	
	private void toUpdated() {
		if (!toMonth.parseInteger()) {
			badParams = true;
			return;
		}
		if (toMonth.intValue < 1) {
			toMonth.setText("1");
			toMonth.intValue = 1;
		}
		if (toMonth.intValue > time.intValue) {
			toMonth.setText(time.getText());
			toMonth.intValue = time.intValue;
		}
		if (fromMonth.intValue > toMonth.intValue) {
			toMonth.setText(fromMonth.getText());
			toMonth.intValue = fromMonth.intValue;
		}
		fromUpdated();
	}
	
	private void linearSelected() {
		if (!annuityOrLinear) {
			linear.setSelected(true);
		} else {
			annuity.setSelected(false);
			annuityOrLinear = false;
		}
	}
	
	private void annuitySelected() {
		if (annuityOrLinear) {
			annuity.setSelected(true);
		} else {
			linear.setSelected(false);
			annuityOrLinear = true;
		}
	}
	
	private static double calcAnnuityMonthly(double L, double c, int n){
		return L * ((c * Math.pow(1 + c, n)) / (Math.pow(1 + c, n) - 1));
	}
	private static double calcAnnuityRemainder(double L, double c, int n, int p){
		return L * ((Math.pow(1 + c, n) - Math.pow(1 + c, p)) / (Math.pow(1 + c, n) - 1));
	}
	
	private String annuityString() {
		String uberBig = "";
		
		double totalMonthly = calcAnnuityMonthly(loanSize.doubleValue, interestRate.doubleValue / 100, time.intValue), balanceDifference, oldLoan = loanSize.doubleValue, newLoan;
		for (int i = 0; i < time.intValue; ++i) {
			newLoan = calcAnnuityRemainder(loanSize.doubleValue, interestRate.doubleValue / 100, time.intValue, i + 1);
			balanceDifference = oldLoan - newLoan;
			oldLoan = newLoan;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				uberBig += "Left to pay after " + (i + 1) + " mon.: " + String.format("%.2f", newLoan)
						           + ";\tBase pay: " + String.format("%.2f", balanceDifference)
						           + ";\tInterest: " + String.format("%.2f", totalMonthly - balanceDifference)
						           + ";\tTotal pay: " + String.format("%.2f", totalMonthly) + "\r\n";
			}
		}
		
		return uberBig + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\r\n"
				       + "Complete payment: " + String.format("%.2f", totalMonthly * time.intValue) + "\r\n";
	}
	private double[] annuityBases() {
		double[] values = new double[toMonth.intValue - fromMonth.intValue + 1];
		int recordedValues = 0;
		
		double balanceDifference, oldLoan = loanSize.doubleValue, newLoan;
		for (int i = 0; i < time.intValue; ++i) {
			newLoan = calcAnnuityRemainder(loanSize.doubleValue, interestRate.doubleValue / 100, time.intValue, i + 1);
			balanceDifference = oldLoan - newLoan;
			oldLoan = newLoan;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				values[recordedValues++] = balanceDifference;
			}
		}
		return values;
	}
	private double[] annuityInterests() {
		double[] values = new double[toMonth.intValue - fromMonth.intValue + 1];
		int recordedValues = 0;
		
		double totalMonthly = calcAnnuityMonthly(loanSize.doubleValue, interestRate.doubleValue / 100, time.intValue), balanceDifference, oldLoan = loanSize.doubleValue, newLoan;
		for (int i = 0; i < time.intValue; ++i) {
			newLoan = calcAnnuityRemainder(loanSize.doubleValue, interestRate.doubleValue / 100, time.intValue, i + 1);
			balanceDifference = oldLoan - newLoan;
			oldLoan = newLoan;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				values[recordedValues++] = totalMonthly - balanceDifference;
			}
		}
		return values;
	}
	
	private String linearString() {
		String uberBig = "";
		
		double veryBase = loanSize.doubleValue / time.intValue, dePercented = interestRate.doubleValue / 100, monthsInterest, totalInterest = 0, newLoan = loanSize.doubleValue;
		for (int i = 0; i < time.intValue; ++i) {
			totalInterest += monthsInterest = newLoan * dePercented;
			newLoan -= veryBase;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				uberBig += "Left to pay after " + (i + 1) + " mon.: " + String.format("%.2f", newLoan)
						           + ";\tBase pay: " + String.format("%.2f", veryBase)
						           + ";\tInterest: " + String.format("%.2f", monthsInterest)
						           + ";\tTotal pay: " + String.format("%.2f", veryBase + monthsInterest) + "\r\n";
			}
		}
		
		return uberBig + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\r\n"
				       + "Complete payment: " + String.format("%.2f", loanSize.doubleValue + totalInterest) + "\r\n";
	}
	private double[] linearInterests() {
		double[] values = new double[toMonth.intValue - fromMonth.intValue + 1];
		int recordedValues = 0;
		
		double veryBase = loanSize.doubleValue / time.intValue, dePercented = interestRate.doubleValue / 100, monthsInterest, newLoan = loanSize.doubleValue;
		for (int i = 0; i < time.intValue; ++i) {
			monthsInterest = newLoan * dePercented;
			newLoan -= veryBase;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				values[recordedValues++] = monthsInterest;
			}
		}
		return values;
	}
	private double[] linearTotals() {
		double[] values = new double[toMonth.intValue - fromMonth.intValue + 1];
		int recordedValues = 0;
		
		double veryBase = loanSize.doubleValue / time.intValue, dePercented = interestRate.doubleValue / 100, monthsInterest, newLoan = loanSize.doubleValue;
		for (int i = 0; i < time.intValue; ++i) {
			monthsInterest = newLoan * dePercented;
			newLoan -= veryBase;
			if (i + 1 >= fromMonth.intValue && i + 1 <= toMonth.intValue) {
				values[recordedValues++] = veryBase + monthsInterest;
			}
		}
		return values;
	}
}
