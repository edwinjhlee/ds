package el.netty;

/*import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;*/
import java.io.ByteArrayOutputStream;

import io.netty.channel.*;
import io.netty.buffer.*;
import io.netty.util.*;
/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter  { // (1)

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Registered channel");
        ctx.fireChannelRegistered();
    }

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
    	ByteBuf in = (ByteBuf) msg;
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (in.isReadable()) { // (1)
                 byte readByte = in.readByte();
                 baos.write(readByte);
                //System.out.flush();

                ctx.flush();
            }
            System.out.println("Msg Read: " + baos.toString());
        } finally {
        	//System.out.println("finish!");
            ReferenceCountUtil.release(msg); // (2)
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}