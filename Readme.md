# practice-learn
该仓库用于记录一些个人的实践学习，比如复刻一个优先队列。

## 实践记录
### 1、复刻优先队列

**功能点**
- 泛型，支持任意元素
- 可自定义比较器，支持自定义元素的优先顺序
- 对象数组存储元素，需实现扩容方式
- 通过堆维护顺序
- 先进先出，非双端队列
- 迭代器访问，和支持 foreach 遍历
- fast-fail机制

**收获**  
（1）泛型
```java
/**
 * <? extends E> 表示集合中的元素必须是 E 的子类
 * <? super E> 表示集合中的元素必须是 E 的父类
 * <E extends Comparable<? super E>> 表示 E 或者 E 的父类其中之一，必须实现 comparable 接口
 */
```

（2）堆
优先队列底层结构为对象数组，通过堆结构来实现指定优先级的访问，以数组开头为堆顶。 注意如果以数组末尾为堆顶，向下调整时数组开头会空出位置。

（3）迭代器访问和 foreach 遍历
通过创建实现`Iterator`接口的内部类可实现迭代器访问。如果要支持 foreach 遍历方式，需要让优先队列实现`Iterable`类，即实现一个返回`iterator`对象的方法。

### 2、时间轮实现

**功能点**  
- 通过指针循环后移实现延时任务的调度执行，时间复杂度`O(1)`
- 单级时间轮的最大延时时间比较小，通过多级时间轮（比如一分钟、一小时、一天）增大最大延时时间

**收获**  
（1）为什么`Java`调用`wait`,`notify/notifyAll`必须先获得锁？
根本原因在于`Java`规范中是这么定义的：

> Every object, in addition to having an associated monitor, has an associated wait set. A wait set is a set of threads.
> 
> When an object is first created, its wait set is empty. Elementary actions that add threads to and remove threads from wait sets are atomic. Wait sets are manipulated solely through the methods Object.wait, Object.notify, and Object.notifyAll.
Ref: https://docs.oracle.com/javase/
> 

为了保证访问`wait set`操作是原子的，所以在调用`wait/notify/notifyAll`操作之前必须先获得锁。

（2）多级时间轮下，始末时刻需要加载未来一小时或者一天的数据会导致延迟以及内存可能不够的问题   
可以考虑在时刻到来之前对数据进行**预加载**，并且只**加载未来的一部分数据**，从而避免加载延迟和内存不够的问题。

### 3、Reactor

**功能点**
- 顺带实现了 `Bio Server` 和 `Nio Server`
- 实现了单 `Reactor` 多线程模型和多 `Reactor` 多线程模型
- 简单池化 `ByteBuffer` 对象

**收获**
（1）`Java`自带的`ByteBuffer`写入数据之后，读取数据前注意先`flip to reset read postion`，否则容易发生`BufferOverflowException`
```java
public class Test {
    public static void main(String[] args) throws IOException {
        FileChannel fc = new FileInputStream("Readme.md").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        fc.read(buffer);
        buffer.flip(); // reset position, otherwise cannot read anyone
        while (buffer.hasRemaining()) {
            System.out.println((char)buffer.get());
        }
        
        // or other method to get data
        // channel.write(ByteBuffer.wrap(str.getBytes()));
    }
}
```

### 4、Async

异步计算，目前仅支持异步执行和回调。

