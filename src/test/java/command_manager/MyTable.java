package command_manager;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class MyTable extends JTable {
	private final DefaultTableModel model = new DefaultTableModel();
	private boolean editable = false;
	private boolean selectable = false;

	MyTable(final String... columnNames) {
		super();
		setModel(model);
		model.setColumnIdentifiers(columnNames);
	}

	void addRow(final Object... data) {
		model.addRow(data);
	}

	void removeRow(final int index) {
		model.removeRow(index);
	}

	void removeSelectedRow() {
		removeRow(getSelectedRow());
	}

	void clear() {
		for (int i = getRowCount(); i >= 0; --i) {
			try {
				removeRow(i);
			} catch (final ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	void setEditable(boolean b) {
		editable = b;
	}

	void setSelectable(boolean b) {
		selectable = b;
	}

	void setSortable(boolean b) {
		setRowSorter(b ? new TableRowSorter<>(model) : null);
	}

	@SuppressWarnings("rawtypes")
	Iterable<Vector> rows() {
		return Util.iterableFrom(model.getDataVector().iterator());
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return editable;
	}

	@Override
	public boolean isCellSelected(int row, int col) {
		return selectable;
	}
}