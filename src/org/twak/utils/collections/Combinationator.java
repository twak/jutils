package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * All possible (seqLength^I.size()) combinations of a given list
 * 
 * @author twak
 */
public class Combinationator<I> implements Iterable<List<I>>
{
    List<I> al;
    int seqLen;
    
    public Combinationator( List<I> ait, int seqLen ) {
        this.seqLen = seqLen;
        this.al = ait;
    }
    
    public Iterator<I> getAIterator()
    {
        return al.iterator();
    }
    
    public class It implements Iterator<List<I>>
    {
        int[] current;
        boolean done = false;
        
        public It()
        {
            current = new int[ seqLen ];
        }
        
        private List<I> get( int[] current )
        {
            List<I> out = new ArrayList();
            
            for (int i : current)
                out.add (al.get(i));
            
            return out;
        }
        
        @Override
        public boolean hasNext()
        {
            return !done;
        }

        @Override
        public List<I> next()
        {
            List<I> out = get(current);
            
            for (int j = 0; j < current.length; j++)
            {
                current[j]++;
                if ( current[j] < al.size() )
                {
                    done = j == current.length -1 && current[j] == al.size();
                    break;
                }
                else
                    current[j] = 0;
            }
            
            return out;
        }
        
        @Override
        public void remove()
        {
            throw new Error( "Not supported" );
        }
    }

    @Override
    public Iterator<List<I>> iterator()
    {
        return new It();
    }
}
