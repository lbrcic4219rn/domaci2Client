package main.server;

import main.server.messages.Message;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {
    public final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    public final ConcurrentHashMap<String, Socket> users = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, PrintWriter> writers = new ConcurrentHashMap<>();
    public final CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<>();
    public final List<String> forbiddenWords = List.of("corona", "war", "flip", "dead");
}
