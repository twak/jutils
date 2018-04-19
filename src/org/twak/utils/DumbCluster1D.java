package org.twak.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.twak.utils.DumbCluster1D.Cluster;

/**
 *
 * @author twak
 */
public abstract class DumbCluster1D<E> extends ArrayList<Cluster<E>>
{
	public static class Cluster<E> implements Comparable<Cluster<E>> {
		
		public Set<Double> vals = new HashSet();
		public Set<E> things = new HashSet();
		
		public double max, min, mean;
		
		Cluster n, p;
		
		public Cluster (E e, double v1) {
			this.min = this.max = v1;
			vals.add(v1);
			things.add(e);
		}

		double gap() {
			double out = Double.MAX_VALUE;
			if (n != null)
				out = Math.min (out, n.min - max );
			if (p != null)
				out = Math.min (out, min - p.max);
			return out;
		}
		
		@Override
		public int compareTo( Cluster o ) {
			
			Cluster t = (Cluster)o;
			int out =  Double.compare( gap(), t.gap() );
			if (out != 0)
				return out;
			
			return Double.compare (min, t.min);
		}
		
		@Override
		public boolean equals( Object obj ) {
			return obj == this;
		}

		public void mergeDownOrUp( TreeSet<Cluster> byGap ) {
			
			byGap.remove (this);
			if ( p == null || (n != null && n.min - max < min - p.max ) ) {
				
				byGap.remove (n);
				
				max = n.max;
				vals.addAll(n.vals);
				things.addAll(n.things);
				n = n.n;
				
				if (n != null) {
					n.p = this;
					byGap.remove( n );
					byGap.add( n );
				}
				
			}
			else {
				
				byGap.remove (p);
				
				min = p.min;
				vals.addAll(p.vals);
				things.addAll(p.things);
				p = p.p;
				
				if (p != null) {
					p.n = this;
					byGap.remove( p );
					byGap.add( p );
				}
				
			}
			byGap.add(this);
		}
	}
	
	public DumbCluster1D(){}
	public DumbCluster1D (double tol, Collection<E> thingsIn) {
		setup(tol,thingsIn);
	}		
	
	public void setup (double tol, Collection<E> thingsIn) {
		
		List<E> things = new ArrayList<>(thingsIn);
		
		Collections.sort(things, new Comparator<E>() {
			@Override
			public int compare( E o1, E o2 ) {
				return Double.compare ( toDouble( o1 ), toDouble(o2) );
			}
		});
		
		List<Cluster> dists = things.stream().map ( e -> new Cluster<E>(e, toDouble(e)) ).collect( Collectors.toList() );
		
		for (int i = 0; i < dists.size(); i++) {
			if (i > 0 )
				dists.get(i).p = dists.get(i-1);
			if (i < dists.size()-1)
				dists.get(i).n = dists.get(i+1);
		}
		
		TreeSet<Cluster> byGap = new TreeSet(dists);
		
		while (byGap.size() > 1) {
			
			Cluster t = byGap.first();
			
			if (t.gap() > tol)
				break;
			
			t.mergeDownOrUp(byGap);
		}
		
		for (Cluster<E> t : byGap) {
			
			t.mean = t.vals.stream().mapToDouble( x -> x ).average().getAsDouble();
			this.add(t);
		}
		
		Collections.sort (this, new Comparator<Cluster>() {
			@Override
			public int compare( Cluster o1, Cluster o2 ) {
				return Double.compare (o2.max, o1.max);
			}
		});
	}
	
	public abstract double toDouble (E e);
}
