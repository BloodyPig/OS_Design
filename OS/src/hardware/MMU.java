package hardware;

import software.PageUnit;

//��ַת���߼���Ԫ
public class MMU {

	// ���ҿ��
	public boolean isInTLB(int PageNum, PageUnit[] TLB) {
		for (int i = 0; i < TLB.length; i++) {
			if (TLB[i].PageFrameNum == PageNum) {
				return true;
			}
		}
		return false;
	}

	// ����һ��ҳ��
	public boolean isInPageTable(int PageNum, PageUnit[] PageTable) {
		for (int i = 0; i < PageTable.length; i++) {
			if (PageTable[i].PageFrameNum == PageNum) {
				return true;
			}
		}
		return false;
	}

	// �ж��Ƿ����ڴ� ����ҳ�Ż��߲���ȱҳ�ж�
	public int isInMemory(int add, Memory memory) {
		int PageNum = add >> 9; // �;�λ��ҳ��ƫ�� �õ�ҳ��
		// �����Ƿ��ڿ�� �ھͷ���1
		if (isInTLB(PageNum, memory.TLB))
			return 1;
		// ��������Ƿ���ҳ���ھͷ���2
		else if (isInPageTable(PageNum, memory.PageTable))
			return 2;
		// �����ھͷ���-1 ��ʾȱҳ�ж�
		else
			return -1;
	}

}
