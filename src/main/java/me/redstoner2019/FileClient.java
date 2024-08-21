package me.redstoner2019;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class FileClient {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) {
        try{
            String uuid = upload(new File("C:\\Users\\Redstoner_2019\\Downloads\\logo.gif"));
            download(uuid);
            /*socket = new Socket("localhost", 20);
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

            Thread.sleep(10000);*/
        } catch (Exception e) {
            System.err.println("Failed to create FileClient");
        }
    }

    public static String upload(File file){
        try {
            Socket socket = new Socket("localhost", 20);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            JSONObject request = new JSONObject();
            request.put("header", "upload");
            request.put("filename", file.getName());
            out.writeObject(request.toString());

            JSONObject response = new JSONObject(String.valueOf(in.readObject()));

            FileInputStream fis = new FileInputStream(file);
            out.write(fis.readAllBytes());

            out.flush();
            out.close();
            socket.close();

            return response.getString("filename");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void download(String uuid){
        try {
            Socket socket = new Socket("localhost", 20);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            JSONObject request = new JSONObject();
            request.put("header", "download");
            request.put("uuid", uuid);
            out.writeObject(request.toString());

            File file = new File("downloads/"+uuid);

            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = in.readNBytes(1024);
            while (data.length > 0) {
                System.out.println(data.length);
                fos.write(data);
                data = in.readNBytes(1024);
            }

            fos.flush();
            fos.close();

            out.close();
            in.close();

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
