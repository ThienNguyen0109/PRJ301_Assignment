package realtime;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/realtime")
public class RealtimeEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        Map<String, String> params = parseQuery(session.getQueryString());
        RealtimeSessionRegistry.register(session, params.get("accountId"), params.get("role"));
    }

    @OnClose
    public void onClose(Session session) {
        RealtimeSessionRegistry.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        RealtimeSessionRegistry.remove(session);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int index = pair.indexOf('=');
            if (index <= 0) {
                continue;
            }
            params.put(decode(pair.substring(0, index)), decode(pair.substring(index + 1)));
        }
        return params;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return value;
        }
    }
}
