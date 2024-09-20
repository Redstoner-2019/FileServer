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
import java.util.HashMap;
import java.util.UUID;

public class WebFileServer extends WebSocketServer {
    public WebFileServer(int port) {
        super(new InetSocketAddress(port));
    }

    private HashMap<String, Boolean> recievingFiles = new HashMap<>();
    private HashMap<String, String> filenames = new HashMap<>();

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
        if(!recievingFiles.getOrDefault(webSocket.getRemoteSocketAddress().toString(),false)){
            JSONObject data = new JSONObject(s);
            filenames.put(webSocket.getRemoteSocketAddress().toString(),data.getString("filename"));
            recievingFiles.put(webSocket.getRemoteSocketAddress().toString(),true);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer s) {
        if(recievingFiles.getOrDefault(webSocket.getRemoteSocketAddress().toString(),false)){
            try {
                String uuid = UUID.randomUUID().toString();
                FileOutputStream fos = new FileOutputStream("files/"+uuid);
                fos.write(s.array());
                fos.close();
                FileServer.addFile(uuid, filenames.get(webSocket.getRemoteSocketAddress().toString()));

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
        System.out.println("FileServer started");
    }
}
