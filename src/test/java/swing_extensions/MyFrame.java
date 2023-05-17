package swing_extensions;

import javax.swing.JFrame;

public class MyFrame extends JFrame {
	public MyFrame(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			validate();
			pack();
		}
		
		super.setVisible(b);
	}
}
