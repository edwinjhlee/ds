package el.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.curator.framework.CuratorFramework;

import el.zk.ZKClient;

public class CmdClient implements Runnable {

	ZKClient zkclient;

	private boolean isRunning;

	public boolean isRunning(){
		return isRunning;
	}

	public CmdClient(ZKClient zkclinet) {
		this.zkclient = zkclinet;
	}

	public CmdClient() {

	}

	public CuratorFramework getClient() {
		return this.zkclient.getClient();
	}

	@Override
	public void run() {

		isRunning = true;

		System.out.println("Client Ready.");

		try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
			while (isRunning) {
				System.out.print(">>> ");
				String cmd = r.readLine();

				final String innerResult = processInnerCommand(cmd);

				if (innerResult != null){
					System.out.println("==> " + innerResult);
					continue;
				}

				String s = process(cmd);
				System.out.println("==> " + s);
			}

		} catch (IOException ex) {

		}

	}

	private String processInnerCommand(String cmd) {
		switch (cmd.trim()) {
		case "quit":
		case "bye":
			isRunning = false;
			return "Client. Close. Goodbye";
		default:
			return null;
		}
	}

	private String process(String cmd) {
		int idx = cmd.indexOf(':');

		String node = null;

		if (idx != -1) {
			node = cmd.substring(0, idx);
			cmd = cmd.substring(idx + 1);
		}

		return cmd;
	}

	public static void main(String[] args) {
		new CmdClient().run();
	}

}
