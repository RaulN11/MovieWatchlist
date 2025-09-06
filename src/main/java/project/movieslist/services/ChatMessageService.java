package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.movieslist.model.ChatMessage;
import project.movieslist.repositories.ChatMessageRepository;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSender(), chatMessage.getReceiver(), true)
                .orElseThrow(() -> new RuntimeException("Could not create chat room"));
        chatMessage.setChatId(chatId);
        return repository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String sender, String receiver) {
        try {
            var chatId = chatRoomService.getChatRoomId(sender, receiver, false);
            List<ChatMessage> messages = chatId.map(repository::findByChatId).orElse(new ArrayList<>());
            var reverseChatId = chatRoomService.getChatRoomId(receiver, sender, false);
            if (reverseChatId.isPresent() && !reverseChatId.equals(chatId)) {
                List<ChatMessage> reverseMessages = repository.findByChatId(reverseChatId.get());
                messages.addAll(reverseMessages);
            }
            messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
            return messages;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}