package project.movieslist.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.movieslist.model.ChatMessage;
import project.movieslist.model.ChatNotification;
import project.movieslist.services.ChatMessageService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMessage=chatMessageService.save(chatMessage);
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getReceiverName(),"/queue/messages",
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .senderName(savedMessage.getSenderName())
                        .receiverName(savedMessage.getReceiverName())
                        .content(savedMessage.getContent())
                        .build());

    }

    @PostMapping("/test/chat")
    public ResponseEntity<ChatMessage> testSendMessage(@RequestBody ChatMessage message) {
        ChatMessage saved = chatMessageService.save(message);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/messages/{senderName}/{receiverName}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("senderName") String senderName, @PathVariable("receiverName") String receiverName) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderName, receiverName));
    }

}
