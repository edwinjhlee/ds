package el.zk;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import el.app.AppServer;

public class Commander {
	final static String COMMAND_DIR = "/command/";
	final static String COMMAND_OUTPUT = "/command_out/";

	final String COMMAND_NODE;
	final String COMMAND_OUTPUT_NODE;

	ZKClient zkclient;

	private AppServer app;
	private NodeCache nodeCache;

	public Commander(ZKClient zkclient, AppServer app) {
		this.zkclient = zkclient;
		this.app = app;

		this.COMMAND_NODE = COMMAND_DIR + this.app.getName();
		this.COMMAND_OUTPUT_NODE = COMMAND_OUTPUT + this.app.getName();

		this.nodeCache = new NodeCache(zkclient.getClient(), COMMAND_NODE);
	}

	public CuratorFramework getClient() {
		return this.zkclient.getClient();
	}

	public void run() throws Exception{
		this.nodeCache.start();
		this.zkclient.addPreClose(new Runnable() {

			@Override
			public void run() {
				if (null != Commander.this.nodeCache){
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
				monitor();
			}
		});
	}

	public void monitor() {
		try {
			// If no such a node, Exception will be thrown

			byte[] data = this.getClient().getData().forPath(COMMAND_NODE);

			String cmd = data == null ? "" : new String(data);

			String result = process(cmd);

			this.getClient().inTransaction()
				.create().forPath(COMMAND_OUTPUT_NODE, result.getBytes())
				.and()
				.delete().forPath(COMMAND_NODE).and()
				.commit();

		} catch (Exception e) {
			// log
		}
	}

	public String process(String cmd) {
		switch (cmd.trim()) {
		case "shutdown":
		case "kill":
			this.app.shutdown();
			return "Kill Issued.";
		default:
			return "Command unsupported.";
		}
	}

}
