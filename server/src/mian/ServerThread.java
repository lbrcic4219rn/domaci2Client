package mian;

import messages.Message;
import messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ServerThread implements Runnable{

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            //register user
            String username = "";
            out.println("Please enter username: ");
            while(true){
                username = in.readLine();
                if(!Main.users.containsKey(username)){
                    Main.messages.put(new Message("User: " + username + " connected.", MessageType.CONNECT, username));
                    out.println("Success!!!");
                    out.println("Welcome: " + username);
                    Main.users.put(username, socket);

                    //Print history
                    Iterator iterator = Main.history.iterator();
                    while(iterator.hasNext()){
                        out.println(iterator.next());
                    }
                    break;
                } else {
                    out.println("Please select another username: ");
                }
            }

            String datePattern = "dd-MM-yyyy HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);


            while(true) {
                String msg = in.readLine();
                msg = sanitizeMessage(msg);
                Date now = new Date();
                String date = simpleDateFormat.format(now);
                String newMessage = date + " - " + username + " : " + msg;
                Main.messages.put(new Message(newMessage, MessageType.MESSAGE, username));
                if(Main.history.size() >= 100){
                    Main.history.remove(0);
                }
                Main.history.add(newMessage);
            }
        } catch (IOException e) {
            System.out.println("Problem connecting to the client");
            //e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Unexpected interrupt");
            //e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Problem disconnecting");
                }
            }

            if(out != null){
                out.close();
            }

            if(this.socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Problem disconnecting");
                }
            }
        }
    }

    private String sanitizeMessage(String msg) {
        for (String forbiddenWord: Main.forbiddenWords){
            StringBuilder sb = new StringBuilder();
            String replacement = "" + forbiddenWord.charAt(0);
            for(int i = 0; i < forbiddenWord.length() - 2; i++){
                replacement += "*";
            }
            replacement += forbiddenWord.charAt(forbiddenWord.length() - 1);
            msg = msg.replace(forbiddenWord, replacement);
        }
        return msg;
    }
}
