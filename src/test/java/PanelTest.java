import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import swing_extensions.FormPanel;
import swing_extensions.MyFrame;

public class PanelTest extends MyFrame {
	private final FormPanel panel1 = new FormPanel();
	private final FormPanel panel2 = new FormPanel();
	private final JComboBox<JPanel> panelChooser = new JComboBox<>(new JPanel[] { panel1, panel2 });

	PanelTest() {
		super("Panel Test");

		panel1.setName("nig1");
		panel2.setName("nig2");

		panel1.addFormRow("bruh", new JButton("nig"));
		panel1.addFormRow("bruh2", new JButton("nig2"));

		panel2.addFormRow("nig", new JButton("bruh"));

		final var mainPanel = new FormPanel();
		setContentPane(mainPanel);
		mainPanel.addFormRow("Choose Panel", panelChooser);

		panelChooser.addActionListener(__ -> {
			final var selectedPanel = (JPanel) panelChooser.getSelectedItem();
			final var c = new GridBagConstraints();
			c.gridwidth = 2;
			c.fill = GridBagConstraints.BOTH;
			c.gridy = 1;
			try {
				mainPanel.remove(2);
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			mainPanel.add(selectedPanel, c);
			validate();
			pack();
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new PanelTest();
	}
}
