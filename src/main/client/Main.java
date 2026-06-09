package main.client;

import main.client.threads.ReaderThread;
import main.client.threads.WriterThread;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 9000;

    public static void main(String[] args) {
        AtomicBoolean running = new AtomicBoolean(true);

        Logger logger = Logger.getLogger(Main.class.getName());

        try (Socket socket = new Socket(HOST, PORT)) {
            logger.info("Connected to the server at " + HOST + ":" + PORT);

            Thread reader = new Thread(new ReaderThread(socket, running));
            Thread writer = new Thread(new WriterThread(socket, running));
            reader.start();
            writer.start();

            writer.join();
            running.set(false);
            socket.close();
            reader.join();
            logger.info("Client terminated gracefully.");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to connect to server: {0}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Application interrupted.");
        }
    }
}
