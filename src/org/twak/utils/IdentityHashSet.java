package org.twak.utils;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdentityHashSet<K> implements Set<K> {

	IdentityHashMap<K, K> map = new IdentityHashMap<>();

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains( Object o ) {
		return map.containsKey( o );
	}

	@Override
	public Iterator<K> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray( T[] a ) {
		return map.keySet().toArray( a );
	}

	@Override
	public boolean add( K e ) {
		return map.put( e, e ) == null;
	}

	@Override
	public boolean remove( Object o ) {
		return map.remove (o) != null;
	}

	@Override
	public boolean containsAll( Collection<?> c ) {
		return c.stream().allMatch( x -> map.containsKey( c ) );
	}

	@Override
	public boolean addAll( Collection<? extends K> c ) {

		boolean added = false;
		
		for (K o : c)
			added |= null != map.put( o, o );
		
		return added;
	}

	@Override
	public boolean retainAll( Collection<?> c ) {
		
		Iterator<Map.Entry<K,K>> it = map.entrySet().iterator();
		
		boolean changed = false;
		
		while (it.hasNext()) {
			if (!c.contains( it.next().getKey() )) {
				it.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean removeAll( Collection<?> c ) {
		
		boolean removed = false;
		
		for (Object o : c) {
			removed |= null != map.remove( o );
		}
		
		return removed;

	}

	@Override
	public void clear() {
		map.clear();
	}
	

}
