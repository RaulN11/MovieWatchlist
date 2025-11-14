package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.movieslist.model.ChatMessage;
import project.movieslist.repositories.ChatMessageRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId=chatRoomService.getChatroomId(chatMessage.getSenderName(), chatMessage.getReceiverName(), true);
        chatMessage.setChatId(chatId.get());
        return repository.save(chatMessage);
    }
    public List<ChatMessage> findChatMessages(String senderName, String receiverName) {
        var chatId=chatRoomService.getChatroomId(senderName, receiverName, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}
