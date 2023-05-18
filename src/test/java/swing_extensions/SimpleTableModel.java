package swing_extensions;

import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SimpleTableModel extends Vector<Vector<Object>> implements TableModel {
	private Class<?>[] columnTypes;
	private String[] columnNames;
	private int columnCount;

	public void setColumnCount(int count) {
		columnCount = count;
	}

	public void setColumnTypes(Class<?>... types) {
		if (types.length != columnCount)
			throw new RuntimeException();
		columnTypes = types;
	}

	public void setColumnNames(String... names) {
		if (names.length != columnCount)
			throw new RuntimeException();
		columnNames = names;
	}

	@Override
	public int getRowCount() {
		return size();
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return get(rowIndex).get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		get(rowIndex).set(columnIndex, aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		throw new UnsupportedOperationException("Unimplemented method 'addTableModelListener'");
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		throw new UnsupportedOperationException("Unimplemented method 'removeTableModelListener'");
	}
}
