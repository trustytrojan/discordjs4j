package swing_extensions;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridBagPanel extends JPanel {
	public static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	public static GridBagConstraints constraintsInsets5() {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		return c;
	}

	public GridBagPanel() {
		super(new GridBagLayout());
	}

	public void addFormRow(int row, String label, Component comp) {
		final var c = constraintsInsets5();
		c.gridy = row;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		add(new JLabel(label), c);

		c.gridx = 1;
		add(comp, c);
	}
}
