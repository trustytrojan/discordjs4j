package swing_extensions;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

public final class SwingUtils {
	private SwingUtils() {}

	public static JButton button(String text, Runnable onClick) {
		final var b = new JButton(text);
		b.addActionListener(e -> onClick.run());
		return b;
	}

	public static JPanel tableWithHeaders(JTable table) {
		final var panel = new JPanel(new BorderLayout());
		table.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		panel.add(table.getTableHeader(), BorderLayout.NORTH);
		panel.add(table, BorderLayout.CENTER);
		return panel;
	}
}
