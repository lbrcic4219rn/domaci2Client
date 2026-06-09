package main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static final int PORT = 9000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", PORT);

            Thread reader = new Thread(new ReaderThread(socket));
            Thread writer = new Thread(new WriterThread(socket));
            reader.start();
            writer.start();

        } catch (IOException e) {
            System.out.println("Error while connecting, please try again");
            //e.printStackTrace();
        }
    }
}
