package command_manager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

final class SwingUtils {

	static MouseListener registerPopupMenu(Component comp, JPopupMenu popup) {
		return onRightClick(comp, (e) -> popup.show(comp, e.getX(), e.getY()));
	}

	static MouseListener onLeftClick(Component comp, Consumer<MouseEvent> c) {
		return onClick(comp, (e) -> {
			if (SwingUtilities.isLeftMouseButton(e))
				c.accept(e);
		});
	}

	static MouseListener onRightClick(Component comp, Consumer<MouseEvent> c) {
		return onClick(comp, (e) -> {
			if (SwingUtilities.isRightMouseButton(e))
				c.accept(e);
		});
	}

	static MouseListener onClick(Component comp, Consumer<MouseEvent> c) {
		final var ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				c.accept(e);
			}
		};
		comp.addMouseListener(ml);
		return ml;
	}

	static ActionListener onAction(AbstractButton comp, Consumer<ActionEvent> c) {
		final var al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.accept(e);
			}
		};
		comp.addActionListener(al);
		return al;
	}

	@SuppressWarnings("rawtypes")
	static ActionListener onAction(JComboBox comp, Consumer<ActionEvent> c) {
		final var al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				c.accept(e);
			}
		};
		comp.addActionListener(al);
		return al;
	}

}
