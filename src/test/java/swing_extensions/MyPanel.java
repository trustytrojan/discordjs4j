package swing_extensions;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class MyPanel extends JPanel {
	public MyPanel(LayoutManager lm) {
		super(lm);
	}

	public void setComponent(int index, Component comp) {
		try {
			remove(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		add(comp, index);
	}
}
