package software;

//ҳ������
public class PageUnit {
	public int PageNum; // ҳ�ţ���0��ʼ��� 0-32
	public int PageFrameNum;// �ڴ�ҳ��(ҳ���)��ҳ����ҳ���С���
	public boolean Stay; // פ����־λ
	public int Use; // ����λ
	public boolean Modify; // �޸�λ

	public PageUnit() {
		PageNum = 0;
		PageFrameNum = 0;
		Stay = false;
		Use = 0;
		Modify = false;
	}

}
