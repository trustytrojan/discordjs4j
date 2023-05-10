package swing_extensions;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

abstract class MyList<T> extends JList<T> implements ListCellRenderer<T> {
	private final DefaultListModel<T> model = new DefaultListModel<>();

	MyList() {
		setModel(model);
		setCellRenderer(this);
	}

	void addElement(final T element) {
		model.addElement(element);
	}

	void removeElement(final int index) {
		model.removeElementAt(index);
	}

	void clear() {
		model.clear();
	}
}
