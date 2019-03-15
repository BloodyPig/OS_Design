package hardware;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import software.JCB;
import software.OS;
import software.PageUnit;

public class Memory {
	public int BlockUsed; // ��ʹ�õĿ���
	public char MemorySize[][]; // �ڴ��С ��ʼ��Ϊ64�� ÿ��512�ֽ� ��32M
	public boolean MemoryBitMap[];// �ڴ�ռ��λʾͼ
	public PageUnit[] PageTable;// ҳ��
	public PageUnit[] TLB; // ���
	public boolean PageTableState[];// ָʾҳ��ռ��״̬
	public boolean TLBState[];// ָʾ���ռ��״̬

	public int[] available; // ��������Դ����
	public File MemoryFile; // �ڴ��¼�ļ�
	public FileWriter fww; // д����

	// ���캯��
	public Memory() {
		MemorySize = new char[64][256]; // ����Ҫ��洢��ԪΪ˫�ֽ� Java��һ��CharΪ�����ֽ�
		MemoryBitMap = new boolean[64]; // λͼ�ռ�����
		PageTable = new PageUnit[32]; // ֻ���һ��ҳ��32��
		PageTableState = new boolean[32];// ҳ��ռ�����
		TLB = new PageUnit[8]; // ��ƿ��ֻ�а˸�ҳ����
		TLBState = new boolean[8];// ���״̬�ռ�����
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
		available[3] = 1;// ������Դ

		for (int i = 0; i < 64; i++) {
			MemoryBitMap[i] = false; // �ڴ�״̬��ʼ��
			PageTable[i % 32] = new PageUnit();// new һ��ҳ���ÿһ��
			PageTableState[i % 32] = false;// ҳ��״̬��ʼ��
			TLB[i % 8] = new PageUnit();// new ���ÿһ��
			TLBState[i % 8] = false;// ���״̬��ʼ��
		}
	}

	// ������Դ�����������
	public void AllocateResAndDetect(int i) {
		available[i]--;
		if (available[i] <= 0) {
			System.out.println("��������");
			// System.exit(0);
		}
	}

	// �ͷŽ���ռ�õ���Դ
	public void RecycleRes(int i) {
		available[i]++;
	}

	// �����ڴ� �ҵ�һ����С�ʺϵĿ����ڴ�ռ� �ָ���ǰ����
	public void AllocateMemory(JCB job) {
		int s = 0;
		for (int i = 0; i < 64; i++) {
			// ��0��ʼ�ж��ڴ�ռ����� Ȼ�����
			if (this.MemoryBitMap[i] == false) {
				this.MemoryBitMap[i] = true;
				BlockUsed++;// ��ʹ�õ��ڴ�������
				// ���뵽���ڴ�ռ�浽���������
				job.MemoryOwn[s++] = i;
				// System.out.println(i);
			} else
				continue;
			// �������������ڴ�
			if (s == job.MemorySize)
				break;
		}

	}

	// ����ҳ��
	public void ResetPageTable() {
		for (int i = 0; i < 32; i++) {
			PageTable[i].PageFrameNum = 0;
			PageTable[i].Stay = false;
			PageTable[i].Use = 0;
			PageTable[i].Modify = false;
		}
	}

	// ���ÿ��
	public void ResetTLB() {
		for (int i = 0; i < 8; i++) {
			TLB[i].PageFrameNum = 0;
			TLB[i].Stay = false;
			TLB[i].Use = 0;
			TLB[i].Modify = false;
		}
	}

	// �����ڴ�
	public void RecycleMemory(JCB job) {
		// ����ÿһ�鱻��ҵռ�ݵ��ڴ�
		for (int i = 0; i < job.MemoryOwn.length; i++) {
			if (this.MemoryBitMap[job.MemoryOwn[i]]) {
				this.MemoryBitMap[job.MemoryOwn[i]] = false;
				BlockUsed--;
			}
		}
		ResetPageTable();
		ResetTLB();
	}

	// ���ʣ���ڴ�ռ�
	public int GetMemoryRemain() {
		return 64 - this.BlockUsed;
	}

	// չʾ�ڴ���ռ�����
	public void ShowMemoryUsing() {
		// System.out.println("�����ڴ��ʹ��״̬");
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
			fww.write("ϵͳʱ�䣺" + OS.clock.getTime());
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
		} // д�뻻�� ���ܲ���ϵͳ����
	}

	// ����������
	public void ShowTLB() {
		System.out.println("���:��� ����ҳ�� פ��λ ʹ��λ �޸�λ");
		for (int i = 0; i < TLB.length; i++) {
			System.out.println(TLB[i].PageNum + " " + TLB[i].PageFrameNum + " " + TLB[i].Stay + " " + TLB[i].Use + " "
					+ TLB[i].Modify);
		}
	}

	// ���һ��ҳ������
	public void ShowPageTable() {
		System.out.println("һ��ҳ��:��� ����ҳ�� פ��λ ʹ��λ �޸�λ");
		for (int i = 0; i < PageTable.length; i++) {
			System.out.println(PageTable[i].PageNum + " " + PageTable[i].PageFrameNum + " " + PageTable[i].Stay + " "
					+ PageTable[i].Use + " " + PageTable[i].Modify);
		}
	}

	// �жϿ���Ƿ��� δ���ͷ���λ�ÿ�ǰ�Ŀ���ҳ�� ���򷵻�-1
	public int FindInTLB() {
		for (int i = 0; i < TLBState.length; i++)
			// �п���˵��û�� ����i
			if (!TLBState[i])
				return i;
		// ����˵������
		return -1;
	}

	// �ж�һ��ҳ���Ƿ��� δ���ͷ���λ�ÿ�ǰ�Ŀ���ҳ�� ���򷵻�-1
	public int FindInPageTable() {
		for (int i = 0; i < PageTable.length; i++)
			if (!PageTableState[i])
				return i;
		return -1;
	}

	// ҳ���LRU�㷨 ����ֵΪ�����ô�������ҳ����
	public int LRU_PageTable() {
		int max = 0;
		for (int i = 0; i < PageTable.length; i++) {
			max = PageTable[max].Use > PageTable[i].Use ? max : i;
			PageTable[i].Use++;
		}

		return max;
	}

	// ����LRU�㷨
	public int LRU_TLB() {
		int max = 0;
		for (int i = 0; i < TLB.length; i++) {
			max = TLB[max].Use > TLB[i].Use ? max : i;
			TLB[i].Use++;
		}

		return max;
	}

	// ����һ��ҳ�� ���а���ҳ�������滻�㷨
	public void UpdatePageTable(int PGFnum) {
		int r = FindInPageTable();
		// ҳ���� ִ�е����㷨
		if (r == -1)
			r = LRU_PageTable();
		// ����ͼ���һ��ҳ����
		else
			PageTableState[r] = true;
		PageTable[r].PageNum = r;
		PageTable[r].PageFrameNum = PGFnum;
		PageTable[r].Stay = true;
		PageTable[r].Use = 0;
		PageTable[r].Modify = true;
	}

	// ���¿�� ���а�����������滻�㷨
	public void UpdateTLB(int PGFnum) {
		int r = FindInTLB();
		// ҳ���� ִ�е����㷨
		if (r == -1)
			r = LRU_TLB();
		// ����ͼ���һ��ҳ����
		else
			TLBState[r] = true;
		TLB[r].PageNum = r;
		TLB[r].PageFrameNum = PGFnum;
		TLB[r].Stay = true;
		TLB[r].Use = 0;
		TLB[r].Modify = true;
	}

	// �ҵ������ַ��Ӧ��ҳ
	public int FindPage(int address) {
		System.out.println("Memory�ӵ������ַ" + address);
		JCB job = OS.JobQueue.getFirst();// ȡ��ǰ����ִ�е���ҵ
		// ShowMemoryUsing();
		// ShowTLB();
		// ShowPageTable();
		// ����MMU�ķ������Ƿ����ڴ���
		int r = OS.mmu.isInMemory(address, OS.memory);
		if (r == -1) {
			// ����ֵΪ-1 ˵������ȱҳ�ж� ��Ҫ��ҳ
			System.out.println("��ַ��Ӧ����ҳ�� " + address / 512 + "û�����ڴ����ҵ�");
			
			// ��Ϊû�������ڴ���ʵ�ʴ�Ŷ��� ��ҳ�Ǹ��ٹ��� ��ҳ��ɺ����ҳ��Ϳ���������
			UpdatePageTable(address / 512);// ��һ��ҳ���ҳ
			UpdateTLB(address / 512);// �����ҳ
		} else if (r == 1) {
			// ����ֵΪ1˵���ڿ��֮�б��ҵ�
			System.out.println("��ַ��Ӧ����ҳ�� " + address / 512 + "�ڿ�����ҵ�");
			// �ҵ��������е��� ��������λ��Ϊ0
			for (int i = 0; i < TLB.length; i++) {
				if (TLB[i].PageFrameNum == address / 512)
					TLB[i].Use = 0;
				else {
					if (TLB[i].Stay == true)
						TLB[i].Use++;
				}
			}
			// ��Ӧ��ҳ�����
			for (int i = 0; i < PageTable.length; i++) {
				if (PageTable[i].PageFrameNum == address / 512) {
					PageTable[i].Use = 0;
				} else if (PageTable[i].Stay == true)
					PageTable[i].Use++;
			}

		} else if (r == 2) {
			// ����ֵΪ2˵����һ��ҳ���б��ҵ�
			System.out.println("��ַ��Ӧ����ҳ�� " + address / 512 + "��һ��ҳ�����ҵ�");
			// �ҵ����һ��ҳ���е��� ��������λ��Ϊ0
			for (int i = 0; i < PageTable.length; i++) {
				if (PageTable[i].PageFrameNum == address / 512)
					PageTable[i].Use = 0;
				else if (PageTable[i].Stay == true)
					PageTable[i].Use++;

			}
			// ��ҳ�����ҵ���Ҫ���¿������
			UpdateTLB(address / 512);
		}

		return 0;
	}

}
