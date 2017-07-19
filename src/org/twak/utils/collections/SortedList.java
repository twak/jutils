package org.twak.utils.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedList<E> extends AbstractList<E> {

    private ArrayList<E> internalList = new ArrayList<E>();

    public Comparator<E> sorter = null;
    
	public SortedList( Comparator<E> sorter ) {
		this.sorter = sorter;
	};
    
    public SortedList( Comparator<E> sorter, List<E> values ) {
    	this (sorter);
    	internalList = new ArrayList<>(values);
	}

	@Override 
    public void add(int position, E e) {
        internalList.add(e);
        if (sorter != null)
        	Collections.sort(internalList, sorter);
    }

    @Override
    public E get(int i) {
        return internalList.get(i);
    }

    @Override
    public int size() {
        return internalList.size();
    }

}