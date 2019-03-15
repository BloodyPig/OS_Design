package hardware;

import software.PageUnit;

//地址转换逻辑单元
public class MMU {

	// 查找快表
	public boolean isInTLB(int PageNum, PageUnit[] TLB) {
		for (int i = 0; i < TLB.length; i++) {
			if (TLB[i].PageFrameNum == PageNum) {
				return true;
			}
		}
		return false;
	}

	// 查找一级页表
	public boolean isInPageTable(int PageNum, PageUnit[] PageTable) {
		for (int i = 0; i < PageTable.length; i++) {
			if (PageTable[i].PageFrameNum == PageNum) {
				return true;
			}
		}
		return false;
	}

	// 判断是否在内存 返回页号或者产生缺页中断
	public int isInMemory(int add, Memory memory) {
		int PageNum = add >> 9; // 低九位是页内偏移 得到页号
		// 查找是否在快表 在就返回1
		if (isInTLB(PageNum, memory.TLB))
			return 1;
		// 否则查找是否在页表在就返回2
		else if (isInPageTable(PageNum, memory.PageTable))
			return 2;
		// 都不在就返回-1 表示缺页中断
		else
			return -1;
	}

}
