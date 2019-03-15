
package software;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;
//PCB�̳��߳���
public class PCB {

	// �����ָ��ṹ�� ��Ž��̵�ָ�����,���ΪPCB���ڲ���
	public class InstructBlock {
		int InstrucID; // ָ����
		// int InstrucContent; // ָ������
		int InstructState; // ָ��״̬ 0 ��ʾϵͳ���á�1 ��ʾ�û�̬���������2 ��ʾ I/O ����
		int InstructRunTime; // ��ǰָ����Ҫִ�е�ʱ��
		int InstructAddress; // ָ���߼���ַ����ProID*100 + random[0,99]��
		int InstructSource;// ָ��������Դ��0-3,3Ϊ�ٽ���Դ
	}


	// ����״̬����Ϊö������
	enum Pro_State {
		CREATED, // �ձ������ĳ�ʼ״̬
		EXECUTE, // ����״̬
		READY, // ����״̬
		WAITING, // ����״̬
	}

	int ProID; // ���̺�
	int JobID; // ��Ӧ��ҵ��
	int Priority; // �������ȼ���û�кܶ����,����Ϊ�ַ��ͣ�
	long InTime; // ���̴���ʱ��,�����õļ�ʱ�������
	Pro_State ProState; // ��������״̬������Ϊ�ַ�0��1��2��3, 0��ʾ�մ��� 1��ʾִ�� 2��ʾ���� 3��ʾ����
	int ProAddress; // ���̵���ʼ�����ַ
	int ProMemoryNeed; // ������Ҫ���ڴ��С�����ڴ��Ϊ��λ��
	int AlreadyRunTime; // �����Ѿ�����ʱ������ռ��CPU��ʱ��
	int PSW; // ����״̬�ּĴ�������¼���е���ָ���
	int EndTime; // ���̽���ʱ�̣��봴��ʱ��ó�������תʱ��
	int AllRunTime; // ������תʱ��
	int InstrucNum; // ���̰�����ָ����Ŀ
	InstructBlock Instructs[]; // ָ������

	int request[]; // ����������Դ������
	int mutex=1;
	LinkedList<PCB> mutex_waitQueue; // �ź����еĵȴ�����
	// ���캯��
	public PCB() {
		this.ProID = 0;
		request = new int[4];
		for(int i=0;i<4;i++) {
			request[i]=0;
		}
		mutex_waitQueue= new LinkedList<PCB>();
	}

	// ����ԭ�� ���������Ϊ���ļ���ȡ��ز���
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

	// ����ԭ��
		public void FinishPCB(FileWriter writer) {
			// �ͷ�ռ�õ�������Դ
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
		} // д�뻻�� ���ܲ���ϵͳ����

		}

		// ����ԭ��
		public void WaitPCB() {
			this.ProState = Pro_State.WAITING;
		}

		// ����ԭ��
	public void WakeUpPCB() {
		this.ProState = Pro_State.READY;
	}

	// ����ԭ��
		public void MadeReady() {
			this.ProState = Pro_State.READY;
		}

		// ִ��ԭ��
		// 0 ��ʾʱ��Ƭ����Ҫ�����������
		// 1��ʾ��ҵ����� Ҫִ�н���ԭ��
		// 2 ��ʾ������IOָ����Ҫ������������
		public int MadeExecute() {
			this.ProState = Pro_State.EXECUTE;// ����״̬��Ϊִ��
			OS.cpu.SetUsing(true);// CPU��Ϊռ��
			OS.cpu.SetPC(this.ProID);// CPU�����������Ϊ��ǰ���̺�

			// ����ʱ������һ����ʱ��
			int timeCnt = 0;
			while (timeCnt <= 500) {
				// ���ִ�е������һ��ָ�� ֱ���˳�
				if (this.PSW >= InstrucNum)
					return 1;

				OS.cpu.SetPSW(PSW);// ��ָ���
				OS.cpu.SetIR(Instructs[PSW].InstructState); // ��ָ��״̬
				OS.cpu.SetAddress(Instructs[PSW].InstructAddress);// ��ָ���ַ
				OS.memory.FindPage(OS.cpu.GetAddress());// ����ָ���ַ�ҵ����ҳ
				// ����ָ��״̬�ж��Ƿ�����			
			if (Instructs[PSW].InstructState == 3) {//P����
				mutex--;
				if(mutex<0) {
					mutex_waitQueue.add(this);//�����ź����йصĵȴ�����
					this.WaitPCB();//������Ϊ����̬	
					System.out.println(this.ProID+"�Ž�������");
				}
				PSW++;
			}
			else if(Instructs[PSW].InstructState == 4) {//V����
				mutex++;
				if(mutex<=0) {
					mutex_waitQueue.removeFirst();//���ź����йض����еĵ�һ�������Ƴ�
					this.MadeReady();//������Ϊ����̬
					System.out.println(mutex_waitQueue.removeFirst().ProID+"�Ž����Ƴ�");
				}
				PSW++;
			}
			else if(Instructs[PSW].InstructState == 2){
				// �����IOָ��
				OS.cpu.DealIO(Instructs[PSW].InstructRunTime);
				return 2;
			}
			//���������ָ�������Դ������������⣬���������������
			else {					
				request[Instructs[PSW].InstructState]++;//��ָ��״̬������Դ����
				OS.memory.AllocateResAndDetect(Instructs[PSW].InstructState);//���������Դ
				OS.cpu.RunSomeTime(Instructs[PSW].InstructRunTime);//ִ�����ָ��
				timeCnt += Instructs[PSW].InstructRunTime;//ʱ��Ƭ��
				this.AlreadyRunTime += Instructs[PSW].InstructRunTime;//ָ���ܵ�����ʱ���
				PSW++;		
			}

		}

		return 0;// ����ֵ�в�ͬ���� �����ϲ���ȳ����ж���һ������
	}

	
}
