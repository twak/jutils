package org.twak.utils;

/**
 * Adapted from ibm.com
 * 
 * @author twak
 * 
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {
	private A element1;

	private B element2;

	public Pair() {
	}

	public Pair(A element1, B element2) {
		this.element1 = element1;
		this.element2 = element2;
	}

	public A first() {
		return element1;
	}

	public B second() {
		return element2;
	}

	public String toString() {
		return "(" + element1 + "," + element2 + ")";
	}

	public void set1(A element1) {
		this.element1 = element1;
	}

	public void set2(B element2) {
		this.element2 = element2;
	}

	@Override
	public boolean equals(Object obj) {

		try {
			Pair other = (Pair) obj;

			return other.element1.equals(element1) && other.element2.equals(element2);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return  (int) ( ((long) element1.hashCode() + (long) element2.hashCode() ) % Integer.MAX_VALUE );
	}
}
