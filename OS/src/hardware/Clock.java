package hardware;

public class Clock implements Runnable {

	public long TimeNow;

	// ʱ���̵߳�������ǲ��ϵ�������ʱ�� ͬʱ��ҵ�Ľ����ڵ���
	@Override
	public void run() {
		while (true) {
			TimeNow += 10;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.out.println("ϵͳ��ǰʱ�� " + TimeNow);
		}
	}

	public String getTime() {
		return Long.toString(TimeNow);
	}

}
