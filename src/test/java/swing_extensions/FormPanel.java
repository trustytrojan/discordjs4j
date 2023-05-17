package swing_extensions;

import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class FormPanel extends GridBagPanel {
	private int height;

	public void addFormRow(String label, Component comp) {
		final var c = constraintsInsets5();
		c.gridy = height++;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		add(new JLabel(label), c);

		c.gridx = 1;
		add(comp, c);
	}

	public void addCheckBox(JCheckBox checkBox) {
		final var c = constraintsInsets5();
		c.gridy = height++;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		add(checkBox, c);
	}

	// should be the last method called
	public void addBottomButtons(JButton... buttons) {
		final var bottomButtons = new BottomButtonsPanel(buttons);
		final var c = constraintsInsets5();
		c.gridy = height;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		add(bottomButtons, c);
	}
}
