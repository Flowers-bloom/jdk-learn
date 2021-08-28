package main.java.priorityqueue;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 优先队列
 * 1. 泛型，支持任意元素
 * 2. 可自定义比较器，支持自定义元素的优先顺序
 * 3. 对象数组存储元素，需实现扩容方式
 * 4. 通过堆维护元素顺序
 * 5. 先进先出，非双端队列
 *
 * <? extends E> 表示集合中的元素必须是 E 的子类
 * <? super E> 表示集合中的元素必须是 E 的父类
 * <E extends Comparable<? super E>> 表示 E 或者 E 的父类其中之一，必须实现 comparable 接口
 */
@SuppressWarnings("unchecked")
public class MyPriorityQueue<E extends Comparable<? super E>> {
    private transient Object[] queue;
    private final Comparator<E> comparator;
    private static final int DEFAULT_CAPACITY = 6;
    private int cap;
    private int size;

    /**
     * 初始化
     *
     * @param initialCap
     * @param comparator
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
     * @param i
     * @param e
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
     * @param i
     * @param e
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
        if (size == cap)
            grow();
        siftUp(size, e);
        size++;
        return true;
    }

    public E poll() {
        Object result = queue[0];
        Object e = queue[--size];
        queue[size] = null;
        siftDown(0, (E) e);
        return (E) result;
    }
}
