package me.ayunami2000.eaglercraft;

import io.netty.channel.socket.SocketChannel;

import javax.net.ssl.SSLSocketFactory;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static URI c = null;

    public static void main(String[] args) throws URISyntaxException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
        if(args[0].equalsIgnoreCase("client")){
            c = new URI(args[1]);
            (new Thread(() -> {
                try {
                    WebSocketProxyLocal.start(Integer.parseInt(args[2]));
                } catch (InterruptedException ex) {}
            })).start();
        }else if(args[0].equalsIgnoreCase("server")) {
            InetSocketAddress origSock = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
            InetSocketAddress sock = new InetSocketAddress("127.0.0.1", Integer.parseInt(args[3]));
            /*
            int port = 0;
            try {
                ServerSocket s = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"));
                sock = new InetSocketAddress("127.0.0.1", s.getLocalPort());
                s.close();
            } catch(IOException e) {
                port = (int) (System.nanoTime() % 64000L + 1025L);
                sock = new InetSocketAddress("127.0.0.1", port);
            }
            */
            try {
                new WebSocketListener(origSock, sock);
                System.out.println("Listening websockets on " + sock);
            } catch (Throwable t) {
                System.out.println("Could not bind websocket listener to host " + sock);
                t.printStackTrace();
            }
        }else{
            System.out.println("Usage: ... <client|server> <<ip> <port> <localport>|<websocketurl> <localport>>");
        }
    }
}
