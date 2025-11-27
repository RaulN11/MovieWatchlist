package project.movieslist.event_listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import project.movieslist.services.ClientService;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {
    @Autowired
    private ClientService clientService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        clientService.setUserOnline(username);
    }

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent event) {
        event.getSecurityContexts().forEach(context->{
            if(context.getAuthentication()!=null){
                String username = context.getAuthentication().getName();
                clientService.setUserOffline(username);
            }
        });
    }
}
