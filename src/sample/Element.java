package sample;

import java.util.Comparator;

public class Element implements Comparator<Element> {
    public int n, distance;
    public Element prev;

    public Element() {
        this.n = 0;
        this.distance = 0;
        this.prev = null;
    }

    public Element(int n, int distance, Element prev) {
        this.n = n;
        this.distance = distance;
        this.prev = prev;
    }

    @Override
    public int compare(Element o1, Element o2) {
        if (o1 == o2)
            return 0;
        if (o1.distance > o2.distance)
            return 1;
        return -1;
    }

    /*@Override
    public int compareTo(Element other) {
        if (this == other)
            return 0;
        if (this.distance > other.distance)
            return 1;
        return -1;
    }*/
}
