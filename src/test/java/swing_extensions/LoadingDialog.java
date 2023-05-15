package swing_extensions;

import java.awt.Window;

import javax.swing.JProgressBar;

public class LoadingDialog extends MyDialog {	
	public LoadingDialog(Window owner, String title) {
		super(owner, title);
		final var progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		setContentPane(progressBar);
	}
}
