package el.app;

public class AppServer implements Runnable{

	public String serverName;

	MainApp app;

	public AppServer() {
		app = new MainApp();
	}

	public String getName(){
		return serverName;
	}

	public void run(){
		app.jobs.add(()->{

		});
	}

	public void shutdown(){
		this.app.close();
	}


	public boolean isRunning(){
		return this.app.keepRunning;
	}

	public static void main(String[] args) {

	}
}
