package io.fusion.framework.core.tools.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author enhao
 */
public abstract class PersistenceBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    private final BlockingQueue<E> delegateQueue;

    protected PersistenceBlockingQueue(BlockingQueue<E> delegateQueue) {
        this.delegateQueue = delegateQueue;
    }

    @Override
    public Iterator<E> iterator() {
        return delegateQueue.iterator(); // todo
    }

    @Override
    public int size() {
        return countFromStorage() + delegateQueue.size();
    }

    @Override
    public void put(E e) throws InterruptedException {
        delegateQueue.put(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return delegateQueue.offer(e, timeout, unit);
    }

    @Override
    public E take() throws InterruptedException {
        E element = pollFromStorage();
        if (null != element) {
            return element;
        }
        return delegateQueue.take();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E element = pollFromStorage();
        if (null != element) {
            return element;
        }
        return delegateQueue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return countFromStorage() + delegateQueue.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(E e) {
        return delegateQueue.offer(e);
    }

    @Override
    public E poll() {
        E element = pollFromStorage();
        if (null != element) {
            return element;
        }
        return delegateQueue.poll();
    }

    @Override
    public E peek() {
        E element = peekFromStorage();
        if (null != element) {
            return element;
        }
        return delegateQueue.peek();
    }

    protected abstract E pollFromStorage();

    protected abstract int countFromStorage();

    protected abstract E peekFromStorage();
}
