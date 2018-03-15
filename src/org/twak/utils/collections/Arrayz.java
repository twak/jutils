package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author twak
 */
public class Arrayz {

	public static int max(int... array) {
		int max = Integer.MIN_VALUE;
		int maxI = -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] > max) {
				maxI = i;
				max = array[i];
			}

		return maxI;
	}

	public static int max(double... array) {
		double max = -Double.MAX_VALUE;
		int maxI = -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] > max) {
				maxI = i;
				max = array[i];
			}

		return maxI;
	}

	public static int min(double...array) {
		double min = Double.MAX_VALUE;
		int minI = -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] < min) {
				minI = i;
				min = array[i];
			}

		return minI;
	}

	public static <E> Set<E>  asSet(Object... to) {
		Set<E> h = new HashSet();
		
		for (Object o : to)
			h.add((E)o);
		
		return h;
	}

	public static int indexOf(Object o, Object[] things) {
		for (int i = 0; i < things.length; i++)
			if (things[i] == o)
				return i;
		return 0;
	}

	public static Object[] reverse(Object[] selectedValues) {
		Object[] out = new Object[selectedValues.length];

		for (int i = 0; i < selectedValues.length; i++)
			out[selectedValues.length - i - 1] = selectedValues[i];

		return out;
	}

	public static List<Object> newElements(Object[] original, Object[] neu) {

		Set<Object> orig = new HashSet();
		for (Object o : original)
			orig.add(o);

		List<Object> out = new ArrayList();

		int i = 0;
		for (Object o : neu)
			if (!orig.contains(o))
				out.add(o);

		return out;
	}

	public static int countTrue(boolean[] in) {
		int count = 0;

		for (boolean b : in)
			if (b)
				count++;

		return count;
	}

	public static int onlyTrueIndex(int index, boolean[] in) {
		int count = 0;
		for (int i = 0; i < in.length; i++)
			if (in[i]) {
				if (i == index)
					return count;

				count++;
			}
		return -1;
	}

	public static float[] toFloatArray(List<Float> coords) {
		float[] out = new float[coords.size()];
		for (int i = 0; i < coords.size(); i++)
			out[i] = coords.get(i);

		return out;
	}
	
	public static double[] toDoubleArray(List<Double> coords) {
		
		double[] out = new double[coords.size()];
		for (int i = 0; i < coords.size(); i++)
			out[i] = coords.get(i);
		
		return out;
	}
	
	public static double[] toDoubleArray(float[] coords) {
		
		double[] out = new double[coords.length];
		for (int i = 0; i < coords.length; i++)
			out[i] = coords[i];
		
		return out;
	}

	public static int[] toIntArray(List<Integer> inds) {
		int[] out = new int[inds.size()];
		for (int i = 0; i < inds.size(); i++)
			out[i] = inds.get(i);

		return out;
	}

	public static float[] sub(float[] a, float[] b) {
		assert a.length == b.length;
		float[] out = new float[a.length];
		for (int i = 0; i < a.length; i++)
			out[i] = a[i] - b[i];

		return out;
	}

	public static float[] cross(float[] a, float[] b) {
		assert a.length == 3 && b.length == 3;

		return new float[] { a[1] * b[2] + a[2] * b[1], a[2] * b[0] + a[0] * b[2], a[0] * b[1] + a[1] * b[0], };
	}
}
