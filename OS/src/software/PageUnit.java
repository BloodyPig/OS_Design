package software;

//页表项类
public class PageUnit {
	public int PageNum; // 页号，从0开始编号 0-32
	public int PageFrameNum;// 内存页号(页框号)，页框与页面大小相等
	public boolean Stay; // 驻留标志位
	public int Use; // 引用位
	public boolean Modify; // 修改位

	public PageUnit() {
		PageNum = 0;
		PageFrameNum = 0;
		Stay = false;
		Use = 0;
		Modify = false;
	}

}
