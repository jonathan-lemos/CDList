package com.company;
// i'd like to work at oracle where i get to screw people all day and launch lawsuits instead of making a good language

import java.util.HashSet;
import java.util.Set;

public class CDList<E> implements Cloneable {
	private static class Node<E> {
		private E val;
		private Node<E> prev;
		private Node<E> next;

		public Node(E val) {this(val, null, null);}
		public Node(E val, Node<E> prev, Node<E> next) {this.val = val; this.prev = prev; this.next = next;}

		// getters and setters are GoOd pRaCtIcE even though they're functionally identical to public members which are eViL
		public E getVal() { return val; }
		public Node<E> getPrev() { return prev; }
		public Node<E> getNext() { return next; }
		public void setPrev(Node<E> p) { prev = p; }
		public void setNext(Node<E> n) { next = n; }

		// inserts a node before this one
		public void insertPrev(E val) {
			Node<E> tmp = new Node<>(val);
			tmp.prev = this.prev;
			tmp.next = this;
			this.prev.next = tmp;
			this.prev = tmp;
		}

		// inserts a node after this one
		public void insertNext(E val) {
			Node<E> tmp = new Node<>(val);
			tmp.prev = this;
			tmp.next = this.next;
			this.next.prev = tmp;
			this.next = tmp;
		}

		// removes the node before this one
		public void removePrev() {
			this.prev.prev.next = this;
			this.prev = this.prev.prev;
		}

		// removes a node after this one
		public void removeNext() {
			this.next.next.prev = this;
			this.next = this.next.next;
		}
	}

	private Node<E> head = null;
	private int size = 0;

	public CDList() {}

	public CDList(CDList<E> other) {
		CDList<E> cl = other.clone();
		this.head = cl.head;
		this.size = cl.size;
	}

	public int size() {return this.size;}

	public boolean isEmpty() {return this.size == 0;}

	public E first() {return this.head == null ? null : this.head.val;}

	public E last() {return this.head == null ? null : this.head.prev.val;}

	public void rotate() {
		if (head == null) return;
		head = head.getNext();
	}

	public void rotateBackward() {
		if (head == null) return;
		head = head.prev;
	}

	public void addFirst(E e) {
		if (head == null) {
			head = new Node<>(e);
			head.next = head;
			head.prev = head;
			size = 1;
			return;
		}
		this.head.insertPrev(e);
		this.head = this.head.prev;
		size++;
	}

	public void addLast(E e) {
		if (head == null) {
			addFirst(e);
			return;
		}
		this.head.getPrev().insertNext(e);
		size++;
	}

	public E removeFirst() {
		if (head == null) return null;
		if (head == head.next) {
			head = null;
			return null;
		}
		else {
			this.rotate();
			head.removePrev();
			size--;
		}
		return head.getVal();
	}

	public E removeLast() {
		if (head == null) return null;
		if (head.prev == head) {
			head = null;
			return null;
		}
		else {
			head.removePrev();
			size--;
		}
		return head.getVal();
	}

	// java is a great language. as such an entire interface is needed for a lambda
	public interface Lambda<E> {
		void run(E val);
	}

	private void forEach(Lambda<E> func) {
		if (this.head == null) return;
		Node<E> ptr = this.head;
		for (int i = 0; i < this.size; ++i, ptr = ptr.next) func.run(ptr.val);
	}

	@Override
	public CDList<E> clone() {
		int i;
		Node<E> ptr;
		CDList<E> ret = new CDList<E>();
		this.forEach(ret::addLast);
		return ret;
	}

	@Override
	public boolean equals(Object o) {
		// java is a great language. as such it implements "type erasure" which means it can't check if it's an instance of CDList<E>
		if (!(o instanceof CDList)) return false;
		// java is a great language, so this cast is required
		CDList<E> other = (CDList<E>)o;
		if (this.size != other.size()) return false;

		Node<E> n1 = this.head;
		int i;
		for (i = 0; i < this.size; ++i, n1 = n1.next) {
			if (n1.getVal().equals(other.head.getVal())) {
				break;
			}
		}
		if (i == this.size) return false;

		Node<E> n2 = other.head;
		for (i = 0; i < this.size; ++i, n1 = n1.next, n2 = n2.next) {
			if (!n1.getVal().equals(n2.getVal())) {
				return false;
			}
		}
		return true;
	}

	public void attach(CDList<E> other) {
		Node<E> thisTail = this.head.getPrev();
		Node<E> otherTail = other.head.getPrev();

		thisTail.setNext(other.head);
		other.head.setPrev(thisTail);
		otherTail.setNext(this.head);
		this.head.setPrev(otherTail);

		this.size += other.size;
		other.size = this.size;
	}

	public void removeDuplicates() {
		Set<E> elems = new HashSet<>();
		Node<E> n1 = this.head;
		while (n1.getNext() != this.head) {
			elems.add(n1.getVal());
			if (elems.contains(n1.next.getVal())) {
				n1.removeNext();
				size--;
			}
			else {
				n1 = n1.getNext();
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.forEach(e -> {
			sb.append(e);
			sb.append(" -> ");
		});
		sb.append("repeat...");
		return sb.toString();
	}

	public void printList() {
		System.out.println(this.toString());
	}
}
