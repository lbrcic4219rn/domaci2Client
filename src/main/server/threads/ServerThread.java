package main.server.threads;

import main.server.ChatServer;
import main.server.messages.Message;
import main.server.messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerThread implements Runnable{

    private final Socket socket;
    private final ChatServer server;

    public ServerThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        String username = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)){

            out.println("Enter username: ");

            while (true) {
                username = in.readLine();
                if (username == null) return;

                Socket existing = server.users.putIfAbsent(username, socket);
                if (existing == null) {
                    server.writers.put(username, out);

                    server.messages.put(new Message(
                            "User: " + username + " connected.",
                            MessageType.CONNECT,
                            username));

                    out.println("Welcome: " + username);
                    server.users.put(username, socket);
                    server.history.forEach(out::println);
                    break;
                } else {
                    out.println("Username taken, try again: ");
                }
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            while (true) {
                String msg = in.readLine();
                if (msg == null) break;
                msg = sanitize(msg);
                String full = LocalDateTime.now().format(fmt) + " - " + username + " : " + msg;

                server.messages.put(new Message(full, MessageType.MESSAGE, username));

                if (server.history.size() >= 100) {
                    server.history.removeFirst();
                }
                server.history.add(full);
            }
        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            if (username != null) {
                server.users.remove(username);
                server.writers.remove(username);

                try {
                    server.messages.put(new Message(username + " disconnected", MessageType.DISCONNECT, username));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private String sanitize(String msg) {
        for (String w : server.forbiddenWords) {
            String replacement = w.charAt(0) + "*".repeat(Math.max(0, w.length() - 2)) + w.charAt(w.length() - 1);
            msg = msg.replace(w, replacement);
        }
        return msg;
    }
}
