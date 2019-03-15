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

	public static LinkedList<JCB> JobQueue; // ��ҵ����

	public OS() {
		// ����ʼ��ʼ������Ӳ��
		disk = new Disk();// ����ʼ������
		cpu = new CPU(); // ��ʼ��CPU
		memory = new Memory();// ��ʼ���ڴ�
		clock = new Clock(); // ��ʼ��ʱ���ж�
		mmu = new MMU(); // ��ʼ����ַת����Ԫ

		new Thread(clock).start(); // ϵͳ��ʼ��ʱ��ͽ�ʱ������ һֱ����ֱ��ϵͳ�ر�
		// ����һ����ҵ����
		JobQueue = new LinkedList<JCB>();
	}

	// ����������ҵ�����̲��Ҽ�¼����ʹ�����
	public static void CeateJobs(int JobAmount) {
		for (int i = 0; i < JobAmount; i++) {
			JCB tempJob = new JCB();
			try {
				tempJob.CreateJob(new Random().nextInt(10) + 1, i + 1, disk); // ÿ����ҵ��������1-10
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			disk.ShowDiskUsing();
		}
	}

	// ��ҵ��ʼ��,���ļ�����,ÿ��ֻ����һ����ҵ�Ľ����ڲ���
	// �������ڴ�ʹ��������Զ����ҵ���ڴ��еȴ���������ҵ����
	public static void InitJob(int JobID, LinkedList<JCB> JobQueue) {
		File fRead = new File("job" + JobID + ".txt");
		JCB tempJob = new JCB();
		try {
			Scanner reader = new Scanner(new FileInputStream(fRead));// ���ڶ���ҵ
			tempJob.JobID = reader.nextInt();// ����ҵ��
			tempJob.ProcessAmount = reader.nextInt();// ����ҵ��������
			tempJob.InTime = reader.nextLong(); // ����ҵ����ʱ��
			tempJob.Process = new PCB[tempJob.ProcessAmount]; // new�������������
			tempJob.MemorySize = 0; // ��ҵ��Ҫ�ڴ��ڸ������̶�ȡ�����
			tempJob.InMemory = false; // ������Щ��ҵʱ���Ƕ��ڴ���û�����ڴ�
			tempJob.ProIsDone = new boolean[tempJob.ProcessAmount];// new �������������ָʾ

			for (int i = 0; i < tempJob.ProcessAmount; i++) {
				tempJob.ProIsDone[i] = false; // �Ƚ�ÿ�����̱�־Ϊδ���
				reader.nextLine();// ���ж�ȡ
				tempJob.Process[i] = new PCB();// newÿһ������
				tempJob.Process[i].JobID = tempJob.JobID; // ���н��̵Ķ�������һ����ҵ
				tempJob.Process[i].CreatePCB(reader); // ��ÿ������ִ�н��̴���ԭ��
				tempJob.MemorySize += tempJob.Process[i].ProMemoryNeed;
			}
			tempJob.MemoryOwn = new int[tempJob.MemorySize]; // ��ʼ�������С ��ʾֻ��������ô���ڴ�ҳ

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// ����ҵ�Ľ���ȫ����ȡ�������ҵ����\
		JobQueue.add(tempJob);
	}

	// ��ҵ����
	public static void ScheduleJob() throws IOException {
		// ��ҵ���ȿ�ʼ FCFS�㷨 ������ҵ�Ĵ���˳����ؽ����ڴ�
		while (!JobQueue.isEmpty()) {
			// �ڴ�ʣ��ռ������ҵ��Ҫ�����ռ� ��֤��ҵһ�����Խ����ڴ����뵽�㹻�ռ�
			if (memory.GetMemoryRemain() >= JobQueue.getFirst().MemorySize) {
				// ������ҵ��Ҫ���ڴ�ռ�
				memory.AllocateMemory(JobQueue.getFirst());
				clock.TimeNow = JobQueue.getFirst().InTime; // ���߳�Clock��ʱ�����Ϊ��ҵ�Ĵ���ʱ��
				// ��ʼ���̵���
				if (JobQueue.getFirst().ProSchedule()) {
					// ��ҵ���н��� ������ҵռ�ݵ��ڴ�ҳ ����ҵ��ͷ�Ƴ�
					memory.RecycleMemory(JobQueue.getFirst());
					JobQueue.removeFirst();
				}
			} else {
				System.out.println("�������ڴ�");
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
		} // ��ҵ����
	}
	
	// // �������
	// public static void main(String[] args) throws IOException {
	// OS os = new OS();// ��ʼ��ϵͳ
	//// CeateJobs(5); // ������ҵ ������������������˽������е��ļ���
	// for (int i = 0; i < 5; i++) {
	// InitJob(i + 1, JobQueue); // ��ʼ��ҵ������ҵ�������ҵ���� ��
	// }
	// Thread tt = new Thread(os);
	// tt.start();
	// }

	

}
