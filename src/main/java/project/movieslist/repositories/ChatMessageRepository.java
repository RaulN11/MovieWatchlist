package project.movieslist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import project.movieslist.model.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);
}
