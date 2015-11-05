package el.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainApp implements Runnable, ShutdownHooks {

	volatile boolean keepRunning = true;
	volatile Thread sleepingThread = null;

	List<Runnable> jobs = Collections.synchronizedList(new ArrayList<>());
	List<Runnable> cleanUpJobs = Collections.synchronizedList(new ArrayList<>());

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void run() {
		work();

		while (keepRunning) {
			System.out.println("I am Running.");
			sleepingThread = Thread.currentThread();
			sleep(3_000);
		}
	}

	public void work(){
		for (Runnable job : jobs){
			job.run();
		}
	}

	public void cleanup(){
		for (Runnable job : cleanUpJobs){
			job.run();
		}
	}

	@Override
	public void closeInside() {
		if (sleepingThread != null)
			if (sleepingThread != Thread.currentThread()) {
				this.sleepingThread.interrupt();
			}
		this.keepRunning = false;
	}

}
