package main.java.priority_queue;

import java.util.*;

public class Test {
    private final Comparator<Integer> comparator = ((o1, o2) -> o2 - o1);
    private static final Integer[] arr = new Integer[10];
    private static final Random random = new Random();
    private static int size;

    public static void main(String[] args) {
        Test t = new Test();
        MyPriorityQueue<Integer> q = new MyPriorityQueue<>((o1, o2) -> o2 - o1);
        for (int i=0; i<15; i++) {
            int num = random.nextInt(40);
            System.out.print(num + " ");
            q.offer(num);
        }
        System.out.println();
    }

    public boolean isGreaterChild(int i) {
        if (i >= size) {
            return true;
        }
        int l = i*2+1, r = l+1;
        if (l < size && comparator.compare(arr[i], arr[l]) > 0) {
            return false;
        }
        if (r < size && comparator.compare(arr[i], arr[r]) > 0) {
            return false;
        }
        return isGreaterChild(l) && isGreaterChild(r);
    }

    public void offer(int num) {
        siftUp(size, num);
        size++;
    }

    public Integer poll() {
        Integer result = arr[0];
        Integer e = arr[--size];
        arr[size] = null;
        siftDown(0, e);
        return result;
    }

    // 大顶堆，以数组开头为堆顶。注意如果以数组末尾为堆顶，向下调整时数组开头会空出位置
    // 向上调整
    public void siftUp(int i, Integer num) {
        while (i > 0) {
            int parent = (i - 1) >> 1;
            if (comparator.compare(num, arr[parent]) > 0)
                break;
            arr[i] = arr[parent];
            i = parent;
        }
        arr[i] = num;
    }

    // 向下调整
    // 0 (1, 2)
    // 1 (3, 4) / 2 (5, 6)
    public void siftDown(int i, Integer num) {
        int half = size >> 1;
        while (i < half) {
            arr[i] = num;
            int left = (i << 1) + 1, right = left + 1, g = i;
            if (left < size && comparator.compare(arr[left], arr[g]) < 0) {
                g = left;
            }
            if (right < size && comparator.compare(arr[right], arr[g]) < 0) {
                g = right;
            }
            if (g == i)
                break;
            arr[i] = arr[g];
            i = g;
        }
        arr[i] = num;
    }
}
