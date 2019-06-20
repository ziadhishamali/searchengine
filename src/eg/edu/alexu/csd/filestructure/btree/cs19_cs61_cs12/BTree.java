package eg.edu.alexu.csd.filestructure.btree.cs19_cs61_cs12;

import java.util.List;

import javax.management.RuntimeErrorException;

import eg.edu.alexu.csd.filestructure.btree.IBTree;
import eg.edu.alexu.csd.filestructure.btree.IBTreeNode;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

	private IBTreeNode<K, V> root;
	private IBTreeNode<K, V> parent;
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
		while (i < x.getNumOfKeys() && key.compareTo(x.getKeys().get(i)) > 0) {
			i++;
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
		// recursively call using the appropriate child node
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
		if (key == null) {
			throw new RuntimeErrorException(null);
		}

		IBTreeNode<K, V> p = new BTreeNode<>();
		IBTreeNode<K, V> x = new BTreeNode<>();
		IBTreeNode<K, V> y = new BTreeNode<>();
		IBTreeNode<K, V> z = new BTreeNode<>();
		if ((x = find(key)) == null) {
			return false;
		}
		p = this.parent; // PARENT
		this.parent = null;

		int keyIndex = 0;
		int nodeIndex = 0;
		List<K> temp = x.getKeys();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) == key) {
				keyIndex = i;
			}
		}

		for (int i = 0; i < p.getChildren().size(); i++) {
			if (p.getChildren().get(i) == x) {
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
		
		boolean shift = false;  //False: ShiftLeft
								//Right: ShiftRight

		/* ************ CASES ******************** */
		if (x.isLeaf()) {
			if (x.getNumOfKeys() >= minDegree) { // CASE1 "Trivial"
				List<K> xKeys = x.getKeys();
				List<V> xValues = x.getValues();
				xKeys.remove(keyIndex);
				xValues.remove(keyIndex);
				x.setKeys(xKeys);
				x.setValues(xValues);
			} else {
				if (z != null) {  //LeftSiblingFirst
					if (z.getNumOfKeys() >= minDegree) { // CASE2(a) "Trivial" 
						K parentKey = parent.getKeys().get(keyIndex - 1);
						K siblingKey = parent.getChildren().get(nodeIndex - 1).getKeys()
								.get(parent.getChildren().get(nodeIndex - 1).getNumOfKeys() - 1);

						z.getKeys().remove(z.getNumOfKeys() - 1);
						z.getValues().remove(z.getNumOfKeys() - 1);
						p.getKeys().remove(keyIndex - 1);
						p.getValues().remove(keyIndex - 1);
						p.getKeys().add(siblingKey);
						p.getValues().add(parent.getChildren().get(nodeIndex - 1).getValues()
								.get(parent.getChildren().get(nodeIndex - 1).getNumOfKeys() - 1));
						// Sort
						x.getKeys().remove(keyIndex);
						x.getValues().remove(keyIndex);
						x.getKeys().add(parentKey);
						x.getValues().add(parent.getValues().get(keyIndex - 1));
						// Sort
						return true;

					} else if (y != null){	//rightSiblingSecond
						if (y.getNumOfKeys() >= minDegree) {	// CASE2(a) "Trivial"
							K parentKey = parent.getKeys().get(keyIndex);
							K siblingKey = parent.getChildren().get(nodeIndex + 1).getKeys().get(0);

							y.getKeys().remove(0);
							y.getValues().remove(0);
							p.getKeys().remove(keyIndex);
							p.getValues().remove(keyIndex);
							p.getKeys().add(siblingKey);
							p.getValues().add(parent.getChildren().get(nodeIndex + 1).getValues().get(0));
							// Sort
							x.getKeys().remove(keyIndex);
							x.getValues().remove(keyIndex);
							x.getKeys().add(parentKey);
							x.getValues().add(parent.getValues().get(keyIndex));
							// Sort
							return true;
						}
					}else { // CASE2(b) "Trick"
						shift = false;
						int parentKeyIndex = keyIndex - 1;
						merge(z, x, p, parentKeyIndex, shift, false);
						K parentKey = parent.getKeys().get(keyIndex - 1);
						K siblingKey = parent.getChildren().get(nodeIndex - 1).getKeys()
								.get(parent.getChildren().get(nodeIndex - 1).getNumOfKeys() - 1);
						
						
						z.getKeys().remove(keyIndex);
						z.getValues().remove(keyIndex);
						if (parent.getNumOfKeys() >= minDegree) {
							parent.getKeys().remove(parentKeyIndex);
							parent.getValues().remove(parentKeyIndex);
						} else {
							//parent.getKeys().remove(parentKeyIndex);
							//parent.getValues().remove(parentKeyIndex);
							internalNodesFIX_UP(parentKey);
							//delete(parentKey);
							//fix
						}
						
					}
				} else if (y != null) {  //rightSiblingSecond
					if (y.getNumOfKeys() >= minDegree) { // CASE2(a) "Trivial"
						K parentKey = parent.getKeys().get(keyIndex);
						K siblingKey = parent.getChildren().get(nodeIndex + 1).getKeys().get(0);

						y.getKeys().remove(0);
						y.getValues().remove(0);
						p.getKeys().remove(keyIndex);
						p.getValues().remove(keyIndex);
						p.getKeys().add(siblingKey);
						p.getValues().add(parent.getChildren().get(nodeIndex + 1).getValues().get(0));
						// Sort
						x.getKeys().remove(keyIndex);
						x.getValues().remove(keyIndex);
						x.getKeys().add(parentKey);
						x.getValues().add(parent.getValues().get(keyIndex));
						// Sort
						return true;

					} else { // CASE2(b) "Trick"
						shift = true;
						int parentKeyIndex = keyIndex ;
						merge(y, x ,p ,parentKeyIndex, shift, false);
						K parentKey = parent.getKeys().get(keyIndex);
						K siblingKey = parent.getChildren().get(nodeIndex + 1).getKeys().get(0);

						y.getKeys().remove(keyIndex);
						y.getValues().remove(keyIndex);
						if (parent.getNumOfKeys() >= minDegree) {
							parent.getKeys().remove(parentKeyIndex);
							parent.getValues().remove(parentKeyIndex);
						} else {
							//parent.getKeys().remove(parentKeyIndex);
							//parent.getValues().remove(parentKeyIndex);
							internalNodesFIX_UP(parentKey);
							//delete(parentKey);
							//fix
						}
					}
				}
			}
		} else {
			IBTreeNode<K, V> predecessor = new BTreeNode<>();
			predecessor = getPredecesor(x, keyIndex);
			IBTreeNode<K, V> successor = new BTreeNode<>();
			successor = getSuccessor(x, 0);
			if (predecessor.getNumOfKeys() >= minDegree) { // CASE3(a) "Trivial"
				K predecessorKey = predecessor.getKeys().get(keyIndex);
				V predecessorValue = predecessor.getValues().get(keyIndex);

				predecessor.getKeys().remove(keyIndex);
				predecessor.getValues().remove(keyIndex);
				x.getKeys().add(predecessorKey);
				x.getValues().add(predecessorValue);
				return true;

			} else {
				if (successor.getNumOfKeys() >= minDegree) { // CASE3(b) "Trivial"
					K successorKey = successor.getKeys().get(0);
					V successorrValue = successor.getValues().get(0);

					successor.getKeys().remove(0);
					successor.getValues().remove(0);
					x.getKeys().add(successorKey);
					x.getValues().add(successorrValue);
					return true;

				} else { // CASE3(c) "Trick"
					//merge(predecessor, successor);

					predecessor.getKeys().add(x.getKeys().get(keyIndex));
					predecessor.getValues().add(x.getValues().get(keyIndex));
					// Sort
					/*int newIndex = predecessor.getKeys().indexOf(x.getKeys().get(keyIndex));
					predecessor.getKeys().remove(newIndex);
					predecessor.getValues().remove(newIndex);
					delete(key);*/
					
					/*K predecessorKey = predecessor.getKeys().get(predecessor.getKeys().size() - 1);
					V predecessorValue = predecessor.getValues().get(predecessor.getValues().size() - 1);
					node.getKeys().set(i, predecessorKey);
					node.getValues().set(i, predecessorValue);
					deleteRecursively(predecessor, predecessorKey);*/
					 	
				}
			}
		}
		return false;
	}

	void merge(IBTreeNode<K, V> x, IBTreeNode<K, V> y, IBTreeNode<K, V> parent, int indexOfKeyInParent, boolean shift, boolean fix) {
		List<K> xKeys = x.getKeys();
		List<K> yKeys = y.getKeys();
		List<V> xValues = x.getValues();
		List<V> yValues = y.getValues();
		
		List<K> newKeys = xKeys;
		List<V> newValues = xValues;
		
		if (!fix) {
			newKeys.add(parent.getKeys().get(indexOfKeyInParent));
			newValues.add(parent.getValues().get(indexOfKeyInParent));
		}
		
		for (int i = 0; i < yKeys.size(); i++) {
			newKeys.add(yKeys.get(i));
		}
		for (int i = 0; i < yValues.size(); i++) {
			newValues.add(yValues.get(i));
		}
		x.setKeys(newKeys);
		x.setValues(newValues);
		
		if (shift) {
			parent.getChildren().remove(indexOfKeyInParent);
		} else {
			parent.getChildren().remove(indexOfKeyInParent + 1);
		}
	}
	
	void internalNodesFIX_UP(K key) {
		IBTreeNode<K, V> x = new BTreeNode<>();
		IBTreeNode<K, V> p = new BTreeNode<>();
		IBTreeNode<K, V> y = new BTreeNode<>();
		IBTreeNode<K, V> z = new BTreeNode<>();

		if ((x = find(key)) == null) {
			//throw;
		}
		p = this.parent; // PARENT
		this.parent = null;
		int keyIndex = 0;
		int nodeIndex = 0;
		List<K> temp = x.getKeys();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) == key) {
				keyIndex = i;
			}
		}
		for (int i = 0; i < p.getChildren().size(); i++) {
			if (p.getChildren().get(i) == x) {
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
		
		int parentIndex = keyIndex - 1;
		x.getKeys().remove(keyIndex);
		x.getValues().remove(keyIndex);
		
		if (x.getNumOfKeys() == minDegree - 1) {
			if (z != null) {
				K parentKey = parent.getKeys().get(keyIndex - 1);
				V parentValue = parent.getValues().get(keyIndex - 1);
				//x.getKeys().add(0, parentKey);
				//x.getValues().add(0, parentValue);
				merge(z, parent, parent, parentIndex, false, true);
				internalNodesFIX_UP(parentKey);
			} else if (y != null) {
				K parentKey = parent.getKeys().get(keyIndex);
				V parentValue = parent.getValues().get(keyIndex);
				//x.getKeys().add(0, parentKey);
				//x.getValues().add(0, parentValue);
				merge(parent, z, parent, parentIndex, true, true);
				internalNodesFIX_UP(parentKey);
			}
		}
	}

	private IBTreeNode<K, V> getPredecesor(IBTreeNode<K, V> x, int keyIndex) {
		IBTreeNode<K, V> z = x.getChildren().get(keyIndex);
		while (!z.isLeaf()) {
			z = z.getChildren().get(z.getChildren().size() - 1);
		}
		return z;
	}

	private IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> x, int keyIndex) {
		IBTreeNode<K, V> z = x.getChildren().get(keyIndex);
		while (!z.isLeaf()) {
			z = z.getChildren().get(0);
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