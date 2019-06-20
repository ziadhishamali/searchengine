package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.util.List;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

	private IBTreeNode<K, V> root;
	private IBTreeNode<K, V> parent;
	private int minDegree; // t
	private K successoorKey;

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
		
		//System.out.println("Inserting key: " + key + " , value: " + value);
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
			
			while(i >= 0 && stempKeys.get(i).compareTo(key) > 0) {
				
				try {
					stempKeys.set(i + 1, stempKeys.get(i));
					stempValues.set(i + 1, stempValues.get(i));
				} catch(Exception e) {
					stempKeys.add(i + 1, stempKeys.get(i));
					stempValues.add(i + 1, stempValues.get(i));
				}
				i--;
				
			}
			try {
				stempKeys.set(i + 1, key);
				stempValues.set(i + 1, value);
			} catch(Exception e) {
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
	            splitChild(s, i+1, stempChildren.get(i + 1)); 
	  
	            // After split, the middle key of C[i] goes up and 
	            // C[i] is splitted into two.  See which of the two 
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
		} catch(Exception e) {
			stempChildren.add(i + 1, z);
		}
		s.setChildren(stempChildren);
		
		for (int j = s.getNumOfKeys() - 1; j >= i; j--) {
			try {
				stempKeys.set(j + 1, stempKeys.get(j));
				stempValues.set(j + 1, stempValues.get(j));
			} catch(Exception e) {
				stempKeys.add(j + 1, stempKeys.get(j));
				stempValues.add(j + 1, stempValues.get(j));
			}
		}
		try {
			stempKeys.set(i, ytempKeys.get(this.minDegree - 1));
			stempValues.set(i, ytempValues.get(this.minDegree - 1));
		} catch(Exception e) {
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
		while(i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0) {
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
		//System.out.println("i in Find : " + i);
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0) {
			i++;
			//System.out.println("i in Find : " + i);
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
		this.parent = x;
		
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
		System.out.println(" ");
		System.out.println("**DELETION OF : " + key);
		if (key == null) {
			throw new RuntimeErrorException(null);
		}

		IBTreeNode<K, V> p = new BTreeNode<>();
		IBTreeNode<K, V> x = new BTreeNode<>();
		IBTreeNode<K, V> y = new BTreeNode<>();
		IBTreeNode<K, V> z = new BTreeNode<>();
		if ((x = find((K) key)) == null) {
			return false;
		}
		p = this.parent; // PARENT
		this.parent = null;

		int keyIndex = 0;
		int nodeIndex = 0;
		List<K> temp = x.getKeys();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).equals(key)) {
				keyIndex = i;
			}
		}
	

		System.out.println(" ");
		if (p != null) {
			List<IBTreeNode<K, V>> pChildren = p.getChildren();
			for (int i = 0; i < p.getChildren().size(); i++) {
				if (p.getChildren().get(i).equals(x)) {
					nodeIndex = i;
					break;
				}
			}
			
			int size = p.getChildren().size();
			System.out.println("size of Parent Children Before Maodulation : " + size);
			int limit = p.getNumOfKeys() + 1;
			
			if (p.getChildren().size() > limit) {
				for (int i = limit; i <= p.getChildren().size() + 1; i++) {
					pChildren.remove(pChildren.size() - 1);
				}
			}
			System.out.println("NodeIndex of x : " + nodeIndex);
			System.out.println("keyIndex In Node x : " + keyIndex);
			p.setChildren(pChildren);
			System.out.println("size of Parent Children After Maodulation : " + p.getChildren().size());

			if (nodeIndex == 0) {
				y = p.getChildren().get(nodeIndex + 1); // Right_Sibling
			} else if (nodeIndex >= p.getChildren().size() - 1) {
				z = p.getChildren().get(nodeIndex - 1); // Left_Sibling
			} else {
				y = p.getChildren().get(nodeIndex + 1); // Right_Sibling
				z = p.getChildren().get(nodeIndex - 1); // Left_Sibling
			}
			System.out.println("Parent NODE  : " + p.getKeys());
		}

		System.out.println("NodeIndex of x : " + nodeIndex);
		System.out.println("keyIndex In Node x : " + keyIndex);
		System.out.println("NODE x : " + x.getKeys());
		System.out.println("RightSibling NODE : " + y.getKeys());
		System.out.println("LeftSibling NODE : " + z.getKeys());

		boolean shift = false; // False: ShiftLeft
								// Right: ShiftRight

		/* ************ CASES ******************** */
		if (x.isLeaf()) {
			if (this.root == x) { // CASE1 "Root"
				System.out.println("CASE 1 'Root'");

				List<K> xKeys = x.getKeys();
				List<V> xValues = x.getValues();
				xKeys.remove(keyIndex);
				xValues.remove(keyIndex);
				x.setKeys(xKeys);
				x.setValues(xValues);
				x.setNumOfKeys(x.getNumOfKeys() - 1);

				if (p != null) {
					System.out.println("Parent NODE  : " + p.getKeys());
				}
				System.out.println("NODE x : " + x.getKeys());
				System.out.println("RightSibling NODE : " + y.getKeys());
				System.out.println("LeftSibling NODE : " + z.getKeys());

				return true;
			}
			else if (x.getNumOfKeys() >= minDegree) { // CASE1 "Trivial"
				System.out.println("CASE 1");

				List<K> xKeys = x.getKeys();
				List<V> xValues = x.getValues();
				xKeys.remove(keyIndex);
				xValues.remove(keyIndex);
				x.setKeys(xKeys);
				x.setValues(xValues);
				x.setNumOfKeys(x.getNumOfKeys() - 1);

				if (p != null) {
					System.out.println("Parent NODE  : " + p.getKeys());
				}
				System.out.println("NODE x : " + x.getKeys());
				System.out.println("RightSibling NODE : " + y.getKeys());
				System.out.println("LeftSibling NODE : " + z.getKeys());

				return true;
			} else {
				if (z.getNumOfKeys() > 0) { // LeftSiblingFirst
					if (z.getNumOfKeys() >= minDegree) { // CASE2(a) "Trivial"
						System.out.println("CASE 2(a) 'LeftSibling'");

						stealFromLeftSibling(x, y, z, p, nodeIndex, keyIndex);
						
						List<K> xKeys = x.getKeys();
						List<V> xValues = x.getValues();

						xKeys.remove(keyIndex + 1);
						xValues.remove(keyIndex + 1);
						x.setKeys(xKeys);
						x.setNumOfKeys(xKeys.size());
						
						System.out.println("Finally");

						
						System.out.println("NODE x : " + x.getKeys());
						if (p != null) {
							System.out.println("Parent NODE  : " + p.getKeys());
						}
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());
						
						return true;

					} else if (y.getNumOfKeys() > 0) { // rightSiblingSecond
						if (y.getNumOfKeys() >= minDegree) { // CASE2(a) "Trivial"
							System.out.println("CASE 2(a) 'RightSibling'");

							stealFromRightSibling(x, y, z, p, nodeIndex, keyIndex);
							
							List<K> xKeys = x.getKeys();
							List<V> xValues = x.getValues();
							
							System.out.println("keyIndex : " + keyIndex);

							xKeys.remove(keyIndex);
							xValues.remove(keyIndex);
							x.setKeys(xKeys);
							x.setNumOfKeys(xKeys.size());
							
							System.out.println("Finally");

							
							System.out.println("NODE x : " + x.getKeys());
							if (p != null) {
								System.out.println("Parent NODE  : " + p.getKeys());
							}
							System.out.println("RightSibling NODE : " + y.getKeys());
							System.out.println("LeftSibling NODE : " + z.getKeys());
							
							return true;
						} else {
							System.out.println("CASE 2(b) 'LeftSibling'");

							List<K> zKeys = z.getKeys();
							List<V> zValues = z.getValues();

							List<K> pKeys = p.getKeys();
							List<V> pValues = p.getValues();

							shift = false;
							int parentKeyIndex = nodeIndex - 1;
							int zSize = z.getNumOfKeys();
							
							System.out.println("Merge");
							merge(z, x, p, parentKeyIndex, shift, false);
							// MERGE
							K parentKey = p.getKeys().get(nodeIndex - 1);

							zKeys.remove(zSize + keyIndex + 1);
							zValues.remove(zSize + keyIndex + 1);
							z.setKeys(zKeys);
							z.setValues(zValues);
							z.setNumOfKeys(zKeys.size());
							System.out.println("NODE x : " + x.getKeys());
							if (p != null) {
								System.out.println("Parent NODE  : " + p.getKeys());
							}
							System.out.println("RightSibling NODE : " + y.getKeys());
							System.out.println("LeftSibling NODE : " + z.getKeys());

							if (p.getNumOfKeys() >= minDegree) {
								pKeys.remove(parentKeyIndex);
								pValues.remove(parentKeyIndex);
								p.setKeys(pKeys);
								p.setValues(pValues);
								p.setNumOfKeys(pKeys.size());
							} else {
								
								System.out.println("Fix");
								// parent.getKeys().remove(parentKeyIndex);
								// parent.getValues().remove(parentKeyIndex);
								if (p.equals(this.root) && this.root.getKeys().size() == 1) {
									System.out.println("Root Fix");
									
									this.root = z;
								} else if (p.equals(this.root) && this.root.getKeys().size() > 1) {
									System.out.println("Without Fix");
									pKeys.remove(parentKeyIndex);
									pValues.remove(parentKeyIndex);
									p.setKeys(pKeys);
									p.setValues(pValues);
									p.setNumOfKeys(pKeys.size());
								} else {
									System.out.println("internalNodesFIX_UP");

									internalNodesFIX_UP(parentKey, z);
								}
								
							}	
							
							System.out.println("Finally");

							
							System.out.println("NODE x : " + x.getKeys());
							if (p != null) {
								System.out.println("Parent NODE  : " + p.getKeys());
							}
							System.out.println("Parent NODE  : " + p.getKeys());
							System.out.println("RightSibling NODE : " + y.getKeys());
							System.out.println("LeftSibling NODE : " + z.getKeys());
							
							return true;
						}
					} else { // CASE2(b) "Trick"
						System.out.println("CASE 2(b) 'LeftSibling'");

						List<K> zKeys = z.getKeys();
						List<V> zValues = z.getValues();

						List<K> pKeys = p.getKeys();
						List<V> pValues = p.getValues();

						shift = false;
						int parentKeyIndex = nodeIndex - 1;
						int zSize = z.getNumOfKeys();
						System.out.println("Merge");
						merge(z, x, p, parentKeyIndex, shift, false);
						// MERGE
						K parentKey = p.getKeys().get(nodeIndex - 1);

						zKeys.remove(zSize + keyIndex + 1);
						zValues.remove(zSize + keyIndex + 1);
						z.setKeys(zKeys);
						z.setValues(zValues);
						z.setNumOfKeys(zKeys.size());
						System.out.println("NODE x : " + x.getKeys());
						System.out.println("Parent NODE  : " + p.getKeys());
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());

						if (p.getNumOfKeys() >= minDegree) {
							pKeys.remove(parentKeyIndex);
							pValues.remove(parentKeyIndex);
							p.setKeys(pKeys);
							p.setValues(pValues);
							p.setNumOfKeys(pKeys.size());
						} else {
							
							System.out.println("Fix");
							// parent.getKeys().remove(parentKeyIndex);
							// parent.getValues().remove(parentKeyIndex);
							if (p.equals(this.root) && this.root.getKeys().size() == 1) {
								System.out.println("Root Fix");
								
								this.root = z;
							} else if (p.equals(this.root) && this.root.getKeys().size() > 1) {
								System.out.println("Without Fix");
								pKeys.remove(parentKeyIndex);
								pValues.remove(parentKeyIndex);
								p.setKeys(pKeys);
								p.setValues(pValues);
								p.setNumOfKeys(pKeys.size());
							} else {
								System.out.println("internalNodesFIX_UP");

								internalNodesFIX_UP(parentKey, z);
							}
						}
						
						System.out.println("Finally");
						
						System.out.println("NODE x : " + x.getKeys());
						if (p != null) {
							System.out.println("Parent NODE  : " + p.getKeys());
						}
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());
						
						return true;
					}
				} else if (y.getNumOfKeys() > 0) { // rightSiblingSecond
					if (y.getNumOfKeys() >= minDegree) { // CASE2(a) "Trivial"
						System.out.println("CASE 2(a) 'RightSibling'");

						stealFromRightSibling(x, y, z, p, nodeIndex, keyIndex);
						
						List<K> xKeys = x.getKeys();
						List<V> xValues = x.getValues();

						xKeys.remove(keyIndex);
						xValues.remove(keyIndex);
						x.setKeys(xKeys);
						x.setNumOfKeys(xKeys.size());
						
						System.out.println("Finally");

						System.out.println("NODE x : " + x.getKeys());
						if (p != null) {
							System.out.println("Parent NODE  : " + p.getKeys());
						}
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());
						
						return true;

					} else { // CASE2(b) "Trick"
						System.out.println("CASE 2(b) 'RightSibling'");
						
						List<K> xKeys = x.getKeys();
						List<V> xValues = x.getValues();

						List<K> pKeys = p.getKeys();
						List<V> pValues = p.getValues();

						shift = true;
						int parentKeyIndex = nodeIndex;
						System.out.println("parentKeyIndex : " + parentKeyIndex);
						
						System.out.println("Merge");
						merge(x, y, p, parentKeyIndex, shift, false);
						// MERGE
						K parentKey = p.getKeys().get(nodeIndex);

						xKeys.remove(keyIndex);
						xValues.remove(keyIndex);
						x.setKeys(xKeys);
						x.setValues(xValues);
						x.setNumOfKeys(xKeys.size());
						
						
						
						List <IBTreeNode<K, V>> xChildren = x.getChildren();
						x.setChildren(xChildren);
						
						System.out.println("After Merge");
						xChildren = x.getChildren();
						x.setChildren(xChildren);
						
						System.out.println("Children");
						for (int i = 0; i < xChildren.size(); i++) {
							IBTreeNode<K, V> v = xChildren.get(i);
							System.out.println(v.getKeys());
						}
						
						
						
						
						System.out.println("NODE x : " + x.getKeys());
						if (p != null) {
							System.out.println("Parent NODE  : " + p.getKeys());
						}
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());
						
						
						
						

						if (p.getNumOfKeys() >= minDegree) {
							pKeys.remove(parentKeyIndex);
							pValues.remove(parentKeyIndex);
							p.setKeys(pKeys);
							p.setValues(pValues);
							p.setNumOfKeys(pKeys.size());
						} else {
							
							System.out.println("Fix");
							// parent.getKeys().remove(parentKeyIndex);
							// parent.getValues().remove(parentKeyIndex);
							if (p.equals(this.root) && this.root.getKeys().size() == 1) {
								System.out.println("Root Fix");
								
								this.root = x;
								
							} else if (p.equals(this.root) && this.root.getKeys().size() > 1) {
								System.out.println("Without Fix");
								pKeys.remove(parentKeyIndex);
								pValues.remove(parentKeyIndex);
								p.setKeys(pKeys);
								p.setValues(pValues);
								p.setNumOfKeys(pKeys.size());
							} else {
								System.out.println("internalNodesFIX_UP");

								internalNodesFIX_UP(parentKey, y);
							}
							// delete(parentKey);
							// fix
							
							
							
						}
						
						System.out.println("Finally");

						System.out.println("NODE x : " + x.getKeys());
						if (p != null) {
							System.out.println("Parent NODE  : " + p.getKeys());
						}						
						System.out.println("RightSibling NODE : " + y.getKeys());
						System.out.println("LeftSibling NODE : " + z.getKeys());
						

						return true;
					}
				}
			}
		} else {
			
			IBTreeNode<K, V> predecessor = new BTreeNode<>();
			predecessor = getPredecesor(x, keyIndex);
			IBTreeNode<K, V> successor = new BTreeNode<>();
			successor = getSuccessor(x, keyIndex + 1);
			
			if (predecessor.getNumOfKeys() >= minDegree) { // CASE3(a) "Trivial"
				System.out.println("CASE3(a) 'Predecessor'");
				
				K predecessorKey = predecessor.getKeys().get(predecessor.getKeys().size() - 1);
				V predecessorValue = predecessor.getValues().get(predecessor.getKeys().size() - 1);
				
				List<K> xKeys = x.getKeys();
				List<V> xValues = x.getValues();
				
				List<K> predecessorKeys = predecessor.getKeys();
				List<V> predecessorValues = predecessor.getValues();
				
				predecessorKeys.remove(predecessor.getKeys().size() - 1);
				predecessorValues.remove(predecessor.getKeys().size() - 1);
				
				predecessor.setKeys(predecessorKeys);
				predecessor.setValues(predecessorValues);
				predecessor.setNumOfKeys(predecessorKeys.size());
				
				xKeys.set(keyIndex, predecessorKey);
				xValues.set(keyIndex, predecessorValue);
				x.setKeys(xKeys);
				x.setValues(xValues);
				
				System.out.println("NODE x : " + x.getKeys());
				if (p != null) {
					System.out.println("Parent NODE  : " + p.getKeys());
				}
				System.out.println("RightSibling NODE : " + y.getKeys());
				System.out.println("LeftSibling NODE : " + z.getKeys());
				System.out.println("Predecessor NODE : " + predecessor.getKeys());

				
				return true;

			} else {
				if (successor.getNumOfKeys() >= minDegree) { // CASE3(b) "Trivial"
					System.out.println("CASE3(b) 'Successor'");
					
					K successorKey = successor.getKeys().get(0);
					V successorrValue = successor.getValues().get(0);
					
					this.successoorKey = successorKey;
					
					List<K> xKeys = x.getKeys();
					List<V> xValues = x.getValues();
					
					List<K> successorKeys = successor.getKeys();
					List<V> successorValues = successor.getValues();
					
					successorKeys.remove(0);
					successorValues.remove(0);
					
					successor.setKeys(successorKeys);
					successor.setValues(successorValues);
					successor.setNumOfKeys(successorKeys.size());
					
					xKeys.set(keyIndex, successorKey);
					xValues.set(keyIndex, successorrValue);
					x.setKeys(xKeys);
					x.setValues(xValues);
					
					System.out.println("NODE x : " + x.getKeys());
					if (p != null) {
						System.out.println("Parent NODE  : " + p.getKeys());
					}
					System.out.println("RightSibling NODE : " + y.getKeys());
					System.out.println("LeftSibling NODE : " + z.getKeys());
					System.out.println("Successor NODE : " + successor.getKeys());

					return true;

				} else { // CASE3(c) "Trick"
					System.out.println("CASE3(c)");
					
					IBTreeNode<K, V> left = new BTreeNode<>();
					IBTreeNode<K, V> right = new BTreeNode<>();
					if (x.getChildren().size() > 0) {
						left = x.getChildren().get(0);
					} 
					if (x.getChildren().size() >= 2) {
						right = x.getChildren().get(1);
					}
					
					System.out.println("Left Child : " + left.getKeys());
					System.out.println("Right Child : " + right.getKeys());
					
					if (x.equals(this.root) && this.root.getKeys().size() == 1) { /////////////////////////////////////
						System.out.println("Root Fix");
						System.out.println("Left Child : " + left.getKeys());
						System.out.println("Right Child : " + right.getKeys());
						root_FIX(x, left, right);
						
						return true;
					}


					
					List<K> xKeys = x.getKeys();
					List<V> xValues = x.getValues();
					
					List<K> predecessorKeys = predecessor.getKeys();
					List<V> predecessorValues = predecessor.getValues();
					
					K predecessorKey = predecessor.getKeys().get(predecessorKeys.size() - 1);
					V predecessorValue = predecessor.getValues().get(predecessorKeys.size() - 1);
					
					K successorKey = successor.getKeys().get(0);
					V successorrValue = successor.getValues().get(0);
					
					this.successoorKey = successorKey;
					
					int prdecessorSize = predecessor.getNumOfKeys();
					
					System.out.println("Successor NODE : " + successor.getKeys());
					System.out.println("Predecessor NODE : " + predecessor.getKeys());
					
					System.out.println("Before Merge");
					List <IBTreeNode<K, V>> xChildren = x.getChildren();
					x.setChildren(xChildren);
					
					System.out.println("Children");
					for (int i = 0; i < xChildren.size(); i++) {
						IBTreeNode<K, V> v = xChildren.get(i);
						System.out.println(v.getKeys());
					}
					
					
					System.out.println("predecessorKey : " + predecessorKey);
					IBTreeNode<K, V> y1 = new BTreeNode<>();
					if ((y1 = find((K) predecessorKey)) == null) {
						throw new RuntimeErrorException(null);
					}
					p = this.parent; // PARENT
					this.parent = null;
					int keyIndex1 = 0;
					int nodeIndex1 = 0;
					List<K> temp1 = y1.getKeys();
					for (int i = 0; i < temp1.size(); i++) {
						if (temp1.get(i).equals(predecessorKey)) {
							keyIndex1 = i;
						}
					}
					
					if (p != null) {
						List<IBTreeNode<K, V>> pChildren = p.getChildren();
						for (int i = 0; i < p.getChildren().size(); i++) {
							if (p.getChildren().get(i).equals(y1)) {
								nodeIndex1 = i;
								break;
							}
						}
					}
					
					merge(predecessor, successor, x, keyIndex, false, true);
					
					System.out.println("After Merge");
					xChildren = x.getChildren();
					x.setChildren(xChildren);
					
					System.out.println("Children");
					for (int i = 0; i < xChildren.size(); i++) {
						IBTreeNode<K, V> v = xChildren.get(i);
						System.out.println(v.getKeys());
					}
					
					System.out.println("prdecessorSize : " + prdecessorSize);
					predecessorKeys.remove(prdecessorSize);
					predecessorValues.remove(prdecessorSize);
					predecessor.setKeys(predecessorKeys);
					predecessor.setValues(predecessorValues);
					
					
					
					System.out.println("NodeIndex1 'predecessorNodeInde' : " + nodeIndex1);
					p.getChildren().get(nodeIndex1).setKeys(predecessorKeys);
					p.getChildren().get(nodeIndex1).setValues(predecessorValues);
					p.getChildren().get(nodeIndex1).setNumOfKeys(predecessorKeys.size());

					
					
					
					
					System.out.println("NODE x : " + x.getKeys());
					if (p != null) {
						System.out.println("Parent NODE  : " + p.getKeys());
					}
					System.out.println("RightSibling NODE : " + y.getKeys());
					System.out.println("LeftSibling NODE : " + z.getKeys());
					System.out.println("Predecessor NODE : " + predecessor.getKeys());
					
					if (x.getNumOfKeys() >= minDegree) {
						System.out.println("Without Fix");
						xKeys.remove(keyIndex);
						xValues.remove(keyIndex);
						x.setKeys(xKeys);
						x.setValues(xValues);
						x.setNumOfKeys(xKeys.size());
					} else {
						System.out.println("Fix");
						// parent.getKeys().remove(parentKeyIndex);
						// parent.getValues().remove(parentKeyIndex);
						if (x.equals(this.root) && this.root.getKeys().size() == 1) { /////////////////////////////////////
							System.out.println("Root Fix");
							System.out.println("Left Child : " + left.getKeys());
							System.out.println("Right Child : " + right.getKeys());
							root_FIX(x, left, right);
						} else if (x.equals(this.root) && this.root.getKeys().size() > 1) {
							System.out.println("Without Fix");
							xKeys.remove(keyIndex);
							xValues.remove(keyIndex);
							x.setKeys(xKeys);
							x.setValues(xValues);
							x.setNumOfKeys(xKeys.size());
						} else {
							System.out.println("internalNodesFIX_UP");

							internalNodesFIX_UP(key, predecessor);
						}
						// delete(parentKey);
						// fix
					}
					
					System.out.println("Finally");

					
					System.out.println("NODE x : " + x.getKeys());
					if (p != null) {
						System.out.println("Parent NODE  : " + p.getKeys());
					}
					System.out.println("RightSibling NODE : " + y.getKeys());
					System.out.println("LeftSibling NODE : " + z.getKeys());
					
					System.out.println("Children");
					List<IBTreeNode<K, V>> newChildren = x.getChildren();					
					for (int i = 0; i < newChildren.size(); i++) {
						IBTreeNode<K, V> g= newChildren.get(i);
						System.out.println(g.getKeys());

					}
					
					return true;
				}
			}
		}
		return false;
	}

	void merge(IBTreeNode<K, V> x, IBTreeNode<K, V> y, IBTreeNode<K, V> parent, int indexOfKeyInParent, boolean shift,
			boolean fix) {
		List<K> xKeys = x.getKeys();
		List<V> xValues = x.getValues();
		List<K> yKeys = y.getKeys();
		List<V> yValues = y.getValues();
		
		List<IBTreeNode<K, V>> parentChildren = parent.getChildren();


		List<K> newKeys = xKeys;
		List<V> newValues = xValues;

		//if (!fix) {
			newKeys.add(parent.getKeys().get(indexOfKeyInParent));
			newValues.add(parent.getValues().get(indexOfKeyInParent));
		//}
			
		newKeys.addAll(yKeys);
		newValues.addAll(yValues);

		/*for (int i = 0; i < yKeys.size(); i++) {
			newKeys.add(yKeys.get(i));
		}
		for (int i = 0; i < yValues.size(); i++) {
			newValues.add(yValues.get(i));
		}*/
		
		x.setKeys(newKeys);
		x.setValues(newValues);
		x.setNumOfKeys(newKeys.size());

		if (!fix) {
			if (shift) {
				System.out.println("index'removed'");

				parentChildren.remove(indexOfKeyInParent + 1);
				parent.setChildren(parentChildren);
			} else {
				System.out.println("index + 1 'removed'");

				parentChildren.remove(indexOfKeyInParent + 1);
				parent.setChildren(parentChildren);
			}
		} else {
			
			IBTreeNode<K, V> p1 = new BTreeNode<>();
			
			
			System.out.println("successoorKey : " + successoorKey);
			if ((y = find((K) successoorKey)) == null) {
				throw new RuntimeErrorException(null);
			}
			p1 = this.parent; // PARENT
			
			int keyIndex = 0;
			int nodeIndex = 0;
			List<K> temp = y.getKeys();
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals(successoorKey)) {
					keyIndex = i;
				}
			}
			
			if (p1 != null) {
				List<IBTreeNode<K, V>> pChildren = p1.getChildren();
				for (int i = 0; i < p1.getChildren().size(); i++) {
					if (p1.getChildren().get(i).equals(y)) {
						nodeIndex = i;
						break;
					}
				}
			}
			
			List<IBTreeNode<K, V>> pChildren1 = p1.getChildren();
			
			System.out.println("nodeIndex : " + nodeIndex);
			System.out.println("p.keys : " + p1.getKeys());
			pChildren1.remove(nodeIndex);
			p1.setChildren(pChildren1);
			
			this.parent = null;
			this.successoorKey = null;
			
		}
	}

	void internalNodesFIX_UP(K key, IBTreeNode<K, V> node) {
		IBTreeNode<K, V> x = new BTreeNode<>();
		IBTreeNode<K, V> p = new BTreeNode<>();
		IBTreeNode<K, V> y = new BTreeNode<>();
		IBTreeNode<K, V> z = new BTreeNode<>();
		

		if ((x = find((K) key)) == null) {
			// throw;
		}
		p = this.parent; // PARENT
		this.parent = null;
		int keyIndex = 0;
		int nodeIndex = 0;
		List<K> temp = x.getKeys();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).equals(key)) {
				keyIndex = i;
			}
		}
		
		List<K> xKeys = x.getKeys();
		List<V> xValues = x.getValues();

		if (p != null) {
			for (int i = 0; i < p.getChildren().size(); i++) {
				if (p.getChildren().get(i).equals(x)) {
					nodeIndex = i;
				}
			}

			if (nodeIndex == 0) {
				y = p.getChildren().get(nodeIndex + 1); // Right_Sibling
			} else if (nodeIndex == p.getChildren().size() - 1) {
				z = p.getChildren().get(nodeIndex - 1); // Left_Sibling
			} else {
				y = p.getChildren().get(nodeIndex + 1); // Right_Sibling
				z = p.getChildren().get(nodeIndex - 1); // Left_Sibling
			}

			xKeys.remove(keyIndex);
			xValues.remove(keyIndex);
			x.setKeys(xKeys);
			x.setValues(xValues);
			x.setNumOfKeys(xKeys.size());
			
			System.out.println("keyIndex : " + keyIndex);
			System.out.println("Key : " + key);
			System.out.println("NODE x : " + x.getKeys());
			List <IBTreeNode<K, V>> xChildren5 = x.getChildren();
			System.out.println("Children");
			for (int i = 0; i < xChildren5.size(); i++) {
				IBTreeNode<K, V> v = xChildren5.get(i);
				System.out.println(v.getKeys());
			}
			System.out.println("Parent NODE  : " + p.getKeys());
			System.out.println("RightSibling NODE : " + y.getKeys());
			System.out.println("LeftSibling NODE : " + z.getKeys());
			
			
			
			if (z.getNumOfKeys() >= minDegree) { // StealFromLeftSibling'First'
				System.out.println(" StealFromLeftSibling");
				
				List <IBTreeNode<K, V>> zChildren = z.getChildren();
				IBTreeNode<K, V> shiftedSibling = zChildren.get(z.getNumOfKeys());
				System.out.println("shiftedSibling" + shiftedSibling.getKeys());
				
				zChildren.remove(z.getNumOfKeys());
				z.setChildren(zChildren);
				
				
				stealFromLeftSibling(x, y, z, p, nodeIndex, keyIndex);
				
				
				
				List <IBTreeNode<K, V>> xChildren = x.getChildren();
				xChildren.add(0, shiftedSibling);
				x.setChildren(xChildren);
				x.setNumOfKeys(x.getNumOfKeys() + 1);
				
				System.out.println("Children");
				for (int i = 0; i < xChildren.size(); i++) {
					IBTreeNode<K, V> v = xChildren.get(i);
					System.out.println(v.getKeys());
				}
				
				
				

			} else if (y.getNumOfKeys() >= minDegree) { // StealFromRightSibling'Second'
				System.out.println(" StealFromRightSibling");
				
				List <IBTreeNode<K, V>> yChildren = y.getChildren();
				IBTreeNode<K, V> shiftedSibling = yChildren.get(0);
				System.out.println("shiftedSibling" + shiftedSibling.getKeys());
				
				yChildren.remove(0);
				y.setChildren(yChildren);

				stealFromRightSibling(x, y, z, p, nodeIndex, keyIndex);
				
				List <IBTreeNode<K, V>> xChildren = x.getChildren();
				xChildren.set(x.getNumOfKeys(), shiftedSibling);
				x.setChildren(xChildren);
				x.setNumOfKeys(x.getNumOfKeys() + 1);


			} else { // Merge
				System.out.println("Merge");
				
				if (z.getNumOfKeys() > 0) {
					System.out.println("LeftSibling");

					List<K> pKeys = p.getKeys();
					List<V> pValues = p.getValues();
					int parentIndex = nodeIndex - 1;
					
					K parentKey = p.getKeys().get(nodeIndex - 1);
					merge(z, x, p, parentIndex, false, false);	
					
					List<IBTreeNode<K, V>> leftChildren = z.getChildren();
					List<IBTreeNode<K, V>> rightChildren = x.getChildren();

					List<IBTreeNode<K, V>> newChildren = leftChildren;
					
					for (int i = 0; i < rightChildren.size(); i++) {
						newChildren.add(rightChildren.get(i));
					}
					
					z.setChildren(newChildren);
					
					System.out.println("NODE x : " + x.getKeys());
					System.out.println("Parent NODE  : " + p.getKeys());
					System.out.println("RightSibling NODE : " + y.getKeys());
					System.out.println("LeftSibling NODE : " + z.getKeys());

					
					if (p.getNumOfKeys() >= minDegree) {
						pKeys.remove(parentIndex);
						pValues.remove(parentIndex);
						p.setKeys(pKeys);
						p.setValues(pValues);
						p.setNumOfKeys(pKeys.size());
					} else {
						System.out.println("Fix");
						internalNodesFIX_UP(parentKey, z);
					}
					
				} else if (y.getNumOfKeys() > 0) {
					System.out.println("RightSibling");
					
					List<K> pKeys = p.getKeys();
					List<V> pValues = p.getValues();
					int parentIndex = nodeIndex;

					K parentKey = p.getKeys().get(parentIndex);
					merge(x, y, p, parentIndex, true, false);
					
					List<IBTreeNode<K, V>> leftChildren = x.getChildren();
					List<IBTreeNode<K, V>> rightChildren = y.getChildren();

					List<IBTreeNode<K, V>> newChildren = leftChildren;
					
					for (int i = 0; i < rightChildren.size(); i++) {
						newChildren.add(rightChildren.get(i));
					}
					
					x.setChildren(newChildren);
					
					System.out.println("NODE x : " + x.getKeys());
					System.out.println("Parent NODE  : " + p.getKeys());
					System.out.println("RightSibling NODE : " + y.getKeys());
					System.out.println("LeftSibling NODE : " + z.getKeys());
					
					if (p.getNumOfKeys() >= minDegree) {
						pKeys.remove(parentIndex);
						pValues.remove(parentIndex);
						p.setKeys(pKeys);
						p.setValues(pValues);
						p.setNumOfKeys(pKeys.size());
					} else {
						if (p.getNumOfKeys() == 1) {
							System.out.println("NO Parent");
							this.root = x;
							System.out.println("Root : " + this.root.getKeys());
							List <IBTreeNode<K, V>> xChildren = x.getChildren();
							System.out.println("Children");
							for (int i = 0; i < xChildren.size(); i++) {
								IBTreeNode<K, V> v = xChildren.get(i);
								System.out.println(v.getKeys());
							}

						} else {
							System.out.println("Fix");
							internalNodesFIX_UP(parentKey, y);
						}
						
					}
				}				
			}
		} else {  //ReachToTheRoot
			System.out.println("NO Parent");
			List<K> xKeys1 = x.getKeys();
			List<V> xValues1 = x.getValues();

			xKeys1.remove(keyIndex);
			xValues1.remove(keyIndex);
			x.setKeys(xKeys1);
			x.setValues(xValues1);
			x.setNumOfKeys(xKeys1.size());
			
			if (x.getNumOfKeys() > 0) {
				System.out.println("Root Can have 1 Key at Least");
			} else {
				this.root = node;
				System.out.println("Root : " + this.root.getKeys());
				
				List <IBTreeNode<K, V>> xChildren = node.getChildren();
				
				System.out.println("Children");
				for (int i = 0; i < xChildren.size(); i++) {
					IBTreeNode<K, V> v = xChildren.get(i);
					System.out.println(v.getKeys());
				}
				
			}

		}
	}
	
	private void root_FIX (IBTreeNode<K, V> x, IBTreeNode<K, V> left, IBTreeNode<K, V> right) {
		
		System.out.println("ROOT FIX");
		System.out.println("Merge Left & Right");
		
		System.out.println("Left.keys : " + left.getKeys());
		
		List<K> leftKeys = left.getKeys();
		List<V> leftValues = left.getValues();
		List<IBTreeNode<K, V>> leftChildren = left.getChildren();
		
		System.out.println("leftKeys : " + leftKeys);

		List<K> rightKeys = right.getKeys();
		List<V> rightValues = right.getValues();
		List<IBTreeNode<K, V>> rightChildren = right.getChildren();

		List<K> newKeys = leftKeys;
		List<V> newValues = leftValues;
		List<IBTreeNode<K, V>> newChildren = leftChildren;
		
		System.out.println("newKeys : " + newKeys);

		newKeys.addAll(rightKeys);
		newValues.addAll(rightValues);
		newChildren.addAll(rightChildren);
		
		System.out.println("newKeys : " + newKeys);

		/*for (int i = 0; i < rightKeys.size(); i++) {
			newKeys.add(rightKeys.get(i));
		}
		for (int i = 0; i < rightValues.size(); i++) {
			newValues.add(rightValues.get(i));
		}
		for (int i = 0; i < rightChildren.size(); i++) {
			newChildren.add(rightChildren.get(i));
		}*/
		/*x.setKeys(newKeys);
		x.setValues(newValues);
		x.setChildren(newChildren);
		x.setNumOfKeys(newKeys.size());*/
		
		IBTreeNode<K, V> root = new BTreeNode<>();
		root.setKeys(newKeys);
		root.setValues(newValues);
		root.setChildren(newChildren);
		root.setNumOfKeys(newKeys.size());
		root.setLeaf(false);
		
		this.root = root;
		System.out.println("Root : " + root.getKeys());
		System.out.println("Root : " + this.root.getKeys());
		for (int i = 0; i <root.getChildren().size(); i++) {
			IBTreeNode<K, V> g= newChildren.get(i);
			System.out.println(g.getKeys());

		}
		
		
	}

	private void stealFromLeftSibling(IBTreeNode<K, V> x, IBTreeNode<K, V> y, IBTreeNode<K, V> z, IBTreeNode<K, V> p,
			int nodeIndex, int keyIndex) {
		
		System.out.println("stealFromLeftSibling");
		
		List<K> xKeys = x.getKeys();
		List<V> xValues = x.getValues();

		List<K> zKeys = z.getKeys();
		List<V> zValues = z.getValues();

		List<K> pKeys = p.getKeys();
		List<V> pValues = p.getValues();

		K parentKey = p.getKeys().get(nodeIndex - 1);
		V parentValue = p.getValues().get(nodeIndex - 1);

		K siblingKey = p.getChildren().get(nodeIndex - 1).getKeys()
				.get(p.getChildren().get(nodeIndex - 1).getNumOfKeys() - 1);
		V siblingValue = p.getChildren().get(nodeIndex - 1).getValues()
				.get(p.getChildren().get(nodeIndex - 1).getNumOfKeys() - 1);

		zKeys.remove(z.getNumOfKeys() - 1);
		zValues.remove(z.getNumOfKeys() - 1);
		z.setKeys(zKeys);
		z.setValues(zValues);
		z.setNumOfKeys(zKeys.size());

		pKeys.set(nodeIndex - 1, siblingKey);
		pValues.set(nodeIndex - 1, siblingValue);
		p.setKeys(pKeys);
		p.setValues(pValues);
		// Sort
		
		xKeys.add(0, parentKey);
		xValues.add(0, parentValue);
		x.setKeys(xKeys);
		x.setValues(xValues);
		// Sort
		System.out.println("NODE x : " + x.getKeys());
		System.out.println("Parent NODE  : " + p.getKeys());
		System.out.println("RightSibling NODE : " + y.getKeys());
		System.out.println("LeftSibling NODE : " + z.getKeys());
	}

	private void stealFromRightSibling(IBTreeNode<K, V> x, IBTreeNode<K, V> y, IBTreeNode<K, V> z, IBTreeNode<K, V> p,
			int nodeIndex, int keyIndex) {
		
		System.out.println("stealFromRightSibling");
		
		List<K> xKeys = x.getKeys();
		List<V> xValues = x.getValues();

		List<K> yKeys = y.getKeys();
		List<V> yValues = y.getValues();

		List<K> pKeys = p.getKeys();
		List<V> pValues = p.getValues();

		K parentKey = p.getKeys().get(nodeIndex);
		V parentValue = p.getValues().get(nodeIndex);

		K siblingKey = p.getChildren().get(nodeIndex + 1).getKeys().get(0);
		V siblingValue = p.getChildren().get(nodeIndex + 1).getValues().get(0);

		yKeys.remove(0);
		yValues.remove(0);
		y.setKeys(yKeys);
		y.setValues(yValues);
		y.setNumOfKeys(yKeys.size());

		pKeys.set(nodeIndex, siblingKey);
		pValues.set(nodeIndex, siblingValue);
		p.setKeys(pKeys);
		p.setValues(pValues);
		// Sort

		/*xKeys.remove(keyIndex);
		xValues.remove(keyIndex);*/
		
		xKeys.add(parentKey);
		xValues.add(parentValue);
		x.setKeys(xKeys);
		x.setValues(xValues);
		// Sort
		System.out.println("NODE x : " + x.getKeys());
		System.out.println("Parent NODE  : " + p.getKeys());
		System.out.println("RightSibling NODE : " + y.getKeys());
		System.out.println("LeftSibling NODE : " + z.getKeys());
	}

	private IBTreeNode<K, V> getPredecesor(IBTreeNode<K, V> x, int keyIndex) {
		IBTreeNode<K, V> z = x.getChildren().get(keyIndex);
		System.out.println("z :" + z.getKeys());
		
		List<IBTreeNode<K, V>> zChildren = z.getChildren();
		int size = z.getChildren().size();
		int limit = z.getNumOfKeys() + 1;
		
		if (z.getChildren().size() > limit) {
			for (int i = limit; i <= z.getChildren().size() + 1; i++) {
				zChildren.remove(zChildren.size() - 1);
			}
		}
		z.setChildren(zChildren);
		
		while (!z.isLeaf()) {
			z = z.getChildren().get(z.getChildren().size() - 1);
			
			List<IBTreeNode<K, V>> z2Children = z.getChildren();
			int size2 = z.getChildren().size();
			int limit2 = z.getNumOfKeys() + 1;
			
			if (z.getChildren().size() > limit2) {
				for (int i = limit; i <= z.getChildren().size() + 1; i++) {
					z2Children.remove(z2Children.size() - 1);
				}
			}
			z.setChildren(z2Children);
			
			System.out.println("z :" + z.getKeys());
		}
		return z;
	}

	private IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> x, int keyIndex) {
		IBTreeNode<K, V> z = x.getChildren().get(keyIndex);
		
		List<IBTreeNode<K, V>> zChildren = z.getChildren();
		int size = z.getChildren().size();
		int limit = z.getNumOfKeys() + 1;
		
		if (z.getChildren().size() > limit) {
			for (int i = limit; i <= z.getChildren().size() + 1; i++) {
				zChildren.remove(zChildren.size() - 1);
			}
		}
		z.setChildren(zChildren);
		
		while (!z.isLeaf()) {
			z = z.getChildren().get(0);
			
			List<IBTreeNode<K, V>> z2Children = z.getChildren();
			int size2 = z.getChildren().size();
			int limit2 = z.getNumOfKeys() + 1;
			
			if (z.getChildren().size() > limit2) {
				for (int i = limit; i <= z.getChildren().size() + 1; i++) {
					z2Children.remove(z2Children.size() - 1);
				}
			}
			z.setChildren(z2Children);
			
		}
		return z;
	}

	/*
	 * public Object search(Comparable key) { if (key == null) { throw new
	 * RuntimeErrorException(null); } INode<T, V> node = new Node<>(); if ((node =
	 * this.find(key)) == null) { return null; } return node.getValue(); }
	 */

	/*
	 * public IBTreeNode find(Comparable key) { IBTreeNode<K, V> x = root; return
	 * null; }
	 */

	/*int mergeNodes(INode srcNode, Node dstNode) {
		int medianKeyIndex;
		if (srcNode.mKeys[0] < dstNode.mKeys[dstNode.mNumKeys - 1]) {
			int i;
			// Shift all elements of dstNode right by srcNode.mNumKeys + 1 to make place for
			// the srcNode and the median key.
			if (!dstNode.mIsLeafNode) {
				dstNode.mChildNodes[srcNode.mNumKeys + dstNode.mNumKeys + 1] = dstNode.mChildNodes[dstNode.mNumKeys];
			}
			for (i = dstNode.mNumKeys; i > 0; i--) {
				dstNode.mKeys[srcNode.mNumKeys + i] = dstNode.mKeys[i - 1];
				dstNode.mObjects[srcNode.mNumKeys + i] = dstNode.mObjects[i - 1];
				if (!dstNode.mIsLeafNode) {
					dstNode.mChildNodes[srcNode.mNumKeys + i] = dstNode.mChildNodes[i - 1];
				}
			}
			// Clear the median key (element).
			medianKeyIndex = srcNode.mNumKeys;
			dstNode.mKeys[medianKeyIndex] = 0;
			dstNode.mObjects[medianKeyIndex] = null;
			// Copy the srcNode's elements into dstNode.
			for (i = 0; i < srcNode.mNumKeys; i++) {
				dstNode.mKeys[i] = srcNode.mKeys[i];
				dstNode.mObjects[i] = srcNode.mObjects[i];
				if (!srcNode.mIsLeafNode) {
					dstNode.mChildNodes[i] = srcNode.mChildNodes[i];
				}
			}
			if (!srcNode.mIsLeafNode) {
				dstNode.mChildNodes[i] = srcNode.mChildNodes[i];
			}
		} else {
			// Clear the median key (element).
			medianKeyIndex = dstNode.mNumKeys;
			dstNode.mKeys[medianKeyIndex] = 0;
			dstNode.mObjects[medianKeyIndex] = null;
			// Copy the srcNode's elements into dstNode.
			int offset = medianKeyIndex + 1;
			int i;
			for (i = 0; i < srcNode.mNumKeys; i++) {
				dstNode.mKeys[offset + i] = srcNode.mKeys[i];
				dstNode.mObjects[offset + i] = srcNode.mObjects[i];
				if (!srcNode.mIsLeafNode) {
					dstNode.mChildNodes[offset + i] = srcNode.mChildNodes[i];
				}
			}
			if (!srcNode.mIsLeafNode) {
				dstNode.mChildNodes[offset + i] = srcNode.mChildNodes[i];
			}
		}
		dstNode.mNumKeys += srcNode.mNumKeys;
		return medianKeyIndex;

	}*/

}