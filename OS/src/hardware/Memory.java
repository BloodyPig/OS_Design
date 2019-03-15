package hardware;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import software.JCB;
import software.OS;
import software.PageUnit;

public class Memory {
	public int BlockUsed; // 已使用的块数
	public char MemorySize[][]; // 内存大小 初始化为64块 每块512字节 共32M
	public boolean MemoryBitMap[];// 内存占用位示图
	public PageUnit[] PageTable;// 页表
	public PageUnit[] TLB; // 快表
	public boolean PageTableState[];// 指示页表占用状态
	public boolean TLBState[];// 指示快表占用状态

	public int[] available; // 可利用资源数组
	public File MemoryFile; // 内存记录文件
	public FileWriter fww; // 写入流

	// 构造函数
	public Memory() {
		MemorySize = new char[64][256]; // 课设要求存储单元为双字节 Java中一个Char为两个字节
		MemoryBitMap = new boolean[64]; // 位图空间申请
		PageTable = new PageUnit[32]; // 只设计一级页表32项
		PageTableState = new boolean[32];// 页表空间申请
		TLB = new PageUnit[8]; // 设计快表只有八个页表项
		TLBState = new boolean[8];// 快表状态空间申请
		BlockUsed = 0;

		MemoryFile = new File("Memory.txt");
		try {
			fww = new FileWriter(MemoryFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		available = new int[4];
		available[0] = 60;
		available[1] = 100;
		available[2] = 50;
		available[3] = 1;// 互斥资源

		for (int i = 0; i < 64; i++) {
			MemoryBitMap[i] = false; // 内存状态初始化
			PageTable[i % 32] = new PageUnit();// new 一级页表的每一项
			PageTableState[i % 32] = false;// 页表状态初始化
			TLB[i % 8] = new PageUnit();// new 快表每一项
			TLBState[i % 8] = false;// 快表状态初始化
		}
	}

	// 分配资源进行死锁检测
	public void AllocateResAndDetect(int i) {
		available[i]--;
		if (available[i] <= 0) {
			System.out.println("发生死锁");
			// System.exit(0);
		}
	}

	// 释放进程占用的资源
	public void RecycleRes(int i) {
		available[i]++;
	}

	// 分配内存 找到一个大小适合的空闲内存空间 分给当前进程
	public void AllocateMemory(JCB job) {
		int s = 0;
		for (int i = 0; i < 64; i++) {
			// 从0开始判断内存占用情况 然后分配
			if (this.MemoryBitMap[i] == false) {
				this.MemoryBitMap[i] = true;
				BlockUsed++;// 已使用的内存数量加
				// 申请到的内存空间存到这个数组里
				job.MemoryOwn[s++] = i;
				// System.out.println(i);
			} else
				continue;
			// 若分配完所需内存
			if (s == job.MemorySize)
				break;
		}

	}

	// 重置页表
	public void ResetPageTable() {
		for (int i = 0; i < 32; i++) {
			PageTable[i].PageFrameNum = 0;
			PageTable[i].Stay = false;
			PageTable[i].Use = 0;
			PageTable[i].Modify = false;
		}
	}

	// 重置快表
	public void ResetTLB() {
		for (int i = 0; i < 8; i++) {
			TLB[i].PageFrameNum = 0;
			TLB[i].Stay = false;
			TLB[i].Use = 0;
			TLB[i].Modify = false;
		}
	}

	// 回收内存
	public void RecycleMemory(JCB job) {
		// 对于每一块被作业占据的内存
		for (int i = 0; i < job.MemoryOwn.length; i++) {
			if (this.MemoryBitMap[job.MemoryOwn[i]]) {
				this.MemoryBitMap[job.MemoryOwn[i]] = false;
				BlockUsed--;
			}
		}
		ResetPageTable();
		ResetTLB();
	}

	// 获得剩余内存空间
	public int GetMemoryRemain() {
		return 64 - this.BlockUsed;
	}

	// 展示内存块的占用情况
	public void ShowMemoryUsing() {
		// System.out.println("这是内存的使用状态");
		// for (int i = 0; i < MemoryBitMap.length; i++) {
		// if (MemoryBitMap[i])
		// System.out.print(1);
		// else {
		// System.out.print(0);
		// }
		// if ((i + 1) % 8 == 0)
		// System.out.println();
		// }

		try {
			fww.write("系统时间：" + OS.clock.getTime());
			fww.write(System.getProperty("line.separator"));
			for (int i = 0; i < 64; i++) {
				if (MemoryBitMap[i] == false)
					fww.write("0 ");
				else
					fww.write("1 ");
				if ((i + 1) % 8 == 0)
					fww.write(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // 写入换行 不受操作系统限制
	}

	// 输出快表的情况
	public void ShowTLB() {
		System.out.println("快表:编号 物理页号 驻留位 使用位 修改位");
		for (int i = 0; i < TLB.length; i++) {
			System.out.println(TLB[i].PageNum + " " + TLB[i].PageFrameNum + " " + TLB[i].Stay + " " + TLB[i].Use + " "
					+ TLB[i].Modify);
		}
	}

	// 输出一级页表的情况
	public void ShowPageTable() {
		System.out.println("一级页表:编号 物理页号 驻留位 使用位 修改位");
		for (int i = 0; i < PageTable.length; i++) {
			System.out.println(PageTable[i].PageNum + " " + PageTable[i].PageFrameNum + " " + PageTable[i].Stay + " "
					+ PageTable[i].Use + " " + PageTable[i].Modify);
		}
	}

	// 判断快表是否满 未满就返回位置靠前的空闲页号 否则返回-1
	public int FindInTLB() {
		for (int i = 0; i < TLBState.length; i++)
			// 有空闲说明没满 返回i
			if (!TLBState[i])
				return i;
		// 否则说明满了
		return -1;
	}

	// 判断一级页表是否满 未满就返回位置靠前的空闲页号 否则返回-1
	public int FindInPageTable() {
		for (int i = 0; i < PageTable.length; i++)
			if (!PageTableState[i])
				return i;
		return -1;
	}

	// 页表的LRU算法 返回值为被引用次数最多的页表项
	public int LRU_PageTable() {
		int max = 0;
		for (int i = 0; i < PageTable.length; i++) {
			max = PageTable[max].Use > PageTable[i].Use ? max : i;
			PageTable[i].Use++;
		}

		return max;
	}

	// 快表的LRU算法
	public int LRU_TLB() {
		int max = 0;
		for (int i = 0; i < TLB.length; i++) {
			max = TLB[max].Use > TLB[i].Use ? max : i;
			TLB[i].Use++;
		}

		return max;
	}

	// 更新一级页表 其中包括页表满的替换算法
	public void UpdatePageTable(int PGFnum) {
		int r = FindInPageTable();
		// 页表满 执行调换算法
		if (r == -1)
			r = LRU_PageTable();
		// 否则就加入一个页表项
		else
			PageTableState[r] = true;
		PageTable[r].PageNum = r;
		PageTable[r].PageFrameNum = PGFnum;
		PageTable[r].Stay = true;
		PageTable[r].Use = 0;
		PageTable[r].Modify = true;
	}

	// 更新快表 其中包括快表满的替换算法
	public void UpdateTLB(int PGFnum) {
		int r = FindInTLB();
		// 页表满 执行调换算法
		if (r == -1)
			r = LRU_TLB();
		// 否则就加入一个页表项
		else
			TLBState[r] = true;
		TLB[r].PageNum = r;
		TLB[r].PageFrameNum = PGFnum;
		TLB[r].Stay = true;
		TLB[r].Use = 0;
		TLB[r].Modify = true;
	}

	// 找到这个地址对应的页
	public int FindPage(int address) {
		System.out.println("Memory接到这个地址" + address);
		JCB job = OS.JobQueue.getFirst();// 取当前正在执行的作业
		// ShowMemoryUsing();
		// ShowTLB();
		// ShowPageTable();
		// 调用MMU的方法看是否在内存里
		int r = OS.mmu.isInMemory(address, OS.memory);
		if (r == -1) {
			// 返回值为-1 说明发生缺页中断 需要调页
			System.out.println("地址对应物理页号 " + address / 512 + "没有在内存中找到");
			
			// 因为没有再在内存中实际存放东西 调页是个假过程 调页完成后更新页表和快表就完事了
			UpdatePageTable(address / 512);// 向一级页表调页
			UpdateTLB(address / 512);// 向快表调页
		} else if (r == 1) {
			// 返回值为1说明在快表之中被找到
			System.out.println("地址对应物理页号 " + address / 512 + "在快表中找到");
			// 找到这个快表中的项 把它引用位变为0
			for (int i = 0; i < TLB.length; i++) {
				if (TLB[i].PageFrameNum == address / 512)
					TLB[i].Use = 0;
				else {
					if (TLB[i].Stay == true)
						TLB[i].Use++;
				}
			}
			// 对应的页表更新
			for (int i = 0; i < PageTable.length; i++) {
				if (PageTable[i].PageFrameNum == address / 512) {
					PageTable[i].Use = 0;
				} else if (PageTable[i].Stay == true)
					PageTable[i].Use++;
			}

		} else if (r == 2) {
			// 返回值为2说明在一级页表中被找到
			System.out.println("地址对应物理页号 " + address / 512 + "在一级页表中找到");
			// 找到这个一级页表中的项 把它引用位变为0
			for (int i = 0; i < PageTable.length; i++) {
				if (PageTable[i].PageFrameNum == address / 512)
					PageTable[i].Use = 0;
				else if (PageTable[i].Stay == true)
					PageTable[i].Use++;

			}
			// 在页表中找到需要更新快表内容
			UpdateTLB(address / 512);
		}

		return 0;
	}

}
