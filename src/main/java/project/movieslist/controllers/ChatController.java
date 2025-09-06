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
    public String chatPage(Model model, Authentication auth) {
        model.addAttribute("currentUser", auth.getName());
        return "chat";
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Authentication auth) {
        String authenticatedUsername = auth.getName();
        chatMessage.setSender(authenticatedUsername);
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(new Date());
        }
        if (chatMessage.getSender() == null || chatMessage.getReceiver() == null || chatMessage.getContent() == null) {
            return;
        }

        if (chatMessage.getSender().equals(chatMessage.getReceiver())) {
            System.err.println("Cannot send message to self");
            return;
        }

        try {
            ChatMessage savedMsg = chatMessageService.save(chatMessage);
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
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSender(),
                    "/queue/messages",
                    notification
            );

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/messages/{sender}/{receiver}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String sender,
                                                              @PathVariable String receiver,
                                                              Authentication auth) {
        if (auth == null || (!auth.getName().equals(sender) && !auth.getName().equals(receiver))) {
            System.err.println("Unauthorized access attempt");
            return ResponseEntity.status(403).build();
        }
        try {
            List<ChatMessage> messages = chatMessageService.findChatMessages(sender, receiver);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<List<String>> getConnectedUsers(Authentication authentication) {
        try {
            List<String> users = clientService.getAllUsernames();
            users.removeIf(username -> username.equals(authentication.getName()));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}