package main.client.threads;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class WriterThread implements Runnable{

    private final Socket socket;
    private final AtomicBoolean running;

    public WriterThread(Socket socket, AtomicBoolean running) {
        this.socket = socket;
        this.running = running;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Type your messages below (type '/quit' to exit):");

            while (sc.hasNextLine() && running.get()) {
                String command = sc.nextLine();
                if ("/quit".equalsIgnoreCase(command.trim())) {
                    System.out.println("Disconnecting...");
                    running.set(false);
                    break;
                }
                out.println(command);
                if (out.checkError()) {
                    System.err.println("Server connection lost. Cannot send message.");
                    running.set(false);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Server Disconnected");
        } finally {
            running.set(false);
        }
    }
}
