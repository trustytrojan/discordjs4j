package command_manager;

import javax.swing.JTree;

final class MyTree extends JTree {
	MyTree(final String rootNodeName) {
		super(new MyTreeNode(rootNodeName));
	}

	MyTreeNode getRootNode() {
		return (MyTreeNode) getModel().getRoot();
	}
}
