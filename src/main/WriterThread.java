package main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriterThread implements Runnable{

    private Socket usr = null;

    public WriterThread(Socket usr) {
        this.usr = usr;
    }

    @Override
    public void run() {
        PrintWriter out = null;

        try {
            out = new PrintWriter(new OutputStreamWriter(usr.getOutputStream()), true);
            Scanner sc = new Scanner(System.in);

            while(true) {
                String command = sc.nextLine();
                out.println(command);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
        }
    }
}
