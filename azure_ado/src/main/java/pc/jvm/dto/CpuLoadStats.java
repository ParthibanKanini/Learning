package pc.jvm.dto;

import org.json.JSONObject;

public class CpuLoadStats {

    public double processPercent;
    public double systemPercent;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("processPercent", processPercent >= 0
                ? String.format("%.2f", processPercent * 100) : "Not available");
        json.put("systemPercent", systemPercent >= 0
                ? String.format("%.2f", systemPercent * 100) : "Not available");
        return json;
    }
}
