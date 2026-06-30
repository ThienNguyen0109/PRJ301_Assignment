package realtime;

public final class RealtimeEventPublisher {
    private RealtimeEventPublisher() {
    }

    public static void publish(RealtimeEvent event) {
        RealtimeSessionRegistry.broadcast(event);
    }

    public static void staff(String type, String title, String message) {
        publish(new RealtimeEvent(type, RealtimeEvent.TARGET_STAFF, title, message, null));
    }

    public static void admin(String type, String title, String message) {
        publish(new RealtimeEvent(type, RealtimeEvent.TARGET_ADMIN, title, message, null));
    }

    public static void customer(String accountId, String type, String title, String message) {
        publish(new RealtimeEvent(type, RealtimeEvent.TARGET_CUSTOMER_PREFIX + accountId, title, message, null));
    }

    public static void all(String type, String title, String message) {
        publish(new RealtimeEvent(type, RealtimeEvent.TARGET_ALL, title, message, null));
    }
}
