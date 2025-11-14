package project.movieslist.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import project.movieslist.model.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    public Optional<ChatRoom> findBySenderNameAndReceiverName(String senderName, String receiverName);
}
