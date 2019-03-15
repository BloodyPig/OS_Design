package software;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import hardware.CPU;
import hardware.Clock;
import hardware.Disk;
import hardware.MMU;
import hardware.Memory;

public class OS implements Runnable {

	public static Disk disk;
	public static CPU cpu;
	public static Memory memory;
	public static Clock clock;
	public static MMU mmu;

	public static LinkedList<JCB> JobQueue; // 作业队列

	public OS() {
		// 程序开始初始化各个硬件
		disk = new Disk();// 即初始化磁盘
		cpu = new CPU(); // 初始化CPU
		memory = new Memory();// 初始化内存
		clock = new Clock(); // 初始化时钟中断
		mmu = new MMU(); // 初始化地址转换单元

		new Thread(clock).start(); // 系统初始化时候就将时钟启动 一直运行直到系统关闭
		// 创建一个作业队列
		JobQueue = new LinkedList<JCB>();
	}

	// 创建几个作业到磁盘并且记录磁盘使用情况
	public static void CeateJobs(int JobAmount) {
		for (int i = 0; i < JobAmount; i++) {
			JCB tempJob = new JCB();
			try {
				tempJob.CreateJob(new Random().nextInt(10) + 1, i + 1, disk); // 每个作业进程数量1-10
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			disk.ShowDiskUsing();
		}
	}

	// 作业初始化,从文件读入,每次只能有一个作业的进程在并发
	// 但根据内存使用情况可以多个作业在内存中等待，进入作业队列
	public static void InitJob(int JobID, LinkedList<JCB> JobQueue) {
		File fRead = new File("job" + JobID + ".txt");
		JCB tempJob = new JCB();
		try {
			Scanner reader = new Scanner(new FileInputStream(fRead));// 用于读作业
			tempJob.JobID = reader.nextInt();// 读作业号
			tempJob.ProcessAmount = reader.nextInt();// 读作业进程数量
			tempJob.InTime = reader.nextLong(); // 读作业创建时间
			tempJob.Process = new PCB[tempJob.ProcessAmount]; // new相对数量个进程
			tempJob.MemorySize = 0; // 作业需要内存在各个进程读取后计算
			tempJob.InMemory = false; // 创建这些作业时他们都在磁盘没有在内存
			tempJob.ProIsDone = new boolean[tempJob.ProcessAmount];// new 相对数量个进程指示

			for (int i = 0; i < tempJob.ProcessAmount; i++) {
				tempJob.ProIsDone[i] = false; // 先将每个进程标志为未完成
				reader.nextLine();// 换行读取
				tempJob.Process[i] = new PCB();// new每一个进程
				tempJob.Process[i].JobID = tempJob.JobID; // 所有进程的都属于这一个作业
				tempJob.Process[i].CreatePCB(reader); // 对每个进程执行进程创建原语
				tempJob.MemorySize += tempJob.Process[i].ProMemoryNeed;
			}
			tempJob.MemoryOwn = new int[tempJob.MemorySize]; // 初始化数组大小 表示只能申请这么多内存页

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 该作业的进程全部读取后加入作业队列\
		JobQueue.add(tempJob);
	}

	// 作业调度
	public static void ScheduleJob() throws IOException {
		// 作业调度开始 FCFS算法 按照作业的创建顺序加载进入内存
		while (!JobQueue.isEmpty()) {
			// 内存剩余空间大于作业需要的最大空间 保证作业一定可以进入内存申请到足够空间
			if (memory.GetMemoryRemain() >= JobQueue.getFirst().MemorySize) {
				// 分配作业需要的内存空间
				memory.AllocateMemory(JobQueue.getFirst());
				clock.TimeNow = JobQueue.getFirst().InTime; // 将线程Clock的时间更新为作业的创建时间
				// 开始进程调度
				if (JobQueue.getFirst().ProSchedule()) {
					// 作业运行结束 回收作业占据的内存页 从作业队头移除
					memory.RecycleMemory(JobQueue.getFirst());
					JobQueue.removeFirst();
				}
			} else {
				System.out.println("请扩充内存");
				System.exit(0);
			}
		}
		OS.memory.fww.close();
//		System.exit(0);
	}

	@Override
	public void run() {
		try {
			ScheduleJob();
		} catch (IOException e) {
			e.printStackTrace();
		} // 作业调度
	}
	
	// // 工程入口
	// public static void main(String[] args) throws IOException {
	// OS os = new OS();// 初始化系统
	//// CeateJobs(5); // 创建作业 （大量用随机数生成了进程序列到文件）
	// for (int i = 0; i < 5; i++) {
	// InitJob(i + 1, JobQueue); // 初始作业化（作业进入后备作业队列 ）
	// }
	// Thread tt = new Thread(os);
	// tt.start();
	// }

	

}
