分布式存储系统案例背景介绍

hadoop的架构原理,分布式存储hdfs edits log机制,主要是作为操作日志记录到磁盘里去,
如果NameNode突然宕机,内存中的元数据可以通过edits log来进行恢复,edits log的原理和机制
通过分段加锁、内存双缓冲复杂的机制,手写实现这套机制

如果说要做edits log场景,执行一个命令:hadoop fs -mkdir /usr/warehosue创建一个目录,
执行两件事:在内存里的文件目录树中加入对应的目录节点;在磁盘里写入一条edits log,记录本次元数据的修改.

hdfs client去创建目录的话,会给hdfs NameNode发送一个rpc接口调用的请求,调用人家的mkdir()接口,
在那个接口里就会完成上述的两件事情

接下来其实主要是做两件事情:第一件是在内存文件目录树中加入进去对应的一个目录节点,第二件事情是在edits log写入磁盘文件

- FSNamesystem其实是作为NameNode里元数据操作的核心入口,负责管理所有的元数据的操作,在里面可能会调用其他的组件完成相关的事情
- FSDirectory专门负责管理内存中的文件目录树
- FSEditLog专门负责管理写入edits log到磁盘文件里去
