package command_manager;

import javax.swing.tree.DefaultMutableTreeNode;

final class MyTreeNode extends DefaultMutableTreeNode {
	MyTreeNode(final String name) {
		super(name);
	}

	void addChild(final String name) {
		add(new MyTreeNode(name));
	}

	void addChildren(final String... names) {
		for (final var name : names) {
			addChild(name);
		}
	}
}
