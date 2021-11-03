package main.java.external_sort;

import java.io.*;

public class HeapSort {
    public static void main(String[] args) {
        int[] arr = new int[] {
                1,4,3,6,5,2,7
        };
        HeapSort heapSort = new HeapSort();
        for (int i = 0; i < arr.length; i++) {
            heapSort.offer(arr[i]);
        }
        while (!heapSort.isEmpty()) {
            System.out.print(heapSort.poll() + " ");
        }
    }

    private static final int INIT_CAP = 50;
    private int[] queue;
    private int size;

    public HeapSort() {
        queue = new int[INIT_CAP];
    }

    public void sort(String dir) {
        File readFile = new File(dir);
        if (readFile.isDirectory()) {
            File[] files = readFile.listFiles();
            for (File f : files) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                    String str = reader.readLine();
                    if (!"".equals(str)) {
                        offer(Integer.parseInt(str));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.print(poll() + " ");

            boolean hasNum = true;
            while (hasNum) {
                hasNum = false;
                for (File f : files) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                        String str = reader.readLine();
                        if (!"".equals(str)) {
                            hasNum = true;
                            offer(Integer.parseInt(str));
                            System.out.print(poll() + " ");
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void siftDown(int i) {
        int left = i*2+1, right = left+1, min = i;
        if (left < size && queue[left] < queue[min]) {
            min = left;
        }
        if (right < size && queue[right] < queue[min]) {
            min = right;
        }
        if (min != i) {
            int tmp = queue[min];
            queue[min] = queue[i];
            queue[i] = tmp;
            siftDown(min);
        }
    }

    /**
     *
     * @param i 最后一个元素坐标
     */
    public void siftUp(int i) {
        if (i == 0)
            return;
        int parent = (size-1)>>1, val = queue[i];
        while (val < queue[parent]) {
            queue[i] = queue[parent];
            i = parent;
            parent = (parent-1)>>1;
        }
        queue[i] = val;
    }

    public void offer(int val) {
        if (size+1 > INIT_CAP)
            throw new RuntimeException("size over cap");
        queue[size] = val;
        siftUp(size);
        size++;
    }

    public int poll() {
        if (size == 0)
            throw new IllegalStateException("size is zero");
        int result = queue[0];
        queue[0] = queue[size-1];
        siftDown(0);
        size--;
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
