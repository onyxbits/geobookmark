package de.onyxbits.geobookmark;

public class TimeOut implements Runnable {

	private long sleepTime;
	private BookmarkService service;

	public TimeOut(BookmarkService service, long sleepTime) {
		this.sleepTime = sleepTime;
		this.service = service;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(sleepTime);
			service.timeout();
		}
		catch (InterruptedException e) {
			
		}
	}
}
