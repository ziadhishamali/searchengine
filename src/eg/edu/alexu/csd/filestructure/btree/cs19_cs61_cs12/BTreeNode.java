package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.util.ArrayList;
import java.util.List;

import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

	private boolean leaf;
	private List<K> keys;
	private List<V> values;
	private List<IBTreeNode<K, V>> children;
	private int numberOfKeys = 0;
	
	public BTreeNode() {
		keys = new ArrayList<>();
		values = new ArrayList<>();
		children = new ArrayList<>();
		leaf = true;
	}

	@Override
	public int getNumOfKeys() {
		return this.numberOfKeys;
	}

	@Override
	public void setNumOfKeys(int numOfKeys) {
		this.numberOfKeys = numOfKeys;
	}

	@Override
	public boolean isLeaf() {
		return this.leaf;

	}

	@Override
	public void setLeaf(boolean isLeaf) {
		this.leaf = isLeaf;
	}

	@Override
	public List<K> getKeys() {
		return this.keys;
	}

	@Override
	public void setKeys(List<K> keys) {
		this.keys = keys;
	}

	@Override
	public List<V> getValues() {
		return this.values;
	}

	@Override
	public void setValues(List<V> values) {
		this.values = values;
	}

	@Override
	public List<IBTreeNode<K, V>> getChildren() {
		return this.children;
	}

	@Override
	public void setChildren(List<IBTreeNode<K, V>> children) {
		this.children = children;
	}

}
