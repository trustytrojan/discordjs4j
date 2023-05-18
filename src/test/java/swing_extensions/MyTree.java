package swing_extensions;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

public class MyTree extends JTree {
	public final SimpleMutableTreeNode root = new SimpleMutableTreeNode();

	public MyTree() {
		setModel(new DefaultTreeModel(root));
	}
}
