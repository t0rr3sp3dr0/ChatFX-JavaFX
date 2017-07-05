package systems.singularity.chatfx.util.java;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

/**
 * Created by pedro on 6/20/17.
 */
public class ResizableBlockingQueue<E> {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = Integer.MAX_VALUE;

    private final Queue<E> queue = new LinkedBlockingQueue<>();
    private int capacity;

    public ResizableBlockingQueue(int capacity) {
        super();
        this.capacity = capacity;
    }

    public void resize(double coefficient, double constant) {
        synchronized (this.queue) {
            this.capacity = (int) Math.max(Math.min((this.capacity * coefficient) + constant, ResizableBlockingQueue.MAX_SIZE), ResizableBlockingQueue.MIN_SIZE);
        }
    }

    public boolean contains(Object o) {
        synchronized (this.queue) {
            //noinspection SuspiciousMethodCalls
            return this.queue.contains(o);
        }
    }

    public E front() {
        synchronized (this.queue) {
            return this.queue.peek();
        }
    }

    public boolean add(E e) {
        while (true) {
            synchronized (this.queue) {
                if (this.capacity > 0) {
                    this.capacity--;
                    return this.queue.add(e);
                }
            }
        }
    }

    public E remove() {
        while (true) {
            try {
                synchronized (this.queue) {
                    E e = this.queue.remove();
                    this.capacity++;
                    return e;
                }
            } catch (NoSuchElementException ignored) {
            }
        }
    }

    public boolean removeIf(final Predicate<? super E> filter) {
        synchronized (this.queue) {
            return queue.removeIf(e -> {
                if (filter.test(e)) {
                    this.capacity++;
                    return true;
                }

                return false;
            });
        }
    }

    public int size() {
        return this.queue.size();
    }

    public int getCapacity() {
        return capacity;
    }
}
