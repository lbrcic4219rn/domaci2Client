package main.server.threads;

import main.server.ChatServer;
import main.server.messages.Message;
import main.server.messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ServerThread implements Runnable {

    private static final Logger LOG = Logger.getLogger(ServerThread.class.getName());
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final int MAX_HISTORY = 100;

    private final Socket socket;
    private final ChatServer server;

    public ServerThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                var socket = this.socket;
                var in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            String username = negotiateUsername(in, out);
            if (username == null) return;

            try {
                handleMessages(username, in);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warning("Interrupted while disconnecting " + username);
            } finally {
                disconnect(username);
            }
        } catch (IOException e) {
            LOG.warning("I/O error for client " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warning("Thread interrupted for client " + socket.getRemoteSocketAddress());
        }
    }

    private String negotiateUsername(BufferedReader in, PrintWriter out) throws IOException, InterruptedException {
        out.println("Enter username:");

        String username;
        while (true) {
            username = in.readLine();
            if (username == null) return null;

            username = username.trim();
            if (username.isEmpty()) {
                out.println("Username cannot be empty, try again:");
                continue;
            }

            if (server.users.putIfAbsent(username, socket) == null) {
                server.writers.put(username, out);
                server.messages.put(new Message(
                        "User " + username + " connected.",
                        MessageType.CONNECT,
                        username));
                out.println("Welcome, " + username + "!");
                server.history.forEach(out::println);
                return username;
            }

            out.println("Username taken, try again:");
        }
    }

    private void handleMessages(String username, BufferedReader in)
            throws IOException, InterruptedException {

        String raw;
        while ((raw = in.readLine()) != null) {
            String formatted = format(username, sanitize(raw));
            server.messages.put(new Message(formatted, MessageType.MESSAGE, username));
            addToHistory(formatted);
        }
    }

    private String format(String username, String msg) {
        return TIMESTAMP_FMT.format(LocalDateTime.now()) + " - " + username + " : " + msg;
    }

    private void addToHistory(String line) {
        if (server.history.size() >= MAX_HISTORY) {
            server.history.removeFirst();
        }
        server.history.add(line);
    }

    private void disconnect(String username) throws InterruptedException {
        server.users.remove(username);
        server.writers.remove(username);
        server.messages.put(new Message(
                username + " disconnected.",
                MessageType.DISCONNECT,
                username));
    }

    private String sanitize(String msg) {
        for (String word : ChatServer.FORBIDDEN_WORDS) {
            if (word == null || word.isEmpty()) continue;
            msg = msg.replaceAll(
                    "(?i)\\b" + Pattern.quote(word) + "\\b",
                    buildCensor(word));
        }
        return msg;
    }

    private static String buildCensor(String word) {
        if (word.length() == 1) return "*";
        return word.charAt(0)
                + "*".repeat(Math.max(0, word.length() - 2))
                + word.charAt(word.length() - 1);
    }
}