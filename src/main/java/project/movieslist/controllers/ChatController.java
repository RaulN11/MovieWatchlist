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
import org.springframework.web.bind.annotation.ResponseBody;
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
            model.addAttribute("currentUser", authentication.getName());
        }
        return "chat";
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Authentication authentication) {
        System.out.println("Received message: " + chatMessage);

        // Set sender from authentication if not already set
        if (chatMessage.getSender() == null && authentication != null) {
            chatMessage.setSender(authentication.getName());
        }

        // Set timestamp if not already set
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date());
        }

        // Validate required fields
        if (chatMessage.getSender() == null || chatMessage.getReceiver() == null || chatMessage.getContent() == null) {
            System.err.println("Invalid message - missing required fields");
            return;
        }

        try {
            ChatMessage savedMsg = chatMessageService.save(chatMessage);
            System.out.println("Message saved: " + savedMsg);

            // Send notification to receiver
            ChatNotification notification = new ChatNotification(
                    savedMsg.getId(),
                    savedMsg.getSender(),
                    savedMsg.getReceiver(),
                    savedMsg.getContent()
            );

            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiver(),
                    "/queue/messages",
                    notification
            );

            System.out.println("Notification sent to: " + chatMessage.getReceiver());
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        System.out.println("Loading messages between: " + senderId + " and " + recipientId);
        List<ChatMessage> messages = chatMessageService.findChatMessages(senderId, recipientId);
        System.out.println("Found " + messages.size() + " messages");
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<String>> getConnectedUsers() {
        List<String> users = clientService.getAllUsernames();
        System.out.println("Returning " + users.size() + " users");
        return ResponseEntity.ok(users);
    }
}