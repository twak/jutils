package org.twak.utils.collections;

import java.util.ArrayList;
import java.util.List;

public class PairList <A,B> extends ArrayList<A> {

    List<B> bs = new ArrayList<>();

    public void add(A a, B b) {
        add(a);
        bs.add(b);
    }

    public A getLastA() {
        return get(size()-1);
    }

    public B getLastB() {
        return bs.get(bs.size() -1);
    }

    public void removeLast() {
        remove(size()-1);
        bs.remove(bs.size()-1);
    }

}
