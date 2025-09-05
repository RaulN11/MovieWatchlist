package project.movieslist.services;

import org.springframework.stereotype.Service;
import project.movieslist.model.ChatMessage;
import project.movieslist.repositories.ChatMessageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessageService(ChatMessageRepository repository, ChatRoomService chatRoomService) {
        this.repository = repository;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        try {
            var chatId = chatRoomService
                    .getChatRoomId(chatMessage.getSender(), chatMessage.getReceiver(), true)
                    .orElseThrow(() -> new RuntimeException("Could not create chat room"));

            chatMessage.setChatId(chatId);
            ChatMessage saved = repository.save(chatMessage);
            System.out.println("Message saved with chatId: " + chatId);
            return saved;
        } catch (Exception e) {
            System.err.println("Error saving message: " + e.getMessage());
            throw e;
        }
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        try {
            var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
            List<ChatMessage> messages = chatId.map(repository::findByChatId).orElse(new ArrayList<>());

            // Also check the reverse direction
            var reverseChatId = chatRoomService.getChatRoomId(recipientId, senderId, false);
            if (reverseChatId.isPresent() && !reverseChatId.equals(chatId)) {
                List<ChatMessage> reverseMessages = repository.findByChatId(reverseChatId.get());
                messages.addAll(reverseMessages);
            }

            // Sort by timestamp
            messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

            System.out.println("Found " + messages.size() + " messages for chat between " + senderId + " and " + recipientId);
            return messages;
        } catch (Exception e) {
            System.err.println("Error finding messages: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}