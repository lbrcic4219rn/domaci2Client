package mian;

import messages.Message;
import messages.MessageType;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BroadcastThread implements Runnable{


    @Override
    public void run() {
        while(true){
            try {
                //Uzima poruku iz reda
                Message message = Main.messages.take();
                //Cita sve usere koji postoje i salje im poruku
                Main.users.entrySet().forEach(user -> {
                    if(message.getType().equals(MessageType.CONNECT) && message.getUsername().equals(user.getKey())){
                        return;
                    }
                    try {
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(user.getValue().getOutputStream()), true);
                        out.println(message.getData());
                    } catch (IOException e) {
                        System.out.println("Problem connecting to the client");
                        //e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                System.out.println("Unexpected interrupt");
                //e.printStackTrace();
            }
        }
    }
}
