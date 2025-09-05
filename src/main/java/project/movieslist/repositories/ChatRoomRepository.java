package project.movieslist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import project.movieslist.model.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
