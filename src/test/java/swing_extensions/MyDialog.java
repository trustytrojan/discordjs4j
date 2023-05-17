package swing_extensions;

import java.awt.Window;

import javax.swing.JDialog;

public class MyDialog extends JDialog {
	public MyDialog(Window owner, String title) {
		super(owner, title);
	}

	public MyDialog(Window owner) {
		super(owner);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			setLocationRelativeTo(getOwner());
		}

		super.setVisible(b);
	}
}
