package project.movieslist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.movieslist.repositories.ChatRoomRepository;
import project.movieslist.model.ChatRoom;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    public Optional<String> getChatRoomId(String sender, String receiver, boolean createNewRoomIfNotExists){
        return chatRoomRepository
                .findBySenderAndReceiver(sender, receiver)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(sender, receiver);
                        return Optional.of(chatId);
                    }
                    return  Optional.empty();
                });
    }

    private String createChatId(String sender, String receiver) {
        var chatId = String.format("%s_%s", sender, receiver);
        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(sender)
                .receiver(receiver)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(receiver)
                .receiver(sender)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
}

