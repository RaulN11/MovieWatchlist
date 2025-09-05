package project.movieslist.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.movieslist.model.ChatMessage;
import project.movieslist.model.ChatNotification;
import project.movieslist.services.ChatMessageService;
import project.movieslist.services.ClientService;

import java.util.Date;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ClientService clientService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageService chatMessageService,
                          ClientService clientService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.clientService = clientService;
    }
    @GetMapping("/chat")
    public String chatPage(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "chat";
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Authentication authentication) {
        // Set sender from authentication if not already set
        if (chatMessage.getSender() == null) {
            chatMessage.setSender(authentication.getName());
        }

        // Set timestamp if not already set
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date());
        }

        ChatMessage savedMsg = chatMessageService.save(chatMessage);

        // Send notification to receiver
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSender(),
                        savedMsg.getReceiver(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<String>> getConnectedUsers() {
        // Return a list of all usernames for simplicity
        // You might want to implement a more sophisticated user presence system
        List<String> users = clientService.getAllUsernames();
        return ResponseEntity.ok(users);
    }
}