package org.twak.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streamz {
	
	public static <T> Stream<T> stream(Iterable<T> iterable) {
	    return StreamSupport.stream(
	        Spliterators.spliteratorUnknownSize(
	            iterable.iterator(),
	            Spliterator.ORDERED
	        ),
	        false
	    );
	}
	
	public static <T> Stream<T>  stream(T...ts){
		return Arrays.stream( ts );
	}

	public static DoubleStream dStream( List<Double> stream ) {
		return stream.stream().mapToDouble( x -> x );
	}
}
