# OS_Design

## 实现功能

- #### 裸机硬件的仿真

包含Memory、Disk、CPU、MMU、Clock 等硬件的设计、抽象与展示。

- #### 作业控制块JCB、进程控制块PCB、页表、快表等数据结构的设计与展示

- #### 批处理作业的创建、调度、管理以及可视化

仿真实现了批处理作业的创建并且映射到磁盘，作业的调度，调度过程的可视化，调度结果向文件的输出显示。

- ####  进程的状态转换

基于进程的各种原语，实现了进程在运行过程中的状态转换，包括执行态、就绪态、阻塞态、终止态和挂起态。

- #### 进程并发执行的仿真

使用了Java多线程机制，设计多计时器完成了进程的并发执行。

- #### 指令执行过程中地址映射仿真实现

进程的调度细化到指令级别，为每个指令设计了指令地址，实现了地址映射。

- ####  指令执行过程中的缺页异常处理过程仿真实现

仿真实现了页式内存管理，对缺页异常做出处理

- #### 仿真实现内存的分页存储空间的管理

实现了作业调度前的内存分配和作业调度结束后的内存回收

- #### 进程的同步互斥

- #### 死锁的检测算法

- #### 可视化图形界面

