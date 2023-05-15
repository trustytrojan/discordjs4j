package swing_extensions;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class MyTable extends JTable {
	private final DefaultTableModel model = new DefaultTableModel();
	private boolean editable;

	public MyTable(String... columnNames) {
		super();
		setModel(model);
		model.setColumnIdentifiers(columnNames);
	}

	public void addRow(Object... data) {
		model.addRow(data);
	}

	@SuppressWarnings("unchecked")
	public void setRow(int index, Object... data) {
		final var row = rows().get(index);
		final var columnCount = row.size();
		for (int i = 0; i < columnCount; ++i) {
			row.set(i, data[i]);
		}
	}

	public void removeRow(int index) {
		model.removeRow(index);
	}

	public void removeSelectedRow() {
		removeRow(getSelectedRow());
	}

	@SuppressWarnings("rawtypes")
	public Vector getSelectedRowData() {
		return rows().get(getSelectedRow());
	}

	public void clear() {
		model.setRowCount(0);
	}

	public void setEditable(boolean b) {
		editable = b;
	}

	public void setSortable(boolean b) {
		setRowSorter(b ? new TableRowSorter<>(model) : null);
	}

	@SuppressWarnings("rawtypes")
	public Vector<Vector> rows() {
		return model.getDataVector();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return editable;
	}
}
