
package software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import hardware.Clock;
import hardware.Disk;
import software.PCB.Pro_State;

//作业可以是进程执行前的状态，可以包含多个进程
//根据这一特点将设计不同类型作业，分别包含不同数量的进程
//作业调度使用先来先服务调度算法,并根据需要内存大小判断是否足够其开始执行
public class JCB {
	public int JobID; // 作业号
	public int ProcessAmount; // 进程数量
	public PCB Process[];// 作业包含的进程
	public long InTime; // 作业提交时间
	public int MemorySize;// 所需内存大小 以页为单位
	public boolean InMemory; // 标志该作业是否在内存
	public int MemoryOwn[]; // 作业申请到的内存用数组记录
	public boolean ProIsDone[]; // 指示这个进程是否完成 进而判断作业是否结束
	// int proDone; // 指示已经结束的进程数量
	public PCBPool pcbPool;// 作业的进程池 进程加载入内存并且准备运行时候初始化进程池

	// 创建JCB 传参为进程数量 作业号 磁盘
	public void CreateJob(int PCBNum, int JobID, Disk disk) throws IOException, InterruptedException {
		this.JobID = JobID;
		this.ProcessAmount = PCBNum;
		this.Process = new PCB[PCBNum];

		// 创建TXT存储作业内容
		File fin = new File("job" + JobID + ".txt");
//		if(fin.exists()) {
//			fin.delete();					
//		}
		fin.createNewFile();
		FileWriter fw = new FileWriter(fin, true);
		// 作业第一行写入作业号和进程数量
		fw.write(String.format("%4d %4d %10d", this.JobID = JobID, this.ProcessAmount, System.nanoTime() / 1000000L));
		fw.write(System.getProperty("line.separator"));// 写入换行 不受操作系统限制

		// 确定PCB数量以后使用临时PCB创建所有PCB
		PCB tempPCB = new PCB();
		for (int i = 0; i < ProcessAmount; i++) {
		// 每生成一个进程就写入到文件一次
			tempPCB.ProID = i + 1000 * JobID; // 进程号用1000开始
			tempPCB.JobID = this.JobID;// 进程对应作业号
			tempPCB.Priority = new Random().nextInt(3) + 1;// 进程优先级 1-3
			Thread.sleep(50);
			tempPCB.InTime = System.nanoTime() / 1000000L; // 创建时间精确到毫秒
			tempPCB.ProState = Pro_State.CREATED;// 进程状态为0
			tempPCB.PSW = 0; // 进程创建后从第0条指令开始执行
			tempPCB.InstrucNum = new Random().nextInt(20) + 5;// 进程的指令数目（5-25条最多三页，最多一个作业占用30页）
			tempPCB.ProAddress = (i + 1) * 10 * JobID * 512 + (JobID - 1) * 40 * 512; // 进程地址开始
			tempPCB.Instructs = new PCB.InstructBlock[tempPCB.InstrucNum]; // 为内部类分配数组空间
			int tempADD = 0; // 为指令赋地址使用
			for (int j = 0; j < tempPCB.InstrucNum; j++) {
				tempPCB.Instructs[j] = tempPCB.new InstructBlock(); // 实例化内部类对象数组
				tempPCB.Instructs[j].InstrucID = j;
				// tempPCB.Instructs[i].InstrucContent
				tempPCB.Instructs[j].InstructState = new Random().nextInt(3); // 指令状态随机生成
				tempPCB.Instructs[j].InstructSource = new Random().nextInt(3); // 指令所需资源随机生成
				if(j==8) {
					tempPCB.Instructs[6].InstructState = 3;//P
					tempPCB.Instructs[7].InstructSource = 3;//访问互斥资源
					tempPCB.Instructs[8].InstructState = 4; //V
				}
				 
				switch (tempPCB.Instructs[j].InstructState) {
				case (0):
				case (1):
					tempPCB.Instructs[j].InstructRunTime = new Random().nextInt(4) * 10 + 10;
					break;
				case (2):
					tempPCB.Instructs[j].InstructRunTime = 50;
					break;
				}
				// 指令地址
				tempADD += j * 64;
				tempPCB.Instructs[j].InstructAddress = (tempPCB.ProAddress + tempADD % 512) + tempADD / 512;
			}
			// 计算需要的内存块数量
			tempPCB.ProMemoryNeed = (tempPCB.InstrucNum % 8 == 0) ? tempPCB.InstrucNum / 8 : tempPCB.InstrucNum / 8 + 1;
			for (int j = 0; j < tempPCB.ProMemoryNeed; j++) {
				Print2Disk(tempPCB.ProAddress / 512 + j, disk);
			}

			// 写入文件操作
			fw.write(String.format("%10d %4d %6d %4d %10d %4d %4d ", tempPCB.ProAddress, tempPCB.ProMemoryNeed,
					tempPCB.ProID, tempPCB.Priority, tempPCB.InTime, tempPCB.PSW, tempPCB.InstrucNum));
			// 指令内容逐条写入
			for (int j = 0; j < tempPCB.InstrucNum; j++) {
				fw.write(String.format("%4d %10d %4d %4d %4d", tempPCB.Instructs[j].InstrucID,
						tempPCB.Instructs[j].InstructAddress, tempPCB.Instructs[j].InstructState,
						tempPCB.Instructs[j].InstructRunTime,tempPCB.Instructs[j].InstructSource));
			}
			fw.write(System.getProperty("line.separator"));
		}
		fw.close();
		
	}

	// JCB保存到磁盘,传参为物理地址
	public void Print2Disk(int BlockID, Disk disk) {
		disk.AllocateDisk(BlockID); // 按照物理块号分配磁盘空间
	}

	// 判断作业是否结束 进而判断是否执行下一作业调入
	public boolean jobIsDone() {
			// 有进程未结束表示整个作业未结束
		for (int i = 0; i < ProIsDone.length; i++)
			if (!ProIsDone[i])
				return false;
		return true;
	}

		// 有进程到达
		public boolean haveProArrive(Clock clock) {
			// 取后备队列第一个作为判断，其到达时间<=当前时间则说明已到达 返回true
		if (pcbPool.BackUp.size() != 0 && pcbPool.BackUp.getFirst().InTime <= clock.TimeNow)
			return true;
		return false;
	}

		// 找到就绪队列数组中的最高优先级的队列
	public int FindFirstPriority() {
		if (pcbPool.Ready[1].size() != 0)
			return 1;
		else if (pcbPool.Ready[2].size() != 0)
			return 2;
		else if (pcbPool.Ready[3].size() != 0)
			return 3;
		return 0;
	}

	// 进程调度过程 返回值为True说明当前作业的进程全部调度结束
		public boolean ProSchedule() throws IOException {
			// this.proDone = 0;
			this.pcbPool = new PCBPool();// 开始调度时候初始化这个作业的进程池
			// 将所有进程加载进入线程池的后备队列 接下里所有操作在队列之间进行
			for (int i = 0; i < this.ProcessAmount; i++)
				pcbPool.BackUp.add(Process[i]);

			// 创建作业完成序列的显示文件
			
			File fout = new File("job" + this.JobID + "DoneSquence.txt");
			if(fout.exists()) {
				fout.delete();					
			}
			fout.createNewFile();
			FileWriter writer = new FileWriter(fout, true);
			// 写入文件的项目
			writer.write(String.format("进程号 优先级 创建时间 周转时间 完成时间"));

			// 进程调度的总循环
		while (!this.jobIsDone()) {

			pcbPool.ShowPCBPool();
//			OS.memory.ShowMemoryUsing();
			// 后备>就绪(对BackUp队列进行操作)
			if (haveProArrive(OS.clock)) {
				pcbPool.Ready[pcbPool.BackUp.getFirst().Priority].add(pcbPool.BackUp.getFirst());// 取出后备队列的第一个进程加入到对应优先级队列
				pcbPool.BackUp.getFirst().MadeReady();// 执行就绪原语
				pcbPool.BackUp.removeFirst();// 从后备队列中删除这个进程
			}

			// 就绪>执行>其他状态
			// 依次判断三个优先级队列 就绪队列不为空且CPU未被占用
			if (!OS.cpu.GetUsing()) {
			// 找到最紧急进程让他执行
				int p = FindFirstPriority();
				if (p != 0) {
					pcbPool.Execute = pcbPool.Ready[p].getFirst(); // 进入执行状态
					pcbPool.Ready[p].removeFirst();// 从就绪队列中移除
					// 为进程的执行原语设计了返回值类型
					// 返回值为0 表示时间片到了要进入就绪队列

					if (pcbPool.Execute.MadeExecute() == 0) {
						OS.cpu.ReSet();// 重置CPU为空闲状态
						pcbPool.AdaptPriority();// 调整进程池中的优先级
						pcbPool.Ready[pcbPool.Execute.Priority].add(pcbPool.Execute);// 将刚才执行的进程放入对应优先级的就绪队列
						pcbPool.Ready[pcbPool.Execute.Priority].getLast().MadeReady();// 执行他的就绪原语
					}
					// 1表示作业完成了 要执行结束原语
					else if (pcbPool.Execute.MadeExecute() == 1) {
						OS.cpu.ReSet(); // 重置CPU
						pcbPool.Execute.FinishPCB(writer);// 执行作业完成
						// proDone++;
						ProIsDone[pcbPool.Execute.ProID % 10] = true;
					} // 2 表示遇到了IO指令需要进入阻塞队列
					else {
						OS.cpu.ReSet();// 重置CPU
						pcbPool.Waiting.add(pcbPool.Execute);// 加入阻塞队列
						pcbPool.Waiting.getLast().WaitPCB();// 执行阻塞原语
					}
				}
			}

			// 阻塞>就绪
			if (pcbPool.Waiting.size() != 0) {
				System.out.println("这个线程阻塞着呢" + pcbPool.Waiting.getFirst().PSW);
				OS.cpu.DealIO(pcbPool.Waiting.getFirst().Instructs[pcbPool.Waiting.getFirst().PSW].InstructRunTime);
				pcbPool.Waiting.getFirst().PSW++;// 结束了这个IO指令
				pcbPool.Ready[pcbPool.Waiting.getFirst().Priority].add(pcbPool.Waiting.getFirst());// 加入对应的就绪队列
				pcbPool.Ready[pcbPool.Waiting.getFirst().Priority].getLast().WakeUpPCB();// 执行唤醒原语
				pcbPool.Waiting.removeFirst();
			}
		}
		writer.close();
		return true;
	}

}
