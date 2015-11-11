package el.zk;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import el.app.AppServer;
import el.app.MainApp;

public class Commander {
	final static String COMMAND_DIR = "/command/";
	final static String COMMAND_OUTPUT = "/command_out/";

	final String COMMAND_NODE;
	final String COMMAND_OUTPUT_NODE;

	ZKClient zkclient;

	private MainApp app;
	private NodeCache nodeCache;

	public Commander(ZKClient zkclient, MainApp app, String serverName) {
		this.zkclient = zkclient;
		this.app = app;

		this.COMMAND_NODE = COMMAND_DIR + serverName;
		this.COMMAND_OUTPUT_NODE = COMMAND_OUTPUT + serverName;

		this.nodeCache = new NodeCache(zkclient.getClient(), COMMAND_NODE);
	}

	public CuratorFramework getClient() {
		return this.zkclient.getClient();
	}

	public void run(){
		try{
			runMain();
		}catch(Exception ex){
			// TODO: Log
			// Shutdown
		}
	}

	public void runMain() throws Exception {
		this.nodeCache.start();
		this.zkclient.addPreClose(new Runnable() {

			@Override
			public void run() {
				if (null != Commander.this.nodeCache) {
					try {
						Commander.this.nodeCache.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		this.nodeCache.getListenable().addListener(new NodeCacheListener() {

			@Override
			public void nodeChanged() throws Exception {
				// Make sure only one processed at the same time.
				synchronized(Commander.this){
					if (app.isRunning() == true) {
						monitor();
					}
				}
			}
		});
	}

	public void monitor() throws Exception{
		try {
			// If no such a node, Exception will be thrown

			byte[] data = this.getClient().getData().forPath(COMMAND_NODE);
			String cmd = data == null ? "" : new String(data);

			String result = process(cmd);
			doAction(result);

		} catch (InterruptedException iex) {
			doAction("Will Shutdown.");
			// Probably we could consider shutdown node here.
		}
	}

	public void doAction(String result) throws Exception {
		this.getClient().inTransaction().create().forPath(COMMAND_OUTPUT_NODE, result.getBytes()).and().delete()
				.forPath(COMMAND_NODE).and().commit();

	}

	public String process(String cmd) throws InterruptedException {
		switch (cmd.trim()) {
		case "shutdown":
		case "kill":
			// this.app.shutdown();
			// return "Kill Issued.";
			throw new InterruptedException();
		default:
			return "Command unsupported.";
		}
	}

}
