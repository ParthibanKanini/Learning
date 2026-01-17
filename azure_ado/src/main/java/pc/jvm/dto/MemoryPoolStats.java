package pc.jvm.dto;

import org.json.JSONObject;

public class MemoryPoolStats {

    public String name;
    public String type;
    public double usedMB;
    public double committedMB;
    public double maxMB;
    public double usagePercent;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("type", type);
        json.put("usedMB", String.format("%.2f", usedMB));
        json.put("committedMB", String.format("%.2f", committedMB));

        if (maxMB != -1) {
            json.put("maxMB", String.format("%.2f", maxMB));
            json.put("usagePercent", String.format("%.2f", usagePercent));
        } else {
            json.put("maxMB", "unlimited");
            json.put("usagePercent", "N/A");
        }

        return json;
    }
}
