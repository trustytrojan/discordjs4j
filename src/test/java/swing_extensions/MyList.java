package swing_extensions;

import java.util.Collection;

import javax.swing.JList;

public abstract class MyList<T> extends JList<T> {
	public final SimpleListModel<T> model = new SimpleListModel<>();

	public MyList() {
		setModel(model);
	}

	public MyList(Collection<? extends T> elements) {
		this();
		model.addAll(elements);
	}
}
