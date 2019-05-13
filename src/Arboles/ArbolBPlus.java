package Arboles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArbolBPlus<t extends Comparable<t>, v>{
	public static enum RangePolicy {
		EXCLUSIVE, INCLUSIVE
	}
	private static final int DEFAULT_BRANCHING_FACTOR = 128;//el valor base que le declaro en el constructor 
	private int branchingFactor;//el factor de derivacion que me maneja la capacidad de nodos internos del arbol
	private Node root;
	public ArbolBPlus() {
		this(DEFAULT_BRANCHING_FACTOR);//le damos al constructor el valor base
	}
	public ArbolBPlus(int branchingFactor) {
		if (branchingFactor <= 2)
			throw new IllegalArgumentException("Illegal branching factor: "+ branchingFactor);
		this.branchingFactor = branchingFactor;
		root = new LeafNode();
	}
	public v search(t key) {
		return root.getValue(key);
	}
	public void insert(t key, v refe) {
		root.insertValue(key, refe);
	}
	public void delete(t key) {
		root.deleteValue(key);
	}
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof ArbolBPlus.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else
					sb.append('\n');
			}
			queue = nextQueue;
		}

		return sb.toString();
	}

	private abstract class Node {
		List<t> keys;

		int keyNumber() {
			return keys.size();
		}

		abstract v getValue(t key);

		abstract void deleteValue(t key);

		abstract void insertValue(t key, v value);

		abstract t getFirstLeafKey();

		abstract List<v> getRange(t key1, RangePolicy policy1, t key2,RangePolicy policy2);

		abstract void merge(Node sibling);

		abstract Node split();

		abstract boolean isOverflow();

		abstract boolean isUnderflow();

		public String toString() {
			return keys.toString();
		}
	}

	private class InternalNode extends Node {
		List<Node> children;

		InternalNode() {
			this.keys = new ArrayList<t>();
			this.children = new ArrayList<Node>();
		}

		@Override
		v getValue(t key) {
			return getChild(key).getValue(key);
		}

		@Override
		void deleteValue(t key) {
			Node child = getChild(key);
			child.deleteValue(key);
			if (child.isUnderflow()) {
				Node childLeftSibling = getChildLeftSibling(key);
				Node childRightSibling = getChildRightSibling(key);
				Node left = childLeftSibling != null ? childLeftSibling : child;
				Node right = childLeftSibling != null ? child
						: childRightSibling;
				left.merge(right);
				deleteChild(right.getFirstLeafKey());
				if (left.isOverflow()) {
					Node sibling = left.split();
					insertChild(sibling.getFirstLeafKey(), sibling);
				}
				if (root.keyNumber() == 0)
					root = left;
			}
		}

		@Override
		void insertValue(t key, v value) {
			Node child = getChild(key);
			child.insertValue(key, value);
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		t getFirstLeafKey() {
			return children.get(0).getFirstLeafKey();
		}

		@Override
		List<v> getRange(t key1, RangePolicy policy1, t key2,RangePolicy policy2) {
			return getChild(key1).getRange(key1, policy1, key2, policy2);
		}

		@Override
		void merge(Node sibling) {
			@SuppressWarnings("unchecked")
			InternalNode node = (InternalNode) sibling;
			keys.add(node.getFirstLeafKey());
			keys.addAll(node.keys);
			children.addAll(node.children);
		}
		@Override
		Node split() {
			int from = keyNumber() / 2 + 1, to = keyNumber();
			InternalNode sibling = new InternalNode();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.children.addAll(children.subList(from, to + 1));

			keys.subList(from - 1, to).clear();
			children.subList(from, to + 1).clear();

			return sibling;
		}

		@Override
		boolean isOverflow() {
			return children.size() > branchingFactor;
		}

		@Override
		boolean isUnderflow() {
			return children.size() < (branchingFactor + 1) / 2;
		}

		Node getChild(t key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			return children.get(childIndex);
		}

		void deleteChild(t key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				children.remove(loc + 1);
			}
		}

		void insertChild(t key, Node child) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (loc >= 0) {
				children.set(childIndex, child);
			} else {
				keys.add(childIndex, key);
				children.add(childIndex + 1, child);
			}
		}

		Node getChildLeftSibling(t key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex > 0)
				return children.get(childIndex - 1);

			return null;
		}

		Node getChildRightSibling(t key) {
			int loc = Collections.binarySearch(keys, key);
			int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
			if (childIndex < keyNumber())
				return children.get(childIndex + 1);

			return null;
		}
	}

	private class LeafNode extends Node {
		List<v> values;
		LeafNode next;

		LeafNode() {
			keys = new ArrayList<t>();
			values = new ArrayList<v>();
		}

		@Override
		v getValue(t key) {
			int loc = Collections.binarySearch(keys, key);
			return loc >= 0 ? values.get(loc) : null;
		}

		@Override
		void deleteValue(t key) {
			int loc = Collections.binarySearch(keys, key);
			if (loc >= 0) {
				keys.remove(loc);
				values.remove(loc);
			}
		}

		@Override
		void insertValue(t key, v value) {
			int loc = Collections.binarySearch(keys, key);
			int valueIndex = loc >= 0 ? loc : -loc - 1;
			if (loc >= 0) {
				values.set(valueIndex, value);
			} else {
				keys.add(valueIndex, key);
				values.add(valueIndex, value);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		@Override
		t getFirstLeafKey() {
			return keys.get(0);
		}

		@Override
		List<v> getRange(t key1, RangePolicy policy1, t key2,RangePolicy policy2) {
			List<v> result = new LinkedList<v>();
			LeafNode node = this;
			while (node != null) {
				Iterator<t> kIt = node.keys.iterator();
				Iterator<v> vIt = node.values.iterator();
				while (kIt.hasNext()) {
					t key = kIt.next();
					v value = vIt.next();
					int cmp1 = key.compareTo(key1);
					int cmp2 = key.compareTo(key2);
					if (((policy1 == RangePolicy.EXCLUSIVE && cmp1 > 0) || (policy1 == RangePolicy.INCLUSIVE && cmp1 >= 0))
							&& ((policy2 == RangePolicy.EXCLUSIVE && cmp2 < 0) || (policy2 == RangePolicy.INCLUSIVE && cmp2 <= 0)))
						result.add(value);
					else if ((policy2 == RangePolicy.EXCLUSIVE && cmp2 >= 0)
							|| (policy2 == RangePolicy.INCLUSIVE && cmp2 > 0))
						return result;
				}
				node = node.next;
			}
			return result;
		}
		@Override
		void merge(Node sibling) {
			@SuppressWarnings("unchecked")
			LeafNode node = (LeafNode) sibling;
			keys.addAll(node.keys);
			values.addAll(node.values);
			next = node.next;
		}
		@Override
		Node split() {
			LeafNode sibling = new LeafNode();
			int from = (keyNumber() + 1) / 2, to = keyNumber();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.values.addAll(values.subList(from, to));
			keys.subList(from, to).clear();
			values.subList(from, to).clear();
			sibling.next = next;
			next = sibling;
			return sibling;
		}
		@Override
		boolean isOverflow() {
			return values.size() > branchingFactor - 1;
		}
		@Override
		boolean isUnderflow() {
			return values.size() < branchingFactor / 2;
		}
	}
}
