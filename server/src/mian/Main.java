package mian;

import messages.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static final int PORT = 9000;
    public static BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    public static ConcurrentHashMap<String, Socket> users = new ConcurrentHashMap<>();
    public static CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<>();
    public static List<String> forbiddenWords = new ArrayList<>();

    public static void main(String[] args) {
        initForbiddenWords();
        try {
            ServerSocket ss = new ServerSocket(PORT);
            Thread broadcast = new Thread(new BroadcastThread());
            broadcast.start();
            while(true){
                Socket client = ss.accept();
                Thread st = new Thread(new ServerThread(client));
                st.start();
            }
        } catch (IOException e) {
            System.out.println("Problem connecting to the client");
            e.printStackTrace();
        }

    }

    private static void initForbiddenWords() {
        forbiddenWords.add("corona");
        forbiddenWords.add("war");
        forbiddenWords.add("flip");
        forbiddenWords.add("dead");
    }
}
