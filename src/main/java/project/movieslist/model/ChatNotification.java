package project.movieslist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



public class ChatNotification {
    private String id;
    private String sender;
    private String receiver;
    private String content;

    public ChatNotification() {
    }

    public ChatNotification(String id, String sender, String receiver, String content) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public String toString() {
        return "ChatNotification{" +
                "id='" + id + '\'' +
                ", senderId='" + sender + '\'' +
                ", recipientId='" + receiver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String senderId;
        private String recipientId;
        private String content;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder senderId(String senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder recipientId(String recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ChatNotification build() {
            return new ChatNotification(id, senderId, recipientId, content);
        }
    }
}
