package main.java.external_sort;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
//        makeTestData(10, 1000);
        String dir = "D:\\Code\\Java\\practice-learn\\src\\main\\java\\external_sort\\data";
        heapMethod(dir);
        LinkedList<Integer> list = new LinkedList<>();
        list.addAll(Arrays.asList(1,2,3));

    }

    public static void heapMethod(String dir) {
        HeapSort heapSort = new HeapSort();
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {

            }

            while (true) {
                boolean hasNum = false;
                for (File f : files) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                        String str = reader.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void makeTestData(int fileNum, int numberNum) {
        Random random = new Random();
        String pathPattern = "D:\\Code\\Java\\practice-learn\\src\\main\\java\\external_sort\\data\\slice%d.txt";
        for (int i=1; i<=fileNum; i++) {
            File file = new File(String.format(pathPattern, i));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                for (int j=0; j<numberNum; j++) {
                    int n = random.nextInt(10000);
                    String str = n + "\n";
                    out.write(str.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
