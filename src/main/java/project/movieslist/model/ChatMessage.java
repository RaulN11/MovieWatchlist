package project.movieslist.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ChatMessage {

    @Id
    private String id;
    private String chatId;
    private String sender;
    private String receiver;
    private String content;
    private Date timestamp;

}
