package me.redstoner2019;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

public class WebFileServer extends WebSocketServer {
    public WebFileServer(int port) {
        super(new InetSocketAddress(port));
    }

    public boolean recievingFile = false;
    public String filename = "";

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection opened: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + " for reason: " + reason);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println(s);
        if(!recievingFile){
            JSONObject data = new JSONObject(s);
            filename = data.getString("filename");
            recievingFile = true;
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer s) {
        if(recievingFile){
            try {
                String uuid = UUID.randomUUID().toString();
                FileOutputStream fos = new FileOutputStream("files/"+uuid);
                fos.write(s.array());
                fos.close();
                FileServer.addFile(uuid, filename);

                JSONObject data = new JSONObject();
                data.put("uuid",uuid);
                data.put("header","upload-complete");
                webSocket.send(data.toString());
            } catch (IOException e) {
                JSONObject data = new JSONObject();
                data.put("header","upload-failed");
                webSocket.send(data.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {

    }
}
