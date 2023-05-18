package swing_extensions;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class SimpleMutableTreeNode implements MutableTreeNode {
	private final Vector<MutableTreeNode> children = new Vector<>();
	private MutableTreeNode parent;
	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(Objects.requireNonNull(node));
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return children.isEmpty();
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return children.elements();
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		children.insertElementAt(Objects.requireNonNull(child), index);
	}

	@Override
	public void remove(MutableTreeNode node) {
		children.remove(Objects.requireNonNull(node));
	}

	@Override
	public void setUserObject(Object object) {
		userObject = Objects.requireNonNull(object);
	}

	@Override
	public void removeFromParent() {
		parent.remove(this);
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		parent = Objects.requireNonNull(newParent);
	}

	@Override
	public void remove(int index) {
		children.remove(index);
	}
}
