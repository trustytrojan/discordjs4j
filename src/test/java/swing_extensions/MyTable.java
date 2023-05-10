package swing_extensions;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class MyTable extends JTable {
	private final DefaultTableModel model = new DefaultTableModel();
	private boolean editable;

	public MyTable(final String... columnNames) {
		super();
		setModel(model);
		model.setColumnIdentifiers(columnNames);
	}

	public void addRow(final Object... data) {
		model.addRow(data);
	}

	public void setRow(final int row, final Object... data) {
		for (int i = 0; i < data.length; ++i) {
			model.setValueAt(data[i], row, i);
		}
	}

	public void removeRow(final int index) {
		model.removeRow(index);
	}

	public void removeSelectedRow() {
		removeRow(getSelectedRow());
	}

	public void clear() {
		for (int i = getRowCount(); i >= 0; --i) {
			try {
				removeRow(i);
			} catch (final ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	public void setEditable(final boolean b) {
		editable = b;
	}

	public void setSortable(final boolean b) {
		setRowSorter(b ? new TableRowSorter<>(model) : null);
	}

	@SuppressWarnings("rawtypes")
	public Vector<Vector> rows() {
		return model.getDataVector();
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return editable;
	}
}
