import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TheSaver extends JFileChooser {
	TheSaver(String stringToSave) {
		setVisible(true);
		setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
		setAcceptAllFileFilterUsed(false);
		int returnVal;
		do {
			returnVal = showSaveDialog(null);
			if (returnVal == APPROVE_OPTION) {
				File file = getSelectedFile();
				boolean continueSaving = false;
				System.out.println(file.getAbsolutePath() + " " + file.getName() + " " + file.getPath() + " " + file.length());
				if (file.exists()) {
					int overrideDialogVal = JOptionPane.showConfirmDialog(null, "File already exists. Do you wish to override it?");
					if (overrideDialogVal == JOptionPane.CANCEL_OPTION) {
						return;
					}
					continueSaving = overrideDialogVal == JOptionPane.OK_OPTION;
				} else if (file.getName().endsWith(".txt")) {
					file = new File(file.getParent(), file.getName() + ".txt");
					continueSaving = true;
				}
				
				if (continueSaving) {
					try {
						new FileWriter(file) {{
							write(stringToSave);
							close();
						}};
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		} while (returnVal != CANCEL_OPTION && returnVal != ERROR_OPTION);
	}
}
