package el.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class MyNioServerSocketChannel extends NioServerSocketChannel{
	public static int connectionNum = 0;

	@Override
	protected int doReadMessages(List<Object> buf) throws Exception {

		SocketChannel ch = javaChannel().accept();

		try{
			InetSocketAddress add = (InetSocketAddress)ch.getRemoteAddress();
			//System.out.println(add.getHostName());// + ":" + add.getPort());
		}catch(Exception e)
		{
			System.out.println("IOException");
		}

        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        } catch (Throwable t) {
            //logger.warn("Failed to create a new channel from an accepted socket.", t);

            try {
                ch.close();
            } catch (Throwable t2) {
                //logger.warn("Failed to close a socket.", t2);
            }
        }

        return 0;
	}


}
