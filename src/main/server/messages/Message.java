package main.server.messages;

public record Message(String data, MessageType type, String username) {
}
