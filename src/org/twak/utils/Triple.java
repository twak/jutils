package org.twak.utils;

/**
 * @author tomkelly
 */
public class Triple<A, B, C>
{
	private A a;

	private B b;

	private C c;

	public Triple(A element1, B element2, C element3)
	{
		this.a = element1;
		this.b = element2;
		this.c = element3;
	}

	public A first()
	{
		return a;
	}

	public B second()
	{
		return b;
	}

	public C third()
	{
		return c;
	}

	public String toString()
	{
		return "(" + a + "," + b + "," + c + ")";
	}

	public void set1(A element1)
	{
		this.a = element1;
	}

	public void set2(B element2)
	{
		this.b = element2;
	}

	public void set3(C element3)
	{
		this.c = element3;
	}
}
