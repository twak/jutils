package org.twak.utils;

import java.util.Random;

public class Distribution {

	double min, max;
	int[] buckets;
	int count = 0;

	public Distribution (double min, double max) {
		this (min, max, 1);
	}

	public Distribution(double min, double max, double step) {
		this.min = min;
		this.max = max;

		buckets = new int[ (int)( (max-min) / step ) ];
	}

	public void add(double val) {
		if (val < min || val >= max)
			throw new ArrayIndexOutOfBoundsException(  );

		buckets[(int)( (val  - min) * buckets.length / (max-min))] ++;

		count++;
	}

	public int getI(Random randy) {
		return (int) get(randy);
	}

	public double get( Random randy) {

		double target = randy.nextInt( count );
		double sofar = 0;
		for (int i = 0; i < buckets.length; i++) {

			if (target >= sofar && target < sofar + buckets[i])
				return i * (max-min) / buckets.length + min;

			sofar += buckets[i];
		}

		throw new Error("not in range?");
	}

}
