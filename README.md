Flink

# 快速入门
1. wordcount
2. streamwordcount

# 集群搭建
## standard模式
  1. 官网下载软件包
  2. bin/start-cluster.sh 启动
        1. TaskManagerRunner
        2. Jps
        3. StandaloneSessionClusterEntrypoint
  3. 页面： http://localhost:8081/#/overview
  4. 在页面提交jar, 设置参数，执行任务，观察流程图
  5. bin/stop-cluster.sh 关闭
 
 
 # 运行架构
 ## 运行组件
 1. JobManger
    1. 控制程序运行的主进程，每个程序都有一个不同的jobManger
    2. 会先接受到要执行的程序：作业图（jobgraph）、逻辑数据流图（logical dataflow graph），打包了的所有类库以及资源jar包
    3. 将jobgraph转换为一个物理层面的执行图（executiongraph） 包含了所有可以并发执行的任务
    4. 向ResourceManger申请资源（TaskManger的slot）， 将执行图分发给TaskManger， 并负责中央协调
 2. TaskManger
    1. Flink中会有多个TaskManger，每个TakManger会有一定数量的slots， slots 限制了任务的并行度
    2. 启动后向ResouceManger注册资源slots，然后将slots提供给JobManger调用执行task任务
    3. TaskManger 可以同用一个应用中的其他TaskManger 交换数据
 3. ResourceManger
    1. 负责管理TaskManger中的slots，TaskManger定义了flink中的基本资源单元
    2. flink可以根据不同的环境设置不同的资源管理器：YARN Mesos等
    3. JobManger申请资源时 需要的资源会将空闲的TaskManger分配给JobManger，不满足时还会向资源管理平台发起会话以提供启动TaskManger进程的容器
 4. Dispacher
    1. 可以跨作业进行，提供Rest接口
    2. 当一个应用被执行时，分发器将应用提交给JobManger
    3. 启动一个webui 方便监控和展示作业流程（yarn中没有Dispacher，故没有webui）
 ## 任务提交流程
 ### standalone
 ![standalone模式](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/%E4%BB%BB%E5%8A%A1%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%9B%BE-standalone.PNG?raw=true)
 ### yarn
 ![yarn模式](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/%E4%BB%BB%E5%8A%A1%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B%E5%9B%BE-yarn.PNG?raw=true)
   
 ## 任务调度原理
 ![任务调度](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/%E4%BB%BB%E5%8A%A1%E8%B0%83%E5%BA%A6.PNG?raw=true)

 ### TaskManger和slot
 ![TaskManger](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/TaskMangerAndSlots.PNG?raw=true)  ![TaskManger](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/TaskMangerAndSlots2.PNG?raw=true)
  1. 每一个TEaskManger都是一个jvm进程
  2. 一个TaskManger有一个或多个slot
  3. 默认情况下Flink 允许子任务共享slot，即使是不同任务的子任务
  4. Task slot 是静态概念，是指TaskManger具有的并发能力
  example:
      ![TaskManger_example](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/TaskMangerAndSlotsExample1.PNG?raw=true)  ![TaskManger](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/TaskMangerAndSlotsExample2.PNG?raw=true)
      1. 共有3个TaskManger 每个TaskManger3个slot, 共有9个slots
      2. 第一种情况,一个并行度，导致很多slots空闲
      3. 第二种情况，2个并行度，会分配到两个slots上面
      4. 第三种情况，9个并行度，会平均分到各个slot上面，但是由于sink也是9个输出文件式，可能乱序等问题
      5. 第四种情况，完美
 ### 程序和数据流
   1. 所有flink程序都由三部分：Souce trasformation sink
   2. source 负责读取数据源 trasformation 负责各种算子的处理 sink负责输出
   3. 运行时，flink程序会映射为“逻辑数据流”（dataflow），包含三部分
   4. 每个dataflow 以一个或多个source开始，以一个或多个sink结束，dataflow 类似于任意的有向无环图DAG
   5. 在大部分情况下程序的转换运算（trasformation）和dataflow的operator一一对应
 
 ### 执行图 
   1. streamGraph -》 JobGraph -》 ExecutionGraph -》 物理执行图
   2. streamGraph： 用户通过StreamApi编写的代码生成的最初的图，用来代表程序的拓扑结构
   3. JobGraph：streamGraph经过优化后生成的JobGraph，提交给jobManger的数据结构，将多个符合条件的节点chain合并成一个节点
   4. ExecutionGraph：JobManger根据JobGraph生成的ExecutionGraph,ExecutionGraph是JobGraph的并行化版本，是调度层最核心的数据结构
   5. 物理执行图：JobManger根据ExecutionGraph对job进行调度后， 在各个TaskManger上部署Task后形成的图，并不是一个具体的数据结构
   ![graph](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/StreamGraph-JobGraph.PNG?raw=true) 
   ![graph](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/ExecutionGraph.PNG?raw=true)
   ![graph](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/%E7%89%A9%E7%90%86%E6%89%A7%E8%A1%8C%E5%9B%BE.PNG?raw=true)
   
 ### 并行度
   1. 特定算子的子任务（subTask）的个数称之为并行度
   2. 一个stream job 的并行度是指其所有算子中最大的并行度
   3. 在一个应用程序中，不同的算子可以有不同的并行度
   4. 算子之间传输数据的模式
        1. one-to-one:  stream维护者分区以及元素的顺序（比如source和map之间），
        这以为着map算子子任务看到到元素的个数以及顺序和source算子的子任务看的的元素个数以及顺序是相同的
        map filter flatmap 等算子都是one-to-one的
        2. Redistributing： stream的分区会发生改变，每一个算子的子任务依据所选择的trasformation发送数据到不同的目标任务。
        例如： keyby 根据hashcode重分区，而broadcast和rebalance会随机重新分区，这些算子都会引起Redistributie过程
        而redistribute类似于spark的shuffle过程
   ![graph](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/parallelism.PNG?raw=true) 
    
 ### 任务链
   1. flink采用一种任务链的优化技术，可以在特定条件下减少本地通信开销，为了满足任务链的要求，必须将两个算子或多个算子，设置为相同的并行度
   并通过本地转发（local forward）的方式链接
   2. 相同并行度的one-to-one操作，flink将这样相连的算子链接在一个task，原来的算子成为其中的subtask
   3. 并行度相同 one-to-one操作，二者缺一不可
   ![graph](https://github.com/Aliang-Swordsman/flinkTutorial/blob/master/src/main/resources/picture/OperatorChains.PNG?raw=true) 
   4. 但是针对比较大的task ，如果还要合并在一起会更加影响性能，故可以不选择合并
        1. env.disableOperatorChaining() 
        2. 每个算子后面可以跟加 .disableChaining()
        3. 强行断开，形成一个新的链 
   代码：
   ```scala
     object StreamWordCount {
       def main(args: Array[String]): Unit = {
         val env = StreamExecutionEnvironment.getExecutionEnvironment
         // env.disableOperatorChaining() // 针对比较的的task ，可以选择不进行操作链合并
   
         val paramTool: ParameterTool = ParameterTool.fromArgs(args)
     
     //    val host = "localhost"
     //    val port = 7777
         val host = paramTool.get("host")
         val port = paramTool.getInt("port")
     
         // 接受一个socket文本流
         val streamData = env.socketTextStream(host, port)
     
         // 数据流处理
         val wordCount = streamData.flatMap(_.split(" "))
           .filter(_.nonEmpty)
     //      .disableChaining() 针对算子不使用chain合并操作
     //      .startNewChain() 强行断开形成一个新链
           .map((_, 1))
           .keyBy(0)  // 流式没有group by 只有keyby 代替 nc -lk 7777
           .sum(1)
     
         wordCount.print()
         .setParallelism(1) //设置并行度 及 slot 的个数 默认线是核数 cpu的core 数
         // 任务执行
         env.execute("stream wordcount job")
       }
     
     }
   ```
  1. 在并行度为1 的机器上，当设置任务提交参数为1时，则只有两个 chains
        1. source、flatMap、filter、map -》 agg、sink
  2. 在并行度为1 的机器上，当设置任务提交参数为2时，则只有3个 chains
        1. source -》 flatMap、filter、map -》 agg =》 sink
  3. 当设置env.disableOperatorChaining() 
        1. source -》 flatMap -》 filter -》 map -》 agg -》 sink
  4. 当设置filter算子后设置.disableChaining()
          1. source、flatMap -》 filter -》 map -》 agg、sink
  5. 当设置filter算子后设置.startNewChain()
          1. source、flatMap -》 filter、 map -》 agg、sink
      
       

# API
## 基础api
map flat filter 
keyby .keyBy("id") .keyBy(0) 返回的是JAVATUPLE .keyBy(_._1) 返回的是对应的类型
agg 绝大多多数聚合操作是针对keyStream

## 分流算子
split 将一个stream 分成两部分 select： 根据名称选择类型
connect 优点： 可以对不同的流做不同的操作  缺点： 每次只能操作两个流
union 优点： 可以对多个流进行合并  缺点: 多条流类型必须相同 

 