package el.zk;

import java.io.Closeable;
import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

import el.app.ShutdownHooks;

public class ZKClient implements ShutdownHooks{

	private static CuratorFramework createClient(String connectionString){
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
		return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
	}

	final String zkInitialConnection;
	CuratorFramework client;

	private static final String PATH = "/crud";

	public ZKClient(){
		this("slc08ymk.us.oracle.com:2181");
	}

	public ZKClient(String connection){
		this.zkInitialConnection = connection;
		this.client = createClient(connection);
		client.start();

		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				ZKClient.this.close();
			}
		});
	}

	public CuratorFramework getClient(){
		return this.client;
	}

	public void main(String[] args) {
		try {
			client.start();

			if (client.checkExists().forPath(PATH) == null)
				client.create().forPath(PATH, "I love messi".getBytes());

			byte[] bs = client.getData().forPath(PATH);
			System.out.println("Node created with Data:" + new String(bs));

			client.setData().forPath(PATH, "I love football".getBytes());

			byte[] bs2 = client.getData().watched().inBackground(new BackgroundCallback() {

				@Override
				public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
					System.out.println("Back Ground.");
					System.out.println(new String(event.getData()));
				}
			}).forPath(PATH);
			System.out.println("Modify Data is:" + new String(bs2 != null ? bs2 : /*new byte[0]*/ "NULL".getBytes()));

//			client.delete().forPath(PATH);
			Stat stat = client.checkExists().forPath(PATH);

			System.out.println(stat);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}

	public void closeInside(){
		if (client != null){
			CloseableUtils.closeQuietly(client);
			client = null;
		}
	}

}
