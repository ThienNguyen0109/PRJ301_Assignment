package realtime;

import java.sql.Timestamp;

public class RealtimeEvent {
    public static final String TARGET_ALL = "ALL";
    public static final String TARGET_ADMIN = "ADMIN";
    public static final String TARGET_STAFF = "STAFF";
    public static final String TARGET_CUSTOMER_PREFIX = "CUSTOMER:";

    private final String type;
    private final String target;
    private final String title;
    private final String message;
    private final String dataJson;
    private final Timestamp createdAt;

    public RealtimeEvent(String type, String target, String title, String message, String dataJson) {
        this.type = type;
        this.target = target;
        this.title = title;
        this.message = message;
        this.dataJson = dataJson;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDataJson() {
        return dataJson;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        appendField(json, "type", type).append(",");
        appendField(json, "target", target).append(",");
        appendField(json, "title", title).append(",");
        appendField(json, "message", message).append(",");
        appendField(json, "createdAt", String.valueOf(createdAt.getTime()));
        if (dataJson != null && !dataJson.trim().isEmpty()) {
            json.append(",\"data\":").append(dataJson);
        }
        json.append("}");
        return json.toString();
    }

    private StringBuilder appendField(StringBuilder json, String name, String value) {
        return json.append("\"")
                .append(escape(name))
                .append("\":\"")
                .append(escape(value))
                .append("\"");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (ch < 32) {
                        result.append(String.format("\\u%04x", (int) ch));
                    } else {
                        result.append(ch);
                    }
                    break;
            }
        }
        return result.toString();
    }
}
