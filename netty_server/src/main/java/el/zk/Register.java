package el.zk;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

public class Register {

	final static String NODE_DIR = "/node";

	ZKClient zkclient;


	public Register(ZKClient zkclient) {
		this.zkclient = zkclient;
	}

	public CuratorFramework getClient() {
		return this.zkclient.getClient();
	}

	public void register(String serverName) {
		try {
			Stat stat = this.getClient().checkExists().forPath(NODE_DIR);
			if (null == stat) {
				this.getClient().create().forPath(NODE_DIR);
			}
			this.getClient().create().forPath(NODE_DIR + "/" + serverName);
		} catch (Exception e) {
			// log
		}

	}

	public List<String> getAllNodes() throws Exception{
		return this.getClient().getChildren().forPath(NODE_DIR);
	}

}
