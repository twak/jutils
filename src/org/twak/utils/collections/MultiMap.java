package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HashMap backed mutli-item hash. eg a list for every entry in the hash table
 * 
 * @author twak
 */
public class MultiMap<A, B> //implements Map<A,List<B>>
{
	public Map<A, List<B>> map = new LinkedHashMap();

	public MultiMap() {
	}
	
	public MultiMap(A a , B...bs) {
		for (B b : bs )
			put (a, b);
	}

	public MultiMap( MultiMap<A, B> other ) {
		for ( A a : other.map.keySet() )
			for ( B b : other.map.get( a ) )
				put( a, b );
	}

	public void addEmpty( A a ) {
		if ( !map.containsKey( a ) )
			map.put( a, new ArrayList() );
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey( Object key ) {
		return map.containsKey( key );
	}

	public boolean containsValue( Object value ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public List<B> get( A key ) {
		List<B> out = map.get( key );
		if ( out == null )
			return new ArrayList();
		return out;
	}

	public List<B> getOrAdd( A key ) {
		List<B> out = map.get( key );
		if ( out == null ) {
			List<B> b = new ArrayList();
			map.put( key, b );
			return b;
		}
		return out;
	}

	public boolean remove( A key, B value ) {
		List<B> out = map.get( key );
		if ( out == null )
			return false;

		boolean res = out.remove( value );

		if ( out.isEmpty() )
			map.remove( key );

		return res;
	}

	public void put( A key, B value ) {
		List<B> out = map.get( key );
		if ( out == null ) {
			out = new ArrayList();
			map.put( key, out );
		}
		out.add( value );
	}

	public void put( A key, B value, boolean dupeCheck ) {
		List<B> out = map.get( key );
		if ( out == null ) {
			out = new ArrayList();
			map.put( key, out );
		}
		if ( !dupeCheck || !out.contains( value ) )
			out.add( value );
	}

	public List<B> remove( A key ) {
		return map.remove( key );
	}

	public void putAll( MultiMap<? extends A, ? extends B> m ) {
		for ( A a : m.map.keySet() )
			for ( B b : m.map.get( a ) )
				put( a, b );
	}

	public void putAll( Map<? extends A, ? extends B> m ) {
		for ( Map.Entry<? extends A, ? extends B> entry : m.entrySet() )
			put( entry.getKey(), entry.getValue() );
	}

	public void clear() {
		map.clear();
	}

	public Set<A> keySet() {
		return map.keySet();
	}

	public List<B> valueList() {
		return map.values().stream().flatMap( x -> x.stream() ).collect( Collectors.toList() );
	}

	public Collection<List<B>> values() {
		return map.values();
	}

	public Set<Entry<A, List<B>>> entrySet() {
		return map.entrySet();
	}

	public void putAll( A a, Iterable<B> bs, boolean dupeCheck ) {
		for ( B b : bs )
			put( a, b, dupeCheck );
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( "[\n" );
		for ( A a : map.keySet() ) {
			sb.append( a.toString() + " || " );
			for ( B b : map.get( a ) )
				sb.append( b + ", " );
			sb.append( "\n" );
		}
		sb.append( "]\n" );
		return sb.toString();
	}

	public void removeAll( A... a ) {
		for ( A aa : a )
			remove( aa );
	}

	public boolean contains( A a, B b ) {

		for ( B b2 : get( a ) )
			if ( b.equals( b2 ) )
				return true;

		return false;
	}

	public long countValue() {
		return map.values().stream().flatMap( e -> e.stream() ).count();
	}

	public int minListSize() {
		if (map.isEmpty())
			return 0;
		return values().stream().mapToInt( m -> m.size() ).min().getAsInt();
	}

	public void putAll( A a, Iterable<B> bs ) {
		for (B b : bs)
			put (a, b);
	}
}
