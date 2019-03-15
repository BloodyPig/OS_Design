package hardware;

public class CPU {
	boolean Using; // 判断CPU是否被占用来调度进程进入执行态
	// 下列三项与 PCB 类中的指令信息块分别对应
	int PC; // 程序计数器，记录执行到进程号
	int PSW; // 状态字寄存器，记录运行到的指令号,对应PSW
	int IR; // 指令寄存器,最长20字符，实际上记录指令类型
	int Address; // 指令地址寄存器

	public CPU() {
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
		this.Address = 0;
		this.Using = false;
	}

	public void SetPC(int pc) {
		this.PC = pc;
	}

	public void SetIR(int ir) {
		this.IR = ir;
	}

	public void SetPSW(int psw) {
		this.PSW = psw;
	}

	public void SetAddress(int Add) {
		this.Address = Add;
	}

	// 置CPU使用状态
	public void SetUsing(boolean using) {
		this.Using = using;
	}

	// 返回CPU的空闲状态
	public boolean GetUsing() {
		return Using;
	}

	public int GetPC() {
		return PC;
	}

	public int GetIR() {
		return IR;
	}

	public int GetPSW() {
		return PSW;
	}

	public int GetAddress() {
		return Address;
	}

	// 重置CPU
	public void ReSet() {
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
		this.Address = 0;
		this.Using = false;
	}

	// 挂起一段时间模拟进程在CPU中执行
	public void RunSomeTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void DealIO(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
