package me.redstoner2019;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FileClient {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) {
        try{
            socket = new Socket("localhost", 20);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            File file = new File("C:\\Users\\Redstoner_2019\\Downloads\\logo.gif");

            JSONObject request = new JSONObject();
            request.put("header", "upload");
            request.put("filename", file.getName());
            out.writeObject(request.toString());

            System.out.println(new JSONObject(String.valueOf(in.readObject())));

            FileInputStream fis = new FileInputStream(file);
            out.write(fis.readAllBytes());

            out.flush();
            out.close();

            Thread.sleep(10000);
        } catch (Exception e) {
            System.err.println("Failed to create FileClient");
        }
    }
}
