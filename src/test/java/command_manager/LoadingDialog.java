package command_manager;

import java.awt.Window;

import javax.swing.JProgressBar;

public class LoadingDialog extends MyDialog {	
	public LoadingDialog(Window owner) {
		super(owner, "Waiting for Discord...");
		final var progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		add(progressBar);
	}
}
