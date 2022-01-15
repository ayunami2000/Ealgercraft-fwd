package me.ayunami2000.eaglercraft;

import java.net.URI;
import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketLocal extends WebSocketClient {
    SocketChannel socketChannel;

    public WebSocketLocal(URI serverURI, SocketChannel sc) {
        super(serverURI);
        socketChannel=sc;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
    }

    @Override
    public void onMessage(String s) {
        //lol
        //System.out.println("received: " + s);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        //System.out.println(message);
        socketChannel.write(Unpooled.wrappedBuffer(message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
        socketChannel.close();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}