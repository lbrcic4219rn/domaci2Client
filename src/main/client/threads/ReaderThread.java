package main.client.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReaderThread implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(ReaderThread.class.getName());

    private final Socket socket;
    private final AtomicBoolean running;

    public ReaderThread(Socket socket, AtomicBoolean running) {
        this.socket = socket;
        this.running = running;
    }

    @Override
    public void run() {
        try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while((msg = in.readLine()) != null && running.get()){
                LOGGER.info(msg);
            }
            if(running.get()) {
                LOGGER.info("Server closed the connection gracefully");
            }
        } catch (IOException e) {
            if (running.get()) {
                LOGGER.log(Level.WARNING, "Server disconnected unexpectedly: {0}", e.getMessage());
            }
        } finally {
            running.set(false);
        }
    }
}
