package swing_extensions;

import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class SimpleListModel<T> extends Vector<T> implements ListModel<T> {
	@Override
	public int getSize() {
		return size();
	}

	@Override
	public T getElementAt(int index) {
		return get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		throw new UnsupportedOperationException("Unimplemented method 'addListDataListener'");
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		throw new UnsupportedOperationException("Unimplemented method 'removeListDataListener'");
	}
}
