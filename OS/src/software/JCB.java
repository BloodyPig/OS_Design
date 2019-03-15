
package software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import hardware.Clock;
import hardware.Disk;
import software.PCB.Pro_State;

//��ҵ�����ǽ���ִ��ǰ��״̬�����԰����������
//������һ�ص㽫��Ʋ�ͬ������ҵ���ֱ������ͬ�����Ľ���
//��ҵ����ʹ�������ȷ�������㷨,��������Ҫ�ڴ��С�ж��Ƿ��㹻�俪ʼִ��
public class JCB {
	public int JobID; // ��ҵ��
	public int ProcessAmount; // ��������
	public PCB Process[];// ��ҵ�����Ľ���
	public long InTime; // ��ҵ�ύʱ��
	public int MemorySize;// �����ڴ��С ��ҳΪ��λ
	public boolean InMemory; // ��־����ҵ�Ƿ����ڴ�
	public int MemoryOwn[]; // ��ҵ���뵽���ڴ��������¼
	public boolean ProIsDone[]; // ָʾ��������Ƿ���� �����ж���ҵ�Ƿ����
	// int proDone; // ָʾ�Ѿ������Ľ�������
	public PCBPool pcbPool;// ��ҵ�Ľ��̳� ���̼������ڴ沢��׼������ʱ���ʼ�����̳�

	// ����JCB ����Ϊ�������� ��ҵ�� ����
	public void CreateJob(int PCBNum, int JobID, Disk disk) throws IOException, InterruptedException {
		this.JobID = JobID;
		this.ProcessAmount = PCBNum;
		this.Process = new PCB[PCBNum];

		// ����TXT�洢��ҵ����
		File fin = new File("job" + JobID + ".txt");
//		if(fin.exists()) {
//			fin.delete();					
//		}
		fin.createNewFile();
		FileWriter fw = new FileWriter(fin, true);
		// ��ҵ��һ��д����ҵ�źͽ�������
		fw.write(String.format("%4d %4d %10d", this.JobID = JobID, this.ProcessAmount, System.nanoTime() / 1000000L));
		fw.write(System.getProperty("line.separator"));// д�뻻�� ���ܲ���ϵͳ����

		// ȷ��PCB�����Ժ�ʹ����ʱPCB��������PCB
		PCB tempPCB = new PCB();
		for (int i = 0; i < ProcessAmount; i++) {
		// ÿ����һ�����̾�д�뵽�ļ�һ��
			tempPCB.ProID = i + 1000 * JobID; // ���̺���1000��ʼ
			tempPCB.JobID = this.JobID;// ���̶�Ӧ��ҵ��
			tempPCB.Priority = new Random().nextInt(3) + 1;// �������ȼ� 1-3
			Thread.sleep(50);
			tempPCB.InTime = System.nanoTime() / 1000000L; // ����ʱ�侫ȷ������
			tempPCB.ProState = Pro_State.CREATED;// ����״̬Ϊ0
			tempPCB.PSW = 0; // ���̴�����ӵ�0��ָ�ʼִ��
			tempPCB.InstrucNum = new Random().nextInt(20) + 5;// ���̵�ָ����Ŀ��5-25�������ҳ�����һ����ҵռ��30ҳ��
			tempPCB.ProAddress = (i + 1) * 10 * JobID * 512 + (JobID - 1) * 40 * 512; // ���̵�ַ��ʼ
			tempPCB.Instructs = new PCB.InstructBlock[tempPCB.InstrucNum]; // Ϊ�ڲ����������ռ�
			int tempADD = 0; // Ϊָ���ַʹ��
			for (int j = 0; j < tempPCB.InstrucNum; j++) {
				tempPCB.Instructs[j] = tempPCB.new InstructBlock(); // ʵ�����ڲ����������
				tempPCB.Instructs[j].InstrucID = j;
				// tempPCB.Instructs[i].InstrucContent
				tempPCB.Instructs[j].InstructState = new Random().nextInt(3); // ָ��״̬�������
				tempPCB.Instructs[j].InstructSource = new Random().nextInt(3); // ָ��������Դ�������
				if(j==8) {
					tempPCB.Instructs[6].InstructState = 3;//P
					tempPCB.Instructs[7].InstructSource = 3;//���ʻ�����Դ
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
				// ָ���ַ
				tempADD += j * 64;
				tempPCB.Instructs[j].InstructAddress = (tempPCB.ProAddress + tempADD % 512) + tempADD / 512;
			}
			// ������Ҫ���ڴ������
			tempPCB.ProMemoryNeed = (tempPCB.InstrucNum % 8 == 0) ? tempPCB.InstrucNum / 8 : tempPCB.InstrucNum / 8 + 1;
			for (int j = 0; j < tempPCB.ProMemoryNeed; j++) {
				Print2Disk(tempPCB.ProAddress / 512 + j, disk);
			}

			// д���ļ�����
			fw.write(String.format("%10d %4d %6d %4d %10d %4d %4d ", tempPCB.ProAddress, tempPCB.ProMemoryNeed,
					tempPCB.ProID, tempPCB.Priority, tempPCB.InTime, tempPCB.PSW, tempPCB.InstrucNum));
			// ָ����������д��
			for (int j = 0; j < tempPCB.InstrucNum; j++) {
				fw.write(String.format("%4d %10d %4d %4d %4d", tempPCB.Instructs[j].InstrucID,
						tempPCB.Instructs[j].InstructAddress, tempPCB.Instructs[j].InstructState,
						tempPCB.Instructs[j].InstructRunTime,tempPCB.Instructs[j].InstructSource));
			}
			fw.write(System.getProperty("line.separator"));
		}
		fw.close();
		
	}

	// JCB���浽����,����Ϊ�����ַ
	public void Print2Disk(int BlockID, Disk disk) {
		disk.AllocateDisk(BlockID); // ���������ŷ�����̿ռ�
	}

	// �ж���ҵ�Ƿ���� �����ж��Ƿ�ִ����һ��ҵ����
	public boolean jobIsDone() {
			// �н���δ������ʾ������ҵδ����
		for (int i = 0; i < ProIsDone.length; i++)
			if (!ProIsDone[i])
				return false;
		return true;
	}

		// �н��̵���
		public boolean haveProArrive(Clock clock) {
			// ȡ�󱸶��е�һ����Ϊ�жϣ��䵽��ʱ��<=��ǰʱ����˵���ѵ��� ����true
		if (pcbPool.BackUp.size() != 0 && pcbPool.BackUp.getFirst().InTime <= clock.TimeNow)
			return true;
		return false;
	}

		// �ҵ��������������е�������ȼ��Ķ���
	public int FindFirstPriority() {
		if (pcbPool.Ready[1].size() != 0)
			return 1;
		else if (pcbPool.Ready[2].size() != 0)
			return 2;
		else if (pcbPool.Ready[3].size() != 0)
			return 3;
		return 0;
	}

	// ���̵��ȹ��� ����ֵΪTrue˵����ǰ��ҵ�Ľ���ȫ�����Ƚ���
		public boolean ProSchedule() throws IOException {
			// this.proDone = 0;
			this.pcbPool = new PCBPool();// ��ʼ����ʱ���ʼ�������ҵ�Ľ��̳�
			// �����н��̼��ؽ����̳߳صĺ󱸶��� ���������в����ڶ���֮�����
			for (int i = 0; i < this.ProcessAmount; i++)
				pcbPool.BackUp.add(Process[i]);

			// ������ҵ������е���ʾ�ļ�
			
			File fout = new File("job" + this.JobID + "DoneSquence.txt");
			if(fout.exists()) {
				fout.delete();					
			}
			fout.createNewFile();
			FileWriter writer = new FileWriter(fout, true);
			// д���ļ�����Ŀ
			writer.write(String.format("���̺� ���ȼ� ����ʱ�� ��תʱ�� ���ʱ��"));

			// ���̵��ȵ���ѭ��
		while (!this.jobIsDone()) {

			pcbPool.ShowPCBPool();
//			OS.memory.ShowMemoryUsing();
			// ��>����(��BackUp���н��в���)
			if (haveProArrive(OS.clock)) {
				pcbPool.Ready[pcbPool.BackUp.getFirst().Priority].add(pcbPool.BackUp.getFirst());// ȡ���󱸶��еĵ�һ�����̼��뵽��Ӧ���ȼ�����
				pcbPool.BackUp.getFirst().MadeReady();// ִ�о���ԭ��
				pcbPool.BackUp.removeFirst();// �Ӻ󱸶�����ɾ���������
			}

			// ����>ִ��>����״̬
			// �����ж��������ȼ����� �������в�Ϊ����CPUδ��ռ��
			if (!OS.cpu.GetUsing()) {
			// �ҵ��������������ִ��
				int p = FindFirstPriority();
				if (p != 0) {
					pcbPool.Execute = pcbPool.Ready[p].getFirst(); // ����ִ��״̬
					pcbPool.Ready[p].removeFirst();// �Ӿ����������Ƴ�
					// Ϊ���̵�ִ��ԭ������˷���ֵ����
					// ����ֵΪ0 ��ʾʱ��Ƭ����Ҫ�����������

					if (pcbPool.Execute.MadeExecute() == 0) {
						OS.cpu.ReSet();// ����CPUΪ����״̬
						pcbPool.AdaptPriority();// �������̳��е����ȼ�
						pcbPool.Ready[pcbPool.Execute.Priority].add(pcbPool.Execute);// ���ղ�ִ�еĽ��̷����Ӧ���ȼ��ľ�������
						pcbPool.Ready[pcbPool.Execute.Priority].getLast().MadeReady();// ִ�����ľ���ԭ��
					}
					// 1��ʾ��ҵ����� Ҫִ�н���ԭ��
					else if (pcbPool.Execute.MadeExecute() == 1) {
						OS.cpu.ReSet(); // ����CPU
						pcbPool.Execute.FinishPCB(writer);// ִ����ҵ���
						// proDone++;
						ProIsDone[pcbPool.Execute.ProID % 10] = true;
					} // 2 ��ʾ������IOָ����Ҫ������������
					else {
						OS.cpu.ReSet();// ����CPU
						pcbPool.Waiting.add(pcbPool.Execute);// ������������
						pcbPool.Waiting.getLast().WaitPCB();// ִ������ԭ��
					}
				}
			}

			// ����>����
			if (pcbPool.Waiting.size() != 0) {
				System.out.println("����߳���������" + pcbPool.Waiting.getFirst().PSW);
				OS.cpu.DealIO(pcbPool.Waiting.getFirst().Instructs[pcbPool.Waiting.getFirst().PSW].InstructRunTime);
				pcbPool.Waiting.getFirst().PSW++;// ���������IOָ��
				pcbPool.Ready[pcbPool.Waiting.getFirst().Priority].add(pcbPool.Waiting.getFirst());// �����Ӧ�ľ�������
				pcbPool.Ready[pcbPool.Waiting.getFirst().Priority].getLast().WakeUpPCB();// ִ�л���ԭ��
				pcbPool.Waiting.removeFirst();
			}
		}
		writer.close();
		return true;
	}

}
