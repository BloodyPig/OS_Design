package hardware;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Disk {
	byte DiskSize[][][];
	boolean DiskBitMap[][];
	File fShowDisk;

	public Disk() {
		DiskSize = new byte[32][64][512]; // 一个柱面32个磁道 一个磁道64个扇区 一个扇区512B
		DiskBitMap = new boolean[32][64]; // 按块分配，位示图共32*8，由于一个指令占用64字节刚好用1字节表示
		fShowDisk = new File("Disk.txt");
		// 将所有磁盘块初始化为未使用
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 64; j++) {
				DiskBitMap[i][j] = false;
			}
		}
	}

	// 分配磁盘物理块
	public boolean AllocateDisk(int BlockID) {
		// 如果磁盘块被占用返回值 交于上级处理
		if (DiskBitMap[BlockID / 32][BlockID % 64])
			return false;
		else {
			DiskBitMap[BlockID / 32][BlockID % 64] = true;
			return true;
		}
	}

	// 回收磁盘物理块
	public void RecycleDisk(int BlockID) {
		DiskBitMap[BlockID / 32][BlockID % 64] = false;
	}

	// 显示磁盘使用情况到文件
	public void ShowDiskUsing() {
		try {
			fShowDisk.createNewFile();
			FileWriter fw = new FileWriter(fShowDisk, true);
			fw.write("磁盘使用状况：" + "     " + "系统时间：" + System.nanoTime() / 1000000L);
			fw.write(System.getProperty("line.separator"));// 写入换行 不受操作系统限制

			for (int i = 0; i < 32; i++) {
				fw.write("柱面" + i + ": ");
				for (int j = 0; j < 64; j++) {
					if (DiskBitMap[i][j]) {
						fw.write(" " + 1);
					}

					else
						fw.write(" " + 0);
				}
				fw.write(System.getProperty("line.separator"));// 写入换行 不受操作系统限制
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
