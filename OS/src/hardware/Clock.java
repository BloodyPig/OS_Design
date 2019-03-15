package hardware;

public class Clock implements Runnable {

	public long TimeNow;

	// 时钟线程的主体就是不断的在流逝时间 同时作业的进程在调度
	@Override
	public void run() {
		while (true) {
			TimeNow += 10;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.out.println("系统当前时间 " + TimeNow);
		}
	}

	public String getTime() {
		return Long.toString(TimeNow);
	}

}
