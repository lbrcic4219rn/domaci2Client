package messages;

public class Message {
    private String data;
    private MessageType type;
    private String username;

    public Message(String data, MessageType type, String username) {
        this.username = username;
        this.data = data;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
