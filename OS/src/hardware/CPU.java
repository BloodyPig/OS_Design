package hardware;

public class CPU {
	boolean Using; // �ж�CPU�Ƿ�ռ�������Ƚ��̽���ִ��̬
	// ���������� PCB ���е�ָ����Ϣ��ֱ��Ӧ
	int PC; // �������������¼ִ�е����̺�
	int PSW; // ״̬�ּĴ�������¼���е���ָ���,��ӦPSW
	int IR; // ָ��Ĵ���,�20�ַ���ʵ���ϼ�¼ָ������
	int Address; // ָ���ַ�Ĵ���

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

	// ��CPUʹ��״̬
	public void SetUsing(boolean using) {
		this.Using = using;
	}

	// ����CPU�Ŀ���״̬
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

	// ����CPU
	public void ReSet() {
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
		this.Address = 0;
		this.Using = false;
	}

	// ����һ��ʱ��ģ�������CPU��ִ��
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
