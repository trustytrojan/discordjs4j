package command_manager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

class Swing {

	static final JFrame FRAME = new JFrame("DCM");
	static final DefaultTableModel TABLE_MODEL = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int _1, int _2) {
			return false;
		}
	};
	static final JTable TABLE = new JTable(TABLE_MODEL);
	static final JScrollPane SCROLL_PANE = new JScrollPane(TABLE);

	static {
		TABLE_MODEL.setColumnIdentifiers(new String[] { "ID", "Name", "Description" });

		TABLE_MODEL.addRow(new String[] {"nig1", "nig2", "nig3"});

		final var popup = new JPopupMenu();

		final var edit = popup.add("Edit");
		SwingUtils.onAction(edit, (e) -> {
			JOptionPane.showMessageDialog(edit, "Edited");
		});

		final var delete = popup.add("Delete");
		SwingUtils.onAction(delete, (e) -> {
			JOptionPane.showMessageDialog(delete, "Deleted");
		});

		SwingUtils.registerPopupMenu(TABLE, popup);

		FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FRAME.add(SCROLL_PANE);
		FRAME.validate();
		FRAME.pack();
		FRAME.setVisible(true);
	}

	public static void main(String[] __) {
	}

}
