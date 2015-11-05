package el.zk;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;

public class ZKClientDemo {

	private static CuratorFramework createClient(String connectionString){
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
		return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
	}

	private static final String CONNECTION = "slc08ymk.us.oracle.com:2181";
	private static CuratorFramework client = createClient(CONNECTION);

	private static final String PATH = "/crud";

	public static void main(String[] args) {
		try {
			client.start();

			if (client.checkExists().forPath(PATH) == null)
				client.create().forPath(PATH, "I love messi".getBytes());

			byte[] bs = client.getData().forPath(PATH);
			System.out.println("Node created with Data:" + new String(bs));

			client.setData().forPath(PATH, "I love football".getBytes());

			Thread.sleep(1000);
			CountDownLatch cdl = new CountDownLatch(1);
//			byte[] bs2 = client.getData().watched().inBackground(new BackgroundCallback() {
//
//				@Override
//				public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
//					System.out.println("Back Ground.");
//					System.out.println(new String(event.getData()));
//					cdl.countDown();
//				}
//			}).forPath(PATH);

			NodeCache nc = new NodeCache(client, PATH+"/e");
			nc.start();
			nc.getListenable().addListener(new NodeCacheListener() {

				@Override
				public void nodeChanged() throws Exception {
					try{
					System.out.println(new String(client.getData().forPath(PATH+"/d")));
					}finally{
					cdl.countDown();
					}
				}
			});

			cdl.await();
			nc.close();

//			System.out.println("Modify Data is:" + new String(bs2 != null ? bs2 : /*new byte[0]*/ "NULL".getBytes()));

//			client.delete().forPath(PATH);
			Stat stat = client.checkExists().forPath(PATH);

			System.out.println(stat);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}
}
