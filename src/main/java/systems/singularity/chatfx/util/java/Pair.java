package systems.singularity.chatfx.util.java;

/**
 * Created by pedro on 6/13/17.
 */
public class Pair<T1, T2> implements Comparable<Pair<T1, T2>> {
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Pair))
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;
        return (first != null ? first.equals(pair.first) : pair.first == null) && (second != null ? second.equals(pair.second) : pair.second == null);

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Pair<T1, T2> o) {
        Integer result = null;

        if (this.first instanceof Comparable)
            //noinspection unchecked
            result = ((Comparable<T1>) this.first).compareTo(o.first);

        if (result != null && result != 0)
            return result;

        if (this.second instanceof Comparable)
            //noinspection unchecked
            result = ((Comparable<T2>) this.second).compareTo(o.second);

        if (result != null)
            return result;

        throw new IllegalStateException("T1 and T2 are not comparable");
    }
}
