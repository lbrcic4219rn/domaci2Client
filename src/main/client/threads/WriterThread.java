package main.client.threads;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriterThread implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(WriterThread.class.getName());

    private final Socket socket;
    private final AtomicBoolean running;

    public WriterThread(Socket socket, AtomicBoolean running) {
        this.socket = socket;
        this.running = running;
    }

    @Override
    public void run() {
        try (var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             var sc = new Scanner(System.in)) {

            LOGGER.info("Type your messages below (type '/quit' to exit):");

            while (sc.hasNextLine() && running.get()) {
                String command = sc.nextLine();
                if ("/quit".equalsIgnoreCase(command.trim())) {
                    LOGGER.log(Level.INFO, "Disconnecting...");
                    running.set(false);
                    break;
                }
                out.println(command);
                if (out.checkError()) {
                    LOGGER.log(Level.SEVERE, "Server connection lost. Cannot send message.");
                    running.set(false);
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Server Disconnected: {0}", e.getMessage());
        } finally {
            running.set(false);
        }
    }
}
