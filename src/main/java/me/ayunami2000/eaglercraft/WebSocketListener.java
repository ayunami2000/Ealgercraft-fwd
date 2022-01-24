package me.ayunami2000.eaglercraft;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebSocketListener extends WebSocketServer {
    private InetSocketAddress bungeeProxy;

    Pattern p = Pattern.compile("^"
            + "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
            + "|"
            + "localhost" // localhost
            + "|"
            + "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
            + "(:"
            + "[0-9]{1,5})?$"); // Port

    public WebSocketListener(InetSocketAddress origSock, InetSocketAddress sock) {
        super(sock);
        this.setTcpNoDelay(true);
        this.setConnectionLostTimeout(5);
        this.start();
        this.bungeeProxy = origSock;
    }

    @Override
    public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
        if(arg0.getAttachment() != null) {
            ((WebSocketProxy)arg0.getAttachment()).killConnection();
        }
        System.out.println("websocket closed - " + arg0.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket arg0, Exception arg1) {
        arg1.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket arg0, String arg1) {
    }

    @Override
    public void onMessage(WebSocket arg0, ByteBuffer arg1) {
        if(arg0.getAttachment() != null) {
            ((WebSocketProxy)arg0.getAttachment()).sendPacket(arg1);
        }
    }

    @Override
    public void onOpen(WebSocket arg0, ClientHandshake arg1) {
        System.out.println("websocket opened - " + arg0.getRemoteSocketAddress());
        InetSocketAddress theSock = bungeeProxy;
        if(theSock==null){
            String[] parts=arg1.getResourceDescriptor().split("/");
            if(parts.length>0) {
                String path = parts[parts.length - 1];
                if (p.matcher(path).matches()) {
                    String ip = path;
                    String port = "25565";
                    if (path.contains(":")) {
                        String[] ipPort = path.split(":", 2);
                        ip = ipPort[0];
                        port = ipPort[1];
                    }
                    theSock = new InetSocketAddress(ip, Integer.parseInt(port));
                    if (theSock.getAddress().isSiteLocalAddress() || theSock.getAddress().isLoopbackAddress())
                        theSock = null;
                }
            }
            if(theSock==null){
                arg0.close();
                return;
            }
        }
        WebSocketProxy proxyObj = new WebSocketProxy(arg0, theSock);
        arg0.setAttachment(proxyObj);
        if(!proxyObj.connect()) {
            arg0.close();
        }
    }

    @Override
    public void onStart() {

    }

}