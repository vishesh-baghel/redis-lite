package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
            if (serverSocket.isBound()) {
                System.out.println("Redis server is listening on port: 6379, now you can send commands from the redis client.");
            }
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (clientSocket.isConnected()) {
                    executor.submit(() -> {
                    try {
                        handleIncomingConnection(clientSocket);
                    } catch (IOException e) {
                        System.out.println("IO exception in the incoming connection handler");
                    }
                    });
                    executor.shutdown();
                } else {
                    System.out.println("the client is not connected");
                }
            }

        } catch (IOException e) {
            System.out.println("IO exception in the main method: " + e.getMessage());
        }
    }

    private static void handleIncomingConnection(Socket clientSocket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String command = reader.readLine(); // Read client command
            List<String> args = parseArguments(command);
            String response = processCommand(args);

            writer.write(response + "\n"); // Send response back to client
            writer.flush();
        } catch (IOException e) {
            System.out.println("IO exception in incoming connection handler");
        } finally {
            clientSocket.close();
        }
    }

    private static List<String> parseArguments(String command) {
        return List.of(command.split(" "));
    }

    private static String processCommand(List<String> args) {
        String command = args.get(0);
        System.out.println("[" + Thread.currentThread() + "] Received args: " + args);

        if ("GET".equalsIgnoreCase(command)) {
            return handleGet(args.get(1));
        } else if ("SET".equalsIgnoreCase(command)){
            if (args.size() > 3) {
                return handleSetWithExpiration(args.get(1), args.get(2), Integer.parseInt(args.get(3)));
            } else {
                return handleSet(args.get(1), args.get(2));
            }
        } else if ("ECHO".equalsIgnoreCase(command)) {
            return handleEcho(args.get(1));
        } else if ("PING".equalsIgnoreCase(command)) {
            return handlePing();
        } else {
            return handleUnknownCommand(command);
        }
    }

    private static String handleGet(String key) {
       boolean isExpired = keyToExpirationTime.get(key) != null && keyToExpirationTime.get(key).isBefore(Instant.now());
       String response = storage.getOrDefault(key, "-1");
       if (isExpired || response.equalsIgnoreCase("-1")) {
           return "Either the key is expired or the value for this key doesn't exist";
       } else {
           return response;
       }
    }

    private static String handleSetWithExpiration(String key, String value, int expireAfterMs) {
       keyToExpirationTime.put(key, Instant.now().plus(expireAfterMs, ChronoUnit.MILLIS));
       return handleSet(key, value);
    }

    private static String handleSet(String key, String value) {
        storage.put(key, value);
        System.out.println("size of storage: " + storage.size());
        return "Added to the storage";
    }

    private static String handleEcho(String command) {
        System.out.println("[" + Thread.currentThread() + "] Return: " + command);
        return "+" + command;
    }

    private static String handlePing() {
        System.out.println("[" + Thread.currentThread() + "] Return +PONG");
        return "+PONG";
    }

    private static String handleUnknownCommand(String command) {
        String response = "Unknown command: " + command;
        System.out.println("[" + Thread.currentThread() + "] " + response);
        return response;
    }
}