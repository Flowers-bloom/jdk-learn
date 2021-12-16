package main.java.stream;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamExample {
    public static void main(String[] args) {
        // stream 的创建操作主要分三类：对象序列，集合，supplier对象
        create();

        // stream 的主要使用操作主要分两类：转换（map，filter），聚合（reduce，collect）
        mainUse();

        // stream 的其他使用操作包括：排序，去重，截取，flatMap，并行，一些常见的聚合操作（比如整数的sum，avg等）
        otherUse();
    }

    public static void otherUse() {
        //1.排序
        System.out.println("sorted: " + Arrays.toString(Stream.of(3, 2, 5, 4, 1).sorted().toArray(Integer[]::new)));

        //2.去重
        System.out.println("distinct: " + Arrays.toString(Stream.of(1, 1, 1, 2, 2, 3, 4).distinct().toArray(Integer[]::new)));

        //3.截取
        System.out.println("skip: " + Arrays.toString(Stream.of(1, 2, 3, 4, 5).skip(2).limit(2).toArray(Integer[]::new)));

        //4.合并
        System.out.println("merge: " + Arrays.toString(Stream.concat(Stream.of(1, 3), Stream.of(2, 4)).toArray(Integer[]::new)));

        //4.flatMap
        System.out.println("flatMap: " + Arrays.toString(
                Stream.of(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6))
                        .flatMap(list -> list.stream())
                        .toArray(Integer[]::new)
        ));

        //5.并行
        System.out.println("parallel sort:" + Arrays.toString(Stream.of(3, 2, 5, 4, 1).parallel().sorted().toArray(Integer[]::new)));

        //6.常见聚合操作
        System.out.println("sum: " + Stream.of(1, 2, 3).mapToInt(Integer::intValue).sum());

        //7.测试所有元素是否满足条件
        System.out.println("all num is positive: " + Stream.of(1, 2, 3, -4).allMatch(n -> n > 0));
        System.out.println("any num is positive: " + Stream.of(1, 2, 3, -4).anyMatch(n -> n > 0));
    }

    public static void mainUse() {
        //1. map: Mapping every element to a new element by specified rule.
        Stream<Integer> newIntStream = Stream.of(1, 2, 3).map(n -> n * n);
        System.out.println("n*n map: " + Arrays.toString(newIntStream.toArray(Integer[]::new)));

        //2. filter: Filtering all element by specified rule.
        Stream<Character> lowerCharStream = Stream.of('a', 'B', 'c', 'd', 'E').filter(c -> c >= 'a' && c <= 'z');
        Stream<Character> upperCharStream = Stream.of('a', 'B', 'c', 'd', 'E').filter(c -> c >= 'A' && c <= 'Z');
        System.out.println("lowerCharStream: " + Arrays.toString(lowerCharStream.toArray(Character[]::new)));
        System.out.println("upperCharStream: " + Arrays.toString(upperCharStream.toArray(Character[]::new)));

        //3. reduce: Reducing a collection to one Object.
        Integer sum = Stream.of(1, 2, 3).reduce(0, (acc, e) -> acc + e);
        Stream<Integer> emptyStream = Stream.empty();
        Optional<Integer> optional = emptyStream.reduce((acc, e) -> acc + e);
        System.out.println("sum: " + sum);
        System.out.println("optional: " + (optional.isPresent() ? optional.get() : "empty"));

        // 4. map-reduce: Mapping every string config line to Map<String, String>, and reduce every Map to one Map.
        List<String> props = Arrays.asList("profile=daily", "log.level=info", "debug=true");
        HashMap<String, String> configMap = (HashMap<String, String>) props.stream()
                .map(prop -> {
                    String[] strings = prop.split("=");
                    return strings.length == 2 ? Collections.singletonMap(strings[0], strings[1]) : null;
                })
                .reduce(new HashMap<>(10), (m, kv) -> {
                    m.putAll(kv);
                    return m;
                });
        System.out.println("Map-reduce config result: ");
        configMap.forEach(
                (k, v) -> System.out.println(k + "=" + v)
        );

        //5. collect: Collecting all element to a collection, and it can divide elements to multiple group.
        List<String> stringList = Stream.of("Apple", "Alice", "Banana", "Cherry", "Black", "Coco", "", "  ")
                .filter(s -> s.trim().length() > 0)
                .collect(Collectors.toList());
        Map<Character, List<String>> map = Stream.of("Apple", "Alice", "Banana", "Cherry", "Black", "Coco", "", "  ")
                .filter(s -> s.trim().length() > 0)
                .collect(
                        Collectors.groupingBy(s -> s.charAt(0), Collectors.toList())
                );
        System.out.println("stringList: " + Arrays.toString(stringList.toArray()));
        map.forEach(
                (k, v) -> System.out.println(k + ":" + v)
        );
    }

    public static void create() {
        //1. 根据若干个对象创建
        Stream<Integer> intStream = Stream.of(1, 2);
        assert intStream.count() == 2;

        //2. 借助集合
        List<Integer> list = Arrays.asList(1, 2, 3);
        Stream<Integer> listStream = list.stream();
        assert listStream.count() == 3;

        //3. 借助 supplier 对象
        Stream<Integer> supplierStream = Stream.generate(new NatureNumSupplier()).limit(5);
        assert supplierStream.count() == 5;
    }

    public static final class NatureNumSupplier implements Supplier<Integer> {
        private int num = 0;

        @Override
        public Integer get() {
            return ++num;
        }
    }
}
