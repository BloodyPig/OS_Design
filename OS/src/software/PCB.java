
package software;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;
//PCB继承线程类
public class PCB {

	// 定义的指令结构体 存放进程的指令情况,设计为PCB的内部类
	public class InstructBlock {
		int InstrucID; // 指令编号
		// int InstrucContent; // 指令内容
		int InstructState; // 指令状态 0 表示系统调用、1 表示用户态计算操作、2 表示 I/O 操作
		int InstructRunTime; // 当前指令需要执行的时长
		int InstructAddress; // 指令逻辑地址，（ProID*100 + random[0,99]）
		int InstructSource;// 指令所需资源，0-3,3为临界资源
	}


	// 进程状态设置为枚举类型
	enum Pro_State {
		CREATED, // 刚被创建的初始状态
		EXECUTE, // 运行状态
		READY, // 就绪状态
		WAITING, // 阻塞状态
	}

	int ProID; // 进程号
	int JobID; // 对应作业号
	int Priority; // 进程优先级，没有很多情况,定义为字符型，
	long InTime; // 进程创建时刻,与设置的计时机制相关
	Pro_State ProState; // 进程三种状态，定义为字符0、1、2、3, 0表示刚创建 1表示执行 2表示就绪 3表示阻塞
	int ProAddress; // 进程的起始物理地址
	int ProMemoryNeed; // 进程需要的内存大小（以内存块为单位）
	int AlreadyRunTime; // 进程已经运行时长，即占用CPU的时间
	int PSW; // 进程状态字寄存器，记录运行到的指令号
	int EndTime; // 进程结束时刻，与创建时间得出程序周转时间
	int AllRunTime; // 程序周转时间
	int InstrucNum; // 进程包含的指令数目
	InstructBlock Instructs[]; // 指令数组

	int request[]; // 进程申请资源的数组
	int mutex=1;
	LinkedList<PCB> mutex_waitQueue; // 信号量有的等待队列
	// 构造函数
	public PCB() {
		this.ProID = 0;
		request = new int[4];
		for(int i=0;i<4;i++) {
			request[i]=0;
		}
		mutex_waitQueue= new LinkedList<PCB>();
	}

	// 创建原语 在这里设计为从文件读取相关参数
	public void CreatePCB(Scanner reader) {
		this.ProAddress = reader.nextInt();
		this.ProMemoryNeed = reader.nextInt();
		this.ProID = reader.nextInt();
		this.Priority = reader.nextInt();
		this.InTime = reader.nextInt();
		this.ProState = Pro_State.CREATED;
		this.PSW = reader.nextInt();
		this.InstrucNum = reader.nextInt();
		this.Instructs = new InstructBlock[this.InstrucNum];
		for (int i = 0; i < this.InstrucNum; i++) {
			this.Instructs[i] = new InstructBlock();
			this.Instructs[i].InstrucID = reader.nextInt();
			this.Instructs[i].InstructAddress = reader.nextInt();
			this.Instructs[i].InstructState = reader.nextInt();
			this.Instructs[i].InstructRunTime = reader.nextInt();
			this.Instructs[i].InstructSource = reader.nextInt();
		}
	}

	// 撤销原语
		public void FinishPCB(FileWriter writer) {
			// 释放占用的两种资源
		OS.memory.available[0] += request[0];
		OS.memory.available[1] += request[1];
		OS.memory.available[2] += request[2];
		OS.memory.available[3] += request[3];
		
		this.AllRunTime = (int) (OS.clock.TimeNow - this.InTime);
		try {
			writer.write(System.getProperty("line.separator"));
			writer.write(
					String.format("%4d %4d %10d %10d %10d", ProID, Priority, InTime, AllRunTime, OS.clock.TimeNow));
		} catch (IOException e) {
			e.printStackTrace();
		} // 写入换行 不受操作系统限制

		}

		// 阻塞原语
		public void WaitPCB() {
			this.ProState = Pro_State.WAITING;
		}

		// 唤醒原语
	public void WakeUpPCB() {
		this.ProState = Pro_State.READY;
	}

	// 就绪原语
		public void MadeReady() {
			this.ProState = Pro_State.READY;
		}

		// 执行原语
		// 0 表示时间片到了要进入就绪队列
		// 1表示作业完成了 要执行结束原语
		// 2 表示遇到了IO指令需要进入阻塞队列
		public int MadeExecute() {
			this.ProState = Pro_State.EXECUTE;// 进程状态置为执行
			OS.cpu.SetUsing(true);// CPU置为占用
			OS.cpu.SetPC(this.ProID);// CPU程序计数器置为当前进程号

			// 调度时候启用一个计时器
			int timeCnt = 0;
			while (timeCnt <= 500) {
				// 如果执行到了最后一个指令 直接退出
				if (this.PSW >= InstrucNum)
					return 1;

				OS.cpu.SetPSW(PSW);// 置指令号
				OS.cpu.SetIR(Instructs[PSW].InstructState); // 置指令状态
				OS.cpu.SetAddress(Instructs[PSW].InstructAddress);// 置指令地址
				OS.memory.FindPage(OS.cpu.GetAddress());// 根据指令地址找到相关页
				// 根据指令状态判断是否阻塞			
			if (Instructs[PSW].InstructState == 3) {//P操作
				mutex--;
				if(mutex<0) {
					mutex_waitQueue.add(this);//移入信号量有关的等待队列
					this.WaitPCB();//进程设为阻塞态	
					System.out.println(this.ProID+"号进程移入");
				}
				PSW++;
			}
			else if(Instructs[PSW].InstructState == 4) {//V操作
				mutex++;
				if(mutex<=0) {
					mutex_waitQueue.removeFirst();//将信号量有关队列中的第一个进程移出
					this.MadeReady();//进程设为就绪态
					System.out.println(mutex_waitQueue.removeFirst().ProID+"号进程移出");
				}
				PSW++;
			}
			else if(Instructs[PSW].InstructState == 2){
				// 如果是IO指令
				OS.cpu.DealIO(Instructs[PSW].InstructRunTime);
				return 2;
			}
			//如果是其他指令，分配资源并进行死锁检测，无死锁则继续运行
			else {					
				request[Instructs[PSW].InstructState]++;//按指令状态进行资源请求
				OS.memory.AllocateResAndDetect(Instructs[PSW].InstructState);//分配这个资源
				OS.cpu.RunSomeTime(Instructs[PSW].InstructRunTime);//执行这个指令
				timeCnt += Instructs[PSW].InstructRunTime;//时间片加
				this.AlreadyRunTime += Instructs[PSW].InstructRunTime;//指令总的运行时间加
				PSW++;		
			}

		}

		return 0;// 返回值有不同类型 交于上层调度程序判断下一步动作
	}

	
}
