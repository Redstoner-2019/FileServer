package me.redstoner2019;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

    public static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(20);
        } catch (IOException e) {
            System.err.println("Could not start Server on port: 20");
            System.exit(1);
        }

        while (serverSocket.isBound()){
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
            } catch (IOException e) {
                System.err.println("Connection failed");
            }
        }
    }

    public static void addFile(String name, String originalName){
        File cache = new File("cache.json");
        if(!cache.exists()){
            try {
                cache.createNewFile();
                Util.writeStringToFile(new JSONObject(),cache);
            } catch (IOException e) {
                System.err.println("Failed to create Cache.");
                return;
            }
        }
        try {
            JSONObject cacheObject = new JSONObject(Util.readFile(cache));
            cacheObject.put(name,originalName);
            Util.writeStringToFile(cacheObject,cache);
        } catch (IOException e) {
            System.err.println("Failed to update Cache.");
        }
    }
}
