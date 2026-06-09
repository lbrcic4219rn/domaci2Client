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
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        AtomicBoolean running = new AtomicBoolean(true);


        try (Socket socket = new Socket(HOST, PORT)) {
            LOGGER.info("Connected to the server at " + HOST + ":" + PORT);

            Thread reader = new Thread(new ReaderThread(socket, running));
            Thread writer = new Thread(new WriterThread(socket, running));
            reader.start();
            writer.start();

            writer.join();
            running.set(false);
            socket.close();
            reader.join();
            LOGGER.log(Level.INFO, "Client terminated gracefully.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to server: {0}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Application interrupted.");
        }
    }
}
