package org.example;

import java.io.*;
import java.net.Socket;

public class RedisClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 6379);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String command = "GET key"; // Redis-like command
            writer.write(command + "\n"); // Send command to server
            writer.flush();

            String response = reader.readLine(); // Receive response from server
            System.out.println("Response from server: " + response);
        }
    }
}