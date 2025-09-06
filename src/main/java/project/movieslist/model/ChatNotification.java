package project.movieslist.model;

import lombok.*;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatNotification {
    private String id;
    private String sender;
    private String receiver;
    private String content;
}
