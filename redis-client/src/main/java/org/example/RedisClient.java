package org.example;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class RedisClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 6379);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

             BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
             if (socket.isConnected()) {
                 System.out.println("Connected to the redis server");
             }
             while (true) {
                System.out.print("Enter command: ");
                String command = userInputReader.readLine();

                writer.write(command + "\n"); // Send command to server
                writer.flush();

                String response = reader.readLine(); // Receive response from server
                System.out.println("Response from server: " + response);
             }
        } catch (ConnectException connectException) {
            System.out.println(connectException.getMessage() + ": The redis server is down. please start the redis server before starting the redis client.");
        }
    }
}