package realtime;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;

public final class RealtimeSessionRegistry {
    private static final Logger LOGGER = Logger.getLogger(RealtimeSessionRegistry.class.getName());
    private static final Map<Session, ClientInfo> CLIENTS = new ConcurrentHashMap<>();

    private RealtimeSessionRegistry() {
    }

    public static void register(Session session, String accountId, String role) {
        CLIENTS.put(session, new ClientInfo(normalize(accountId), normalize(role)));
    }

    public static void remove(Session session) {
        CLIENTS.remove(session);
    }

    public static void broadcast(RealtimeEvent event) {
        if (event == null) {
            return;
        }
        String payload = event.toJson();
        for (Map.Entry<Session, ClientInfo> entry : CLIENTS.entrySet()) {
            Session session = entry.getKey();
            if (!session.isOpen()) {
                CLIENTS.remove(session);
                continue;
            }
            if (matches(event.getTarget(), entry.getValue())) {
                send(session, payload);
            }
        }
    }

    private static boolean matches(String target, ClientInfo client) {
        String normalizedTarget = normalize(target);
        if (RealtimeEvent.TARGET_ALL.equals(normalizedTarget)) {
            return true;
        }
        if (RealtimeEvent.TARGET_ADMIN.equals(normalizedTarget)
                || RealtimeEvent.TARGET_STAFF.equals(normalizedTarget)) {
            return normalizedTarget.equals(client.role);
        }
        if (normalizedTarget.startsWith(RealtimeEvent.TARGET_CUSTOMER_PREFIX)) {
            String accountId = normalizedTarget.substring(RealtimeEvent.TARGET_CUSTOMER_PREFIX.length());
            return "CUSTOMER".equals(client.role) && accountId.equals(client.accountId);
        }
        return false;
    }

    private static void send(Session session, String payload) {
        try {
            synchronized (session) {
                session.getBasicRemote().sendText(payload);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "Could not send realtime event", ex);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private static final class ClientInfo {
        private final String accountId;
        private final String role;

        private ClientInfo(String accountId, String role) {
            this.accountId = accountId;
            this.role = role;
        }
    }
}
