package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12.BTreeNode;
import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

	private IBTreeNode<K, V> root;
	private int minDegree; // t

	public BTree(int minDegree) {
		if (minDegree < 2) {
			throw new RuntimeErrorException(null);
		}
		this.root = null;
		this.minDegree = minDegree;
	}

	@Override
	public int getMinimumDegree() {
		return this.minDegree;
	}

	@Override
	public IBTreeNode<K, V> getRoot() {
		if (root == null) {
			throw new RuntimeErrorException(null);
		}
		return this.root;
	}

	@Override
	public void insert(K key, V value) {

		System.out.println("Inserting key: " + key + " , value: " + value);
		// checks for null key or value
		if (key == null || value == null) {
			throw new RuntimeErrorException(null);
		}

		// checks if the tree is empty
		if (this.root == null) {

			// initialize new root node
			this.root = new BTreeNode<>();
			this.root.setLeaf(true);

			// adds the new key
			List<K> tempKeys = this.root.getKeys();
			tempKeys.add(key);
			this.root.setKeys(tempKeys);

			// adds the new value
			List<V> tempValues = this.root.getValues();
			tempValues.add(value);
			this.root.setValues(tempValues);

			// set the num of keys to 1
			this.root.setNumOfKeys(1);

		} else {

			IBTreeNode<K, V> tempNode = this.find(key);
			if (tempNode != null) {
				return;
			}

			// checks if the root is full
			if (this.root.getNumOfKeys() == (2 * this.minDegree - 1)) {

				// make a new node
				IBTreeNode<K, V> s = new BTreeNode<>();
				s.setLeaf(false);

				// add the root as its first child
				List<IBTreeNode<K, V>> tempNodes = s.getChildren();
				tempNodes.add(0, this.root);
				s.setChildren(tempNodes);

				// split the child
				splitChild(s, 0, this.root);

				int i = 0;
				if (s.getKeys().get(0).compareTo(key) < 0) {
					i++;
				}

				insertNonFull(s.getChildren().get(i), key, value);

				this.root = s;

			} else {

				insertNonFull(this.root, key, value);

			}

		}

	}

	private void insertNonFull(IBTreeNode<K, V> s, K key, V value) {

		// right most element
		int i = s.getNumOfKeys() - 1;

		// s temporary variables
		List<K> stempKeys = s.getKeys();
		List<IBTreeNode<K, V>> stempChildren = s.getChildren();
		List<V> stempValues = s.getValues();

		// checks if s is a leaf
		if (s.isLeaf()) {

			while (i >= 0 && stempKeys.get(i).compareTo(key) > 0) {

				try {
					stempKeys.set(i + 1, stempKeys.get(i));
					stempValues.set(i + 1, stempValues.get(i));
				} catch (Exception e) {
					stempKeys.add(i + 1, stempKeys.get(i));
					stempValues.add(i + 1, stempValues.get(i));
				}
				i--;

			}
			try {
				stempKeys.set(i + 1, key);
				stempValues.set(i + 1, value);
			} catch (Exception e) {
				stempKeys.add(i + 1, key);
				stempValues.add(i + 1, value);
			}
			s.setKeys(stempKeys);
			s.setValues(stempValues);
			s.setNumOfKeys(s.getNumOfKeys() + 1);

		} else {

			// Find the child which is going to have the new key
			while (i >= 0 && stempKeys.get(i).compareTo(key) > 0)
				i--;

			// See if the found child is full
			if (stempChildren.get(i + 1).getNumOfKeys() == (2 * this.minDegree - 1)) {

				// If the child is full, then split it
				splitChild(s, i + 1, stempChildren.get(i + 1));

				// After split, the middle key of C[i] goes up and
				// C[i] is splitted into two. See which of the two
				// is going to have the new key
				if (stempKeys.get(i + 1).compareTo(key) < 0)
					i++;

			}
			insertNonFull(stempChildren.get(i + 1), key, value);

		}

	}

	private void splitChild(IBTreeNode<K, V> s, int i, IBTreeNode<K, V> y) {

		// make a new node
		IBTreeNode<K, V> z = new BTreeNode<>();
		z.setLeaf(y.isLeaf());
		z.setNumOfKeys(this.minDegree - 1);

		// z temporary variables
		List<K> ztempKeys = z.getKeys();
		List<V> ztempValues = z.getValues();
		List<IBTreeNode<K, V>> ztempChildren = z.getChildren();

		// y temporary variables
		List<K> ytempKeys = y.getKeys();
		List<V> ytempValues = y.getValues();
		List<IBTreeNode<K, V>> ytempChildren = y.getChildren();

		// s temporary variables
		List<K> stempKeys = s.getKeys();
		List<V> stempValues = s.getValues();
		List<IBTreeNode<K, V>> stempChildren = s.getChildren();

		// copy last t - 1 keys of y to z
		for (int j = 0; j < this.minDegree - 1; j++) {
			ztempKeys.add(j, ytempKeys.get(j + this.minDegree));
			ztempValues.add(j, ytempValues.get(j + this.minDegree));
		}
		for (int j = 2 * this.minDegree - 2; j >= this.minDegree; j--) {
			ytempKeys.remove(j);
			ytempValues.remove(j);
		}
		z.setKeys(ztempKeys);
		z.setValues(ztempValues);
		y.setKeys(ytempKeys);
		y.setValues(ytempValues);

		// copy last t children of y to z
		if (!y.isLeaf()) {
			for (int j = 0; j < this.minDegree; j++) {
				ztempChildren.add(j, ytempChildren.get(j + this.minDegree));
			}
		}
		z.setChildren(ztempChildren);
		y.setChildren(ytempChildren);

		// update the number of keys of y
		y.setNumOfKeys(this.minDegree - 1);

		for (int j = s.getNumOfKeys(); j >= i + 1; j--) {
			if (j == s.getNumOfKeys()) {
				stempChildren.add(j + 1, stempChildren.get(j));
			} else {
				stempChildren.set(j + 1, stempChildren.get(j));
			}
		}
		try {
			stempChildren.set(i + 1, z);
		} catch (Exception e) {
			stempChildren.add(i + 1, z);
		}
		s.setChildren(stempChildren);

		for (int j = s.getNumOfKeys() - 1; j >= i; j--) {
			try {
				stempKeys.set(j + 1, stempKeys.get(j));
				stempValues.set(j + 1, stempValues.get(j));
			} catch (Exception e) {
				stempKeys.add(j + 1, stempKeys.get(j));
				stempValues.add(j + 1, stempValues.get(j));
			}
		}
		try {
			stempKeys.set(i, ytempKeys.get(this.minDegree - 1));
			stempValues.set(i, ytempValues.get(this.minDegree - 1));
		} catch (Exception e) {
			stempKeys.add(i, ytempKeys.get(this.minDegree - 1));
			stempValues.add(i, ytempValues.get(this.minDegree - 1));
		}
		ytempKeys.remove(this.minDegree - 1);
		ytempValues.remove(this.minDegree - 1);
		s.setKeys(stempKeys);
		s.setValues(stempValues);
		y.setKeys(ytempKeys);
		y.setValues(ytempValues);
		s.setNumOfKeys(s.getNumOfKeys() + 1);

	}

	/*
	 * helping recursive method used by search
	 */
	private V searchHelper(IBTreeNode<K, V> x, K key) {

		// gets the index of the appropriate node
		int i = 0;
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0) {
			i++;
		}

		// checks if the key is found
		if (i != x.getNumOfKeys()) {
			if (key.compareTo(x.getKeys().get(i)) == 0) {
				return x.getValues().get(i);
			}
		}

		// checks if the node is leaf and the key isn't found
		if (x.isLeaf()) {
			return null;
		}

		// recursively call using the appropriate child node
		return searchHelper(x.getChildren().get(i), key);

	}

	@Override
	public V search(K key) {

		// checks for null key
		if (key == null || this.root == null) {
			throw new RuntimeErrorException(null);
		}

		// call the recursive method searchHelper
		return searchHelper(this.root, key);

	}

	/* helping recursive method used by search */
	private IBTreeNode<K, V> findHelper(IBTreeNode<K, V> x, K key) {
		// gets the index of the appropriate node
		int i = 0;
		// System.out.println("i in Find : " + i);
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0) {
			i++;
			// System.out.println("i in Find : " + i);
		}
		// checks if the key is found
		if (i != x.getNumOfKeys()) {
			if (key.compareTo(x.getKeys().get(i)) == 0) {
				return x;
			}
		}
		// checks if the node is leaf and the key isn't found
		if (x.isLeaf()) {
			return null;
		}

		return findHelper(x.getChildren().get(i), key);

	}

	private IBTreeNode<K, V> find(K key) {
		// checks for null key
		if (key == null || this.root == null) {
			throw new RuntimeErrorException(null);
		}
		// call the recursive method searchHelper
		return findHelper(this.root, key);
	}


	@Override
	public boolean delete(K key) {

		if (key == null)
			throw new RuntimeErrorException(new Error());

		IBTreeNode<K, V> node = find(key);
		if (node == null)
			return false;

		remove(key, root);

		return true;
	}

	private void remove(K key, IBTreeNode<K, V> x) {

		int keyIndex = x.getKeys().indexOf(key);
		
		if (keyIndex > -1) {
			if (x.isLeaf()) { // Case1 "Trivial" 
				System.out.println("CASE 1 'Leaf'");

				x.getKeys().remove(keyIndex);
				x.getValues().remove(keyIndex);
				x.setNumOfKeys(x.getNumOfKeys() - 1);
				
				return;
			}
			
			IBTreeNode<K, V> y = x.getChildren().get(keyIndex);
			IBTreeNode<K, V> z = x.getChildren().get(keyIndex + 1);

			if (y.getNumOfKeys() > minDegree - 1) {
				System.out.println("CASE 2a 'Predecessor'");

				IBTreeNode<K, V> predecessor = y;
				predecessor = getPredessor(y);

				int predecessorIndex = predecessor.getNumOfKeys() - 1;
				
				x.getKeys().set(keyIndex, predecessor.getKeys().get(predecessorIndex));
				x.getValues().set(keyIndex, predecessor.getValues().get(predecessorIndex));
				
				predecessor.getKeys().set(predecessorIndex, key);
				predecessor.getValues().set(predecessorIndex, x.getValues().get(keyIndex));
				
				remove(key, y);

			} else if (z.getNumOfKeys() > minDegree - 1) {
				System.out.println("CASE 2b 'Successor'");

				IBTreeNode<K, V> successor = z;
				successor = getSuccessor(z);

				int successorIndex = 0;
				x.getKeys().set(keyIndex, successor.getKeys().get(successorIndex));
				x.getValues().set(keyIndex, successor.getValues().get(successorIndex));
				
				successor.getKeys().set(successorIndex, key);
				successor.getValues().set(successorIndex, x.getValues().get(keyIndex));
				
				remove(key, z);
				
			} else {
				System.out.println("CASE 2c 'Merge'");

				y.getKeys().add(key);
				y.getKeys().addAll(z.getKeys());

				y.getValues().add(x.getValues().get(keyIndex));
				y.getValues().addAll(z.getValues());
				
				if (y.isLeaf()) {
					y.setChildren(new ArrayList<>());
				}
				
				if (!z.isLeaf()) {
					int shiftedChild = y.getNumOfKeys() + 1;
					for (int j = 0; j < z.getNumOfKeys() + 1; j++) {
						y.getChildren().add(shiftedChild++, z.getChildren().get(j));
					}
				}
				y.setNumOfKeys(y.getKeys().size());

				x.getKeys().remove(keyIndex);
				x.getValues().remove(keyIndex);
				x.setNumOfKeys(x.getNumOfKeys() - 1);
				x.getChildren().remove(z);
				
				if (x.getKeys().size() == 0) {
					if (root == x) {
						root = y;
					}
				}
				
				remove(key, y);
			}

		} else {
			System.out.println("FIX");
			int i = 0;
			
			for (i = 0; i < x.getNumOfKeys(); i++) {
				if (key.compareTo(x.getKeys().get(i)) < 0) {
					break;
				}
			}
			
			IBTreeNode<K, V> child = x.getChildren().get(i);
			
			if (child.getKeys().size() < minDegree) {
				boolean flag = false;
				
				if (i != 0) {
					
					IBTreeNode<K, V> left = x.getChildren().get(i - 1);

					if (left.getKeys().size() >= minDegree) {

						child.getKeys().add(0, x.getKeys().get(i - 1));
						child.getValues().add(0, x.getValues().get(i - 1));
						child.setNumOfKeys(child.getKeys().size());

						if (!child.isLeaf()) {
							if (!left.isLeaf()) {
								child.getChildren().add(0, left.getChildren().get(left.getNumOfKeys()));
							}
						} else {
							List<IBTreeNode<K, V>> newChild = new ArrayList<>();
							if (!left.isLeaf()) {
								if (left.getChildren().size() > 0) {
									newChild.add(left.getChildren().get(left.getNumOfKeys()));
									child.setChildren(newChild);
								}
							}
						}

						x.getKeys().remove(i - 1);
						x.getValues().remove(i - 1);
						x.getKeys().add(i - 1, left.getKeys().get(left.getNumOfKeys() - 1));
						x.getValues().add(i - 1, left.getValues().get(left.getNumOfKeys() - 1));

						left.getKeys().remove(left.getNumOfKeys() - 1);
						left.getValues().remove(left.getNumOfKeys() - 1);
						left.setNumOfKeys(left.getKeys().size());
						if (!left.isLeaf()) {
							if (left.getChildren().size() > 0) {
								left.getChildren().remove(left.getNumOfKeys() + 1);
							}
						}

						flag = true;
						remove(key, child);
						
					}
				}
				if (!flag && i < x.getNumOfKeys()) {

					IBTreeNode<K, V> right = x.getChildren().get(i + 1);
					if (right.getKeys().size() >= minDegree) {

						child.getKeys().add(child.getNumOfKeys(), x.getKeys().get(i));
						child.getValues().add(child.getNumOfKeys(), x.getValues().get(i));
						child.setNumOfKeys(child.getKeys().size());

						if (!child.isLeaf()) {
							if (!right.isLeaf()) {
								child.getChildren().add(child.getNumOfKeys(), right.getChildren().get(0));
							}
						} else {
							List<IBTreeNode<K, V>> newChild = new ArrayList<>();
							if (!right.isLeaf()) {
								if (right.getChildren().size() > 0) {
									newChild.add(right.getChildren().get(0));
									child.setChildren(newChild);
								}
							}
						}

						x.getKeys().remove(i);
						x.getValues().remove(i);
						x.getKeys().add(i, right.getKeys().get(0));
						x.getValues().add(i, right.getValues().get(0));

						right.getKeys().remove(0);
						right.getValues().remove(0);
						right.setNumOfKeys(right.getKeys().size());
						
						if (!right.isLeaf()) {
							right.getChildren().remove(0);
						}

						flag = true;
						remove(key, child);
					}

				}
				if (!flag) {
					
					IBTreeNode<K, V> y;
					IBTreeNode<K, V> mergedNode = new BTreeNode<>();

					mergedNode.setKeys(new ArrayList<>());
					mergedNode.setChildren(new ArrayList<>());
					mergedNode.setValues(new ArrayList<>());
					
					if (i != 0) {
						y = x.getChildren().get(i - 1);

						mergedNode.getKeys().addAll(y.getKeys());
						mergedNode.getKeys().add(x.getKeys().get(i - 1));
						mergedNode.getKeys().addAll(child.getKeys());
					
						mergedNode.getValues().addAll(y.getValues());
						mergedNode.getValues().add(x.getValues().get(i - 1));
						mergedNode.getValues().addAll(child.getValues());

						if (!y.isLeaf()) {
							for (int j = 0; j < y.getNumOfKeys() + 1; j++) {
								mergedNode.getChildren().add(y.getChildren().get(j));
							}
						}
						if (!child.isLeaf()) {
							for (int j = 0; j < child.getNumOfKeys() + 1; j++) {
								mergedNode.getChildren().add(child.getChildren().get(j));
							}
						}

						x.getKeys().remove(i - 1);
						x.getValues().remove(i - 1);
						x.setNumOfKeys(x.getNumOfKeys() - 1);
						x.getChildren().remove(y);
						x.getChildren().remove(child);
						
						if (x.getKeys().size() > 0) {
							x.getChildren().add(i - 1, mergedNode);
						} else {
							root = mergedNode;
						}

					} else {
						
						y = x.getChildren().get(i + 1);

						mergedNode.getKeys().addAll(child.getKeys());
						mergedNode.getKeys().add(x.getKeys().get(i));
						mergedNode.getKeys().addAll(y.getKeys());
						
						mergedNode.getValues().addAll(child.getValues());
						mergedNode.getValues().add(x.getValues().get(i));
						mergedNode.getValues().addAll(y.getValues());

						if (!child.isLeaf()) {
							for (int ii = 0; ii < child.getNumOfKeys() + 1; ii++) {
								mergedNode.getChildren().add(child.getChildren().get(ii));
							}
						}
						
						if (!y.isLeaf()) {
							for (int ii = 0; ii < y.getNumOfKeys() + 1; ii++) {
								mergedNode.getChildren().add(y.getChildren().get(ii));
							}
						}

						x.getKeys().remove(i);
						x.getValues().remove(i);
						x.setNumOfKeys(x.getNumOfKeys() - 1);
						x.getChildren().remove(y);
						x.getChildren().remove(child);
						
						if (x.getKeys().size() > 0) {
							x.getChildren().add(i, mergedNode);
						} else if (x == root) {
							root = mergedNode;
						}
					}

					mergedNode.setNumOfKeys(mergedNode.getKeys().size());
					mergedNode.setLeaf(y.isLeaf());

					remove(key, mergedNode);
				}
				
			} else {
				remove(key, child);
			}
		}
	}


	
	private IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> x) {
		while (!x.isLeaf()) {
			x = x.getChildren().get(0);
		}
		return x;
	}

	private IBTreeNode<K, V> getPredessor(IBTreeNode<K, V> x) {
		while (!x.isLeaf()) {
			x = x.getChildren().get(x.getNumOfKeys() + 1 - 1);
		}
		return x;
	}

}