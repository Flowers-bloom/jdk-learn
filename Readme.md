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

（2）堆 优先队列底层结构为对象数组，通过堆结构来实现指定优先级的访问，以数组开头为堆顶。 注意如果以数组末尾为堆顶，向下调整时数组开头会空出位置。

（3）迭代器访问和 foreach 遍历 通过创建实现`Iterator`接口的内部类可实现迭代器访问。如果要支持 foreach 遍历方式，需要让优先队列实现`Iterable`类，即实现一个返回`iterator`对象的方法。

### 2、时间轮实现

**功能点**

- 通过指针循环后移实现延时任务的调度执行，时间复杂度`O(1)`
- 单级时间轮的最大延时时间比较小，通过多级时间轮（比如一分钟、一小时、一天）增大最大延时时间

**收获**  
（1）为什么`Java`调用`wait`,`notify/notifyAll`必须先获得锁？ 根本原因在于`Java`规范中是这么定义的：

> Every object, in addition to having an associated monitor, has an associated wait set. A wait set is a set of threads.
>
> When an object is first created, its wait set is empty. Elementary actions that add threads to and remove threads from wait sets are atomic. Wait sets are manipulated solely through the methods Object.wait, Object.notify, and Object.notifyAll. Ref: https://docs.oracle.com/javase/
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
            System.out.println((char) buffer.get());
        }

        // or other method to get data
        // channel.write(ByteBuffer.wrap(str.getBytes()));
    }
}
```

### 4、Async

异步计算，目前仅支持异步执行和回调。

### 5、NettyExample

实现 Netty Pipeline 处理器结构和简单回调逻辑。

经验： Selector.select() 操作必须和 socket 操作在同一个线程，否则监听的事件一直为空

![UML类图](https://z3.ax1x.com/2021/11/05/IumEQI.png)

### 6、Stream

学习 Java8 Stream 特性：创建Stream；Stream使用，包括转换和聚合。

有两个注意点：

1.stream转换操作执行时只是声明一个转换规则，并不会对元素实时计算。只有到聚合操作时，才会回溯前面的规则对元素进行计算。

2.stream实例聚合之后就会被close，所以每一个stream实例只能被聚合一次，如果要继续使用相同元素的stream，就需要重新创建一个stream。

### 7、HPStorage

高性能存储，思路参考 RocketMQ CommitLog 消息存储方式。

```java
// 写入文件后空值NUL问题
private void commit0() {
        int lastCommitPos = committedPos.get(), writePos = wrotePos.get(), len = writePos - lastCommitPos;
        if (len > 0) {
            ByteBuffer buffer = writeBuffer.slice();
            buffer.position(lastCommitPos);
            buffer.limit(writePos);
            try {
                fileChannel.write(buffer);
                committedPos.getAndAdd(len);
                System.out.println(filename + " commit success");
            } catch (IOException e) {
                e.printStackTrace();
                }
            }
        }
        
/**
 * 原因：
 * 应该在 slice 操作之前设置 writeBuffer 的 position 和 limit，否则slice出来的buffer不为0，
 * 其值等于原buffer已有数据的长度，然后每次读取数据都是 pos + offset，从而 slice buffer 读取
 * 出来的数据都是空值NUL。
 * 
 * slice 操作就是对原 buffer 在 pos ~ limit 范围做一个切片，与原 buffer 共享数据，
 * 在 slice buffer 上修改数据也会造成原 buffer 改变。
 * 不同之处在于，子切片有自己的 pos，limit，mark，offset 等变量值。
 */
```

### 8、责任树设计模式

责任树模型图：
[![ppf5MKf.png](https://s1.ax1x.com/2023/04/02/ppf5MKf.png)](https://imgse.com/i/ppf5MKf)

Router：  
路由器，树中的非叶子节点，根据请求入参，将请求路由到正确的处理节点；

Handler：   
处理器，树中的叶子节点，请求的实际处理者；

设计思想：  
责任树设计模式融合了责任链和策略模式的优点，既可纵向实现多维度的责任链处理逻辑，也可以横向配置同一纬度下不同的处理策略，支持实现多维度多策略的复杂业务场景，可拓展性高，相应地也就导致代码理解起来成本更高，也容易被滥用，对于简单的业务场景不推荐使用。并且，后续应该支持节点拓扑可视化，如此当算是一个具备可用性的框架。

使用方法：
对于非叶子节点必须继承 Router 定义下游节点，默认所有节点都必须实现 Handler 接口，抽象 Router 类默认已实现 Handler 接口，且 exec 方法不允许重写。其中，通过类继承实现不同的处理器有不同的入参和结果类型，合理抽象和隔离不同的业务场景属性，提升代码可读性和保证高内聚。