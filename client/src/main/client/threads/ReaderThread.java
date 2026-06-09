package main.client.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReaderThread implements Runnable{

    private final Socket socket;
    private final AtomicBoolean running;

    public ReaderThread(Socket socket, AtomicBoolean running) {
        this.socket = socket;
        this.running = running;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while((msg = in.readLine()) != null && running.get()){
                System.out.println(msg);
            }
            if(running.get()) {
                System.out.println("Server closed the connection gracefully");
            }
        } catch (IOException e) {
            if (running.get()) {
                System.err.println("Server disconnected unexpectedly: " + e.getMessage());
            }
        } finally {
            running.set(false);
        }
    }
}
