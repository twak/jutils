package org.twak.utils.collections;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Get<E> implements Collector<E, TreeSet<E>, E> {
	
	ToDouble<E> toDouble;
	boolean max;
	
	public interface ToDouble<F> {
		public double toDouble(F f);
	}
	
	public static <E> Get<E> max (ToDouble<E> toDouble) {
		return new Get<>( toDouble, true );
	}
	
	public static <E> Get<E> min (ToDouble<E> toDouble) {
		return new Get<>( toDouble, false );
	}
	
	public Get (ToDouble<E> toDouble, boolean max) {
		this.toDouble = toDouble;
		this.max = max;
	}
	
	@Override
	public Supplier<TreeSet<E>> supplier() {
		return () -> new TreeSet<E>(new Comparator<E>() {
			@Override
			public int compare( E a, E b ) {
				return -Double.compare( toDouble.toDouble( a ), toDouble.toDouble( b ) );
			}
		});
	}

	@Override
	public BiConsumer<TreeSet<E>, E> accumulator() {
		return (l, v) -> l.add(v);
	}

	@Override
	public BinaryOperator<TreeSet<E>> combiner() {
		return (a,b) -> {
			TreeSet<E> out = new TreeSet<>();
			out.addAll( a );
			out.addAll( b );
			return out;
		};
	}

	@Override
	public Function<TreeSet<E>, E> finisher() {
		return l -> max ? l.pollFirst() : l.pollLast();
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return EnumSet.of(Characteristics.UNORDERED);
	}
	
	public static void main(String[] args) {
		List<String> s = Listz.from( "one", "three", "four", "one bazillion", "0" );
		
		System.out.println("longest " + s.stream().collect( new Get<>(f -> f.length(), true) ) );
		System.out.println("shortest " + s.stream().collect( new Get<>(f -> f.length(), false) ) );
		
	}
}
