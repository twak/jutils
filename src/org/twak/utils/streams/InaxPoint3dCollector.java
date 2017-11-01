package org.twak.utils.streams;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class InaxPoint3dCollector implements Collector<Tuple3d, double[], double[]> {

	@Override
	public BiConsumer<double[], Tuple3d> accumulator() {
		return (minMax, p) -> {
			minMax[0] = Math.min(p.x, minMax[0]);
			minMax[1] = Math.max(p.x, minMax[1]);
			minMax[2] = Math.min(p.y, minMax[2]);
			minMax[3] = Math.max(p.y, minMax[3]);
			minMax[4] = Math.min(p.z, minMax[4]);
			minMax[5] = Math.max(p.z, minMax[5]);
		};
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return EnumSet.of(Characteristics.UNORDERED);
	}

	@Override
	public BinaryOperator<double[]> combiner() {
		return (a,b) -> new double[] { 
				Math.min ( a[0], b[0] ), 
				Math.max ( a[1], b[1] ),
				Math.min ( a[2], b[2] ), 
				Math.max ( a[3], b[3] ),
				Math.min ( a[4], b[4] ), 
				Math.max ( a[5], b[5] ),
		};
	}

	@Override
	public Function<double[], double[]> finisher() {
		return a -> a;
	}

	@Override
	public Supplier<double[]> supplier() {
		return () -> new double[] { 
				Double.MAX_VALUE, -Double.MAX_VALUE, 
				Double.MAX_VALUE, -Double.MAX_VALUE, 
				Double.MAX_VALUE, -Double.MAX_VALUE
			};
	}
}
