package el.app;

import el.zk.Commander;
import el.zk.Register;
import el.zk.ZKClient;

public class AppServer {

	public String serverName;

	MainApp app;
	ZKClient zkclient;

	public AppServer() {
		app = new MainApp();
		zkclient = new ZKClient();
	}

	public String getName(){
		return serverName;
	}

	public static void run(String serverName){

		MainApp app = null;

		try {

			app = new MainApp();

			final MainApp _app = app;

			ZKClient zkclient = new ZKClient();

			zkclient.register(serverName);

			app.jobs.add(()->{
				new Thread(() -> new Commander(zkclient, _app, serverName).run()).start();

				zkclient.buildNodeEnrollNotifier( e -> {
					System.out.println(e.getPath());
				} ).run();
			});

			app.run();

		} catch (Exception ex){
			if (null != app)
				app.close();
		}
	}

	public boolean isRunning(){
		return this.app.keepRunning;
	}

	public static void main(String[] args) {

	}
}
