package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapMapList <I1, I2, O>{

	public Map<I1, Map<I2,List<O>>> map = new LinkedHashMap();
	
	public void put (I1 i1, I2 i2, O o) {
		
		Map<I2,List<O>> m2 = map.get(i1);
		if (m2 == null)
			map.put (i1, m2 = new LinkedHashMap<>());
		
		List<O> m3 = m2.get( i2 );
		if (m3 == null)
			m2.put(i2, m3 = new ArrayList<>());
		
		m3.add(o);
	}

	public List<O> get (I1 i1, I2 i2) {
		
		Map<I2,List<O>> m2 = map.get(i1);
		
		if (m2 == null)
			return Collections.emptyList();
		
		List<O> out = m2.get(i2);
		
		if (out == null)
			return Collections.emptyList();
		
		return out;
	}
	
}
