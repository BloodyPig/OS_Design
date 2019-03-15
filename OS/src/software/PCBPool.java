package software;

import java.util.LinkedList;

public class PCBPool {
	PCB Execute;// 运行中的进程(占据CPU)
	LinkedList<PCB> Waiting; // 进程阻塞队列
	LinkedList<PCB>[] Ready; // 进程就绪队列 三个优先级 所以创建三个就绪队列
	LinkedList<PCB> BackUp; // 进程后备队列 (磁盘尚未进入内存)

	// 构造函数
	public PCBPool() {
		// 为各个队列开辟空间
		Execute = new PCB();
		Waiting = new LinkedList<PCB>();
		BackUp = new LinkedList<PCB>();
		Ready = new LinkedList[4];
		for (int i = 0; i < Ready.length; i++) {
			Ready[i] = new LinkedList<PCB>();
		}
	}

	// 输出显示一下进程池状态
	public void ShowPCBPool() {
		if (Execute.ProID != 0) {
			System.out.println("执行中的进程      " + Execute.ProID);
			System.out.println("执行到的指令号   " + Execute.PSW);
		}
		System.out.println("后备队列大小和阻塞队列大小 " + BackUp.size() + " " + Waiting.size());
		System.out.println("三个就绪队列大小 " + Ready[1].size() + " " + Ready[2].size() + " " + Ready[3].size());
	}

	// 调整各个进程的优先级
	// 调整规则为 每运行一个时间片 进程的优先级降一
	// 即只对运行完一个时间片的进程进行调整 发生在执行>就绪状态后
	public void AdaptPriority() {
		if (this.Execute.Priority < 2)
			this.Execute.Priority += 1;
	}
}
