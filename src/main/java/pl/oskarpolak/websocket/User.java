package pl.oskarpolak.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by Lenovo on 13.06.2017.
 */
public class User {
    private WebSocketSession session;
    private String nick;

    public User(WebSocketSession session) {
        this.session = session;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
