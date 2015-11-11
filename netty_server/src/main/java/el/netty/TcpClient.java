package el.netty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpClient {
	public static void main(String[] args) throws IOException, InterruptedException {
		 String folder = "D:/post";
		 ExecutorService pool = Executors.newCachedThreadPool();
		for(int i = 0; i < 1;i++)
		{
			final String filePath = folder + "/" + i;
			pool.submit(new Thread(){
				@Override
				public void run()
				{
					try {
						new TcpClient().send(filePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}

		pool.shutdown();
	}

	public void send(String filePath) throws UnknownHostException, IOException
	{
		String server = "127.0.0.1";
		int servPort = 19005;
		Socket socket = new Socket(server, servPort);

		System.out.println(socket.isConnected());
		OutputStream out = socket.getOutputStream();
//		InputStream in = new FileInputStream(new File(filePath));

//		byte []buf = new byte[2000];
		out.write("hello server".getBytes());

		socket.close();
	}
}
