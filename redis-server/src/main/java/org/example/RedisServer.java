package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {
    private static final Map<String, String> storage = new ConcurrentHashMap<>();
    private static final Map<String, Instant> keyToExpirationTime = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cpuCores);
        try(ServerSocket serverSocket = new ServerSocket(6379)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> {
                    try {
                        handleIncomingConnection(clientSocket);
                    } catch (IOException e) {
                        System.out.println("IO exception in the incoming connection handler");
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleIncomingConnection(Socket clientSocket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String command = reader.readLine(); // Read client command
            String response = processCommand(command); // Process the command

            writer.write(response + "\n"); // Send response back to client
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            clientSocket.close();
        }
    }

    private static String processCommand(String command) {
        if ("GET key".equals(command)) {
            return "Value for key";
        } else {
            return "Unknown command";
        }
    }
}