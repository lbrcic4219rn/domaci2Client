package main.server.threads;

import main.server.ChatServer;
import main.server.messages.Message;
import main.server.messages.MessageType;

public class BroadcastThread implements Runnable{
    private final ChatServer server;

    public BroadcastThread(ChatServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = server.messages.take();
                server.writers.forEach((username, writer) -> {
                    if (message.type() == MessageType.CONNECT && message.username().equals(username)) {
                        return;
                    }

                    writer.println(message.data());

                    if (writer.checkError()) {
                        server.writers.remove(username);
                        server.users.remove(username);
                    }
                });
            } catch (InterruptedException e) {
                System.out.println("Unexpected interrupt");
                break;
            }
        }
    }
}
