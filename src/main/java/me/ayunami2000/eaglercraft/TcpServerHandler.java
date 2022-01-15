package me.ayunami2000.eaglercraft;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.MessageList;
import io.netty.channel.socket.SocketChannel;

import javax.net.ssl.SSLSocketFactory;

@ChannelHandler.Sharable
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    WebSocketLocal wsl = null;
    SocketChannel socketChannel;

    public TcpServerHandler(SocketChannel sc){
        socketChannel=sc;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        wsl=new WebSocketLocal(Main.c,socketChannel);
        wsl.setTcpNoDelay(true);
        if(Main.c.getScheme().toLowerCase().startsWith("wss"))wsl.setSocketFactory(SSLSocketFactory.getDefault());
        wsl.connectBlocking();
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        wsl.close();
        wsl=null;
        ctx.fireChannelUnregistered();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageList<Object> msgs) throws Exception {
        for (Object msg : msgs) {
            wsl.send(((ByteBuf)msg).nioBuffer());
        }
        ctx.fireMessageReceived(msgs);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}