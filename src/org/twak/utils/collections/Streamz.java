package org.twak.utils.collections;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
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
	
	// https://stackoverflow.com/questions/14165517/processbuilder-forwarding-stdout-and-stderr-of-started-processes-without-blocki
	public static void inheritIO(final InputStream src, final PrintStream dest) { // NO THE OTHER STREAM
	    new Thread(new Runnable() {
	        public void run() {
	            Scanner sc = new Scanner(src);
	            while (sc.hasNextLine()) {
	                dest.println(sc.nextLine());
	            }
	        }
	    }).start();
	}
}
