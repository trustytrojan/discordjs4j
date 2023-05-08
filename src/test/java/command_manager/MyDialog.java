package command_manager;

import java.awt.Window;

import javax.swing.JDialog;

public class MyDialog extends JDialog {
	private final Window owner;

	public MyDialog(final Window owner, final String title) {
		super(owner, title);
		this.owner = owner;
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			pack();
			setLocationRelativeTo(owner);
			super.setVisible(true);
		} else {
			super.setVisible(false);
		}
	}
}
