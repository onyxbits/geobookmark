package de.onyxbits.geobookmark;

import android.os.Handler;

public class TimeOut implements Runnable {

	private long sleepTime;
	private BookmarkService service;
	private Handler handler;

	public TimeOut(BookmarkService service, long sleepTime) {
		this.sleepTime = sleepTime;
		this.service = service;
		this.handler = new Handler();
	}

	private TimeOut(BookmarkService service) {
		this.service=service;
	}

	@Override
	public void run() {
		if (handler == null) {
			service.timeout();
		}
		else {
			try {
				Thread.sleep(sleepTime);
				handler.post(new TimeOut(service));
			}
			catch (InterruptedException e) {

			}
		}
	}
}
