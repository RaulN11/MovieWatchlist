package project.movieslist.event_listeners;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import project.movieslist.services.ClientService;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ClientService clientService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication != null){
            String username = authentication.getName();
            try {
                clientService.setUserOffline(username);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        response.sendRedirect("/homepage");
    }


}
