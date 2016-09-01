/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twak.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author twak
 */
public class StringTree <E>
{
    public Map<String, StringTree<E>> children = new HashMap();
    public Map<String, E> contents = new HashMap();
    
    public void add(String s, E e) {
        
        String[] tokens = s.split("/");
        StringTree<E> tree = this;
        
        for (int i = 0; i < tokens.length -1; i++)
            tree = tree.getOrCreateChild(tokens[i]);
        
        tree.contents.put(tokens[tokens.length-1], e);
    }
    
    public StringTree getOrCreateChild( String s )
    {
        if (contents.containsKey(s))
            throw new ThatsADirectoryError();
        
        StringTree<E> child = children.get(s);
        if (child == null)
            children.put (s, child = new StringTree<E>());
        
        return child;
    }

    public boolean isChild(String s)
    {
        return children.containsKey(s);
    }

    public E get(String s)
    {
        if (s == null)
            return null;
        
        String[] tokens = s.split("/");
        StringTree<E> tree = this;
        
        for (int i = 0; i < tokens.length -1; i++)
        {
            tree = tree.children.get ( tokens[i] );
            if (tree == null)
                return null;
        }
        
        return tree.contents.get(tokens[tokens.length - 1]);
    }

    public Iterable<E> recursiveAll()
    {
        List<E> out = new ArrayList();
        recursiveAll_(out);
        return out;
    }
    
    private void recursiveAll_( List<E> out ) 
    {
        out.addAll (contents.values());
        for (StringTree<E> st:children.values())
            st.recursiveAll_( out );
    }
    
    private static class ThatsADirectoryError extends Error{}
}
