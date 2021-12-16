package main.java.priority_queue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * 优先队列
 * 1. 泛型，支持任意元素
 * 2. 可自定义比较器，支持自定义元素的优先顺序
 * 3. 对象数组存储元素，需实现扩容方式
 * 4. 通过堆维护元素顺序
 * 5. 先进先出，非双端队列
 * 6. 迭代器访问，和支持 foreach 遍历
 * 7. fast-fail机制
 */
@SuppressWarnings("unchecked")
public class MyPriorityQueue<E extends Comparable<? super E>> implements Iterable<E> {
    private transient Object[] queue;
    private final Comparator<E> comparator;
    private static final int DEFAULT_CAPACITY = 6;
    private int cap;
    private int size;
    private int modCount; // fast-fail

    /**
     * 初始化
     *
     * @param initialCap 初始化容量
     * @param comparator 比较器
     */
    public MyPriorityQueue(int initialCap, Comparator<E> comparator) {
        cap = initialCap;
        queue = new Object[initialCap];
        this.comparator = comparator;
    }

    public MyPriorityQueue(int initialCap) {
        this(initialCap, Comparator.naturalOrder());
    }

    public MyPriorityQueue(Comparator<E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public MyPriorityQueue() {
        this(DEFAULT_CAPACITY, Comparator.naturalOrder());
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 扩容
     */
    private void grow() {
        int oldCap = queue.length;
        int newCap = oldCap > 64 ? oldCap + 2 : oldCap << 1;
        if (newCap < 0) {
            throw new OutOfMemoryError();
        }
        cap = newCap;
        queue = Arrays.copyOf(this.queue, newCap);
    }

    /**
     * 向上调整
     *
     * @param i 调整的元素下标
     * @param e 调整的元素值
     */
    private void siftUp(int i, E e) {
        while (i > 0) {
            int parent = (i - 1) >> 1;
            Object p = queue[parent];
            if (comparator.compare(e, (E) p) > 0)
                break;
            queue[i] = p;
            i = parent;
        }
        queue[i] = e;
    }

    /**
     * 向下调整
     *
     * @param i 调整的元素下标
     * @param e 调整的元素值
     */
    private void siftDown(int i, E e) {
        int half = size >> 1; // only need to traverse non-leaf node
        while (i < half) {
            queue[i] = e;
            int l = (i << 1) + 1, r = l + 1, g = i;
            if (l < size && comparator.compare((E) queue[l], (E) queue[g]) < 0)
                g = l;
            if (r < size && comparator.compare((E) queue[r], (E) queue[g]) < 0)
                g = r;
            if (g == i)
                break;
            queue[i] = queue[g];
            i = g;
        }
        queue[i] = e;
    }

    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        modCount++;
        int i = size;
        if (i == cap)
            grow();
        siftUp(i, e);
        size++;
        return true;
    }

    public E poll() {
        final Object result;
        if ((result = queue[0]) != null) {
            modCount++;
            Object e = queue[--size];
            queue[size] = null;
            siftDown(0, (E) e);
        }
        return (E) result;
    }

    public E peek() {
        return (E) queue[size - 1];
    }

    private final class Itr implements Iterator<E> {
        private int cur;
        private final int expectedModCount;

        Itr(int size, int modCount) {
            cur = size;
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return cur > 0;
        }

        @Override
        public E next() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            return (E) queue[--cur];
        }
    }

    /**
     * @return 迭代器
     */
    public Iterator<E> iterator() {
        return new Itr(size, modCount);
    }
}
