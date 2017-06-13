package pl.oskarpolak.websocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.*;

@Configuration
@EnableWebSocket
public class WebSocket extends BinaryWebSocketHandler implements WebSocketConfigurer {

    private Map<String, User> sessions =
            Collections.synchronizedMap(new HashMap<String, User>());
    private  List<String> badWords = Arrays.asList("oskar", "kurwa", "chuj", "baran");

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(this, "/chat").setAllowedOrigins("*");
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

        User userSending = sessions.get(session.getId());
        String messageConverted = cenzure(new String(message.getPayload().array()));

        if(userSending.getNick().isEmpty()){
            userSending.setNick(messageConverted);
            userSending.getSession().sendMessage(new BinaryMessage(("Ustawiliśmy Twój nick na: " + messageConverted).getBytes()));
        }else {
            for (User user : sessions.values()) {
                user.getSession().sendMessage(new BinaryMessage((userSending.getNick() + " : " +  messageConverted).getBytes()));
            }
        }
    }


    private String cenzure(String message) {
        String changedMessage = message;
        for(String word : badWords) {
            if(message.contains(word)){
                 changedMessage = "Jestem głupi i przeklinam. Przepraszam :(";
            }
        }
        return changedMessage;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), new User(session));

        session.sendMessage(new BinaryMessage("Witaj!".getBytes()));
        session.sendMessage(new BinaryMessage("Twoja pierwsza wiadomość, zostanie Twoim nickiem.".getBytes()));

        System.out.println("Zarejestrowalem nowego uzytkownika");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("Wyrejestrowałem uzytkownika");
    }
}
