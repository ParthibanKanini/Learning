package pc.jvm.dto;

import org.json.JSONObject;

public class UptimeStats {

    public long uptimeMs;
    public long uptimeSeconds;
    public long uptimeMinutes;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("uptimeMs", uptimeMs);
        json.put("uptimeSeconds", uptimeSeconds);
        json.put("uptimeMinutes", uptimeMinutes);
        return json;
    }
}
