package org.twak.utils;

import java.util.Map;

public class Collectionz {
	
	public static <A,B extends Comparable<B>> Pair<A, B> maxByVal(Map<A, B> in) {

		Pair<A,B> out = null;
		
		for (Map.Entry<A, B> e : in.entrySet()) 
			if (out == null || e.getValue().compareTo(out.second()) < 0)
				out = new Pair<A,B>(e.getKey(), e.getValue());

		return out;
	}
	
}
