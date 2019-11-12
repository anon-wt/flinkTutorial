Flink

#快速入门
1. wordcount
2. streamwordcount

#集群搭建
##standard模式
  1. 官网下载软件包
  2. bin/start-cluster.sh 启动
        1. TaskManagerRunner
        2. Jps
        3. StandaloneSessionClusterEntrypoint
  3. 页面： http://localhost:8081/#/overview
  4. 在页面提交jar, 设置参数，执行任务，观察流程图
  5. bin/stop-cluster.sh 关闭
 
 
 #运行架构
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
 ![standalone模式](.\src\main\resources\picture\任务执行流程图-standalone.PNG)
 ### yarn
 ![yarn模式](.\src\main\resources\picture\任务执行流程图-yarn.PNG)
   
 ## 任务调度原理
 ![任务调度](.\src\main\resources\picture\任务调度.PNG)

# flinkTutorial
