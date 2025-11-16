package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.movieslist.model.ChatRoom;
import project.movieslist.repositories.ChatRoomRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatroomId(String senderName, String receiverName, Boolean newIfNotExists) {
        return chatRoomRepository.findBySenderNameAndReceiverName(senderName, receiverName)
                .map(ChatRoom::getChatId)
                .or(() -> chatRoomRepository.findBySenderNameAndReceiverName(receiverName, senderName)
                        .map(ChatRoom::getChatId))
                .or(() -> {
                    if (newIfNotExists) {
                        var chatId = createChatId(senderName, receiverName);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatId(String senderName, String receiverName) {
        String chatId;
        if (senderName.compareTo(receiverName) < 0) {
            chatId = String.format("%s_%s", senderName, receiverName);
        } else {
            chatId = String.format("%s_%s", receiverName, senderName);
        }

        ChatRoom senderReceiver = ChatRoom.builder()
                .chatId(chatId)
                .senderName(senderName)
                .receiverName(receiverName)
                .build();
        ChatRoom receiverSender = ChatRoom.builder()
                .chatId(chatId)
                .senderName(receiverName)
                .receiverName(senderName)
                .build();
        chatRoomRepository.save(senderReceiver);
        chatRoomRepository.save(receiverSender);
        return chatId;
    }
}