package me.redstoner2019;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = false;
    private boolean recievingFile = false;
    private String filename;

    public ClientHandler(java.net.Socket socket) {
        try{
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.isConnected = true;
            Thread listener = new Thread(() -> {
                while (isConnected){
                    try {
                        JSONObject packet = new JSONObject(String.valueOf(in.readObject()));
                        if(packet.has("header")) {
                            switch (packet.getString("header")) {
                                case "upload" -> {
                                    filename = UUID.randomUUID().toString() + "_" + packet.getString("filename");
                                    FileServer.addFile(filename,packet.getString("filename"));
                                    JSONObject data = new JSONObject();
                                    data.put("filename",filename);
                                    data.put("header","setup done");
                                    out.writeObject(data.toString());

                                    File output = new File("files/"+filename);
                                    if(!output.exists()){
                                        output.getParentFile().mkdirs();
                                        output.createNewFile();
                                    }

                                    FileOutputStream fos = new FileOutputStream(output);

                                    byte[] bytes = in.readNBytes(1024 * 16);
                                    while (bytes.length > 0) {
                                        fos.write(bytes);
                                        bytes = in.readNBytes(1024 * 16);
                                        System.out.println(bytes.length + " bytes read");
                                    }
                                    fos.flush();
                                    fos.close();
                                }
                                default -> {
                                    System.err.println("Not implemented.");
                                    System.err.println(packet);
                                }
                            }
                        } else {
                            System.err.println("Malformed packet:");
                            System.err.println(packet);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        //EOF, terminate
                        break;
                    }
                }
            });
            listener.start();
        } catch (Exception e) {
            System.err.println("Failed to create Client Handler");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
