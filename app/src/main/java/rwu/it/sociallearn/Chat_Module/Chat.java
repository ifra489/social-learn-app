package rwu.it.sociallearn.Chat_Module;

public class Chat {
    private String chatId;
    private String chatName;   // Group name or user name
    private String lastMessage;
    private String chatType;   // "group" or "personal"
    private String imageUrl;   // For group/user profile

    public Chat() {}

    public Chat(String chatId, String chatName, String lastMessage, String chatType, String imageUrl) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.chatType = chatType;
        this.imageUrl = imageUrl;
    }

    public String getChatId() { return chatId; }
    public String getChatName() { return chatName; }
    public String getLastMessage() { return lastMessage; }
    public String getChatType() { return chatType; }
    public String getImageUrl() { return imageUrl; }

    public void setChatId(String chatId) { this.chatId = chatId; }
    public void setChatName(String chatName) { this.chatName = chatName; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setChatType(String chatType) { this.chatType = chatType; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
