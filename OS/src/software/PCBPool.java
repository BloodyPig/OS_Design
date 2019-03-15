package software;

import java.util.LinkedList;

public class PCBPool {
	PCB Execute;// �����еĽ���(ռ��CPU)
	LinkedList<PCB> Waiting; // ������������
	LinkedList<PCB>[] Ready; // ���̾������� �������ȼ� ���Դ���������������
	LinkedList<PCB> BackUp; // ���̺󱸶��� (������δ�����ڴ�)

	// ���캯��
	public PCBPool() {
		// Ϊ�������п��ٿռ�
		Execute = new PCB();
		Waiting = new LinkedList<PCB>();
		BackUp = new LinkedList<PCB>();
		Ready = new LinkedList[4];
		for (int i = 0; i < Ready.length; i++) {
			Ready[i] = new LinkedList<PCB>();
		}
	}

	// �����ʾһ�½��̳�״̬
	public void ShowPCBPool() {
		if (Execute.ProID != 0) {
			System.out.println("ִ���еĽ���      " + Execute.ProID);
			System.out.println("ִ�е���ָ���   " + Execute.PSW);
		}
		System.out.println("�󱸶��д�С���������д�С " + BackUp.size() + " " + Waiting.size());
		System.out.println("�����������д�С " + Ready[1].size() + " " + Ready[2].size() + " " + Ready[3].size());
	}

	// �����������̵����ȼ�
	// ��������Ϊ ÿ����һ��ʱ��Ƭ ���̵����ȼ���һ
	// ��ֻ��������һ��ʱ��Ƭ�Ľ��̽��е��� ������ִ��>����״̬��
	public void AdaptPriority() {
		if (this.Execute.Priority < 2)
			this.Execute.Priority += 1;
	}
}
