package hardware;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Disk {
	byte DiskSize[][][];
	boolean DiskBitMap[][];
	File fShowDisk;

	public Disk() {
		DiskSize = new byte[32][64][512]; // һ������32���ŵ� һ���ŵ�64������ һ������512B
		DiskBitMap = new boolean[32][64]; // ������䣬λʾͼ��32*8������һ��ָ��ռ��64�ֽڸպ���1�ֽڱ�ʾ
		fShowDisk = new File("Disk.txt");
		// �����д��̿��ʼ��Ϊδʹ��
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 64; j++) {
				DiskBitMap[i][j] = false;
			}
		}
	}

	// ������������
	public boolean AllocateDisk(int BlockID) {
		// ������̿鱻ռ�÷���ֵ �����ϼ�����
		if (DiskBitMap[BlockID / 32][BlockID % 64])
			return false;
		else {
			DiskBitMap[BlockID / 32][BlockID % 64] = true;
			return true;
		}
	}

	// ���մ��������
	public void RecycleDisk(int BlockID) {
		DiskBitMap[BlockID / 32][BlockID % 64] = false;
	}

	// ��ʾ����ʹ��������ļ�
	public void ShowDiskUsing() {
		try {
			fShowDisk.createNewFile();
			FileWriter fw = new FileWriter(fShowDisk, true);
			fw.write("����ʹ��״����" + "     " + "ϵͳʱ�䣺" + System.nanoTime() / 1000000L);
			fw.write(System.getProperty("line.separator"));// д�뻻�� ���ܲ���ϵͳ����

			for (int i = 0; i < 32; i++) {
				fw.write("����" + i + ": ");
				for (int j = 0; j < 64; j++) {
					if (DiskBitMap[i][j]) {
						fw.write(" " + 1);
					}

					else
						fw.write(" " + 0);
				}
				fw.write(System.getProperty("line.separator"));// д�뻻�� ���ܲ���ϵͳ����
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
