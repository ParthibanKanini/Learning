package pc.jvm.dto;

import org.json.JSONObject;

public class SwapSpaceStats {

    public double totalMB;
    public double usedMB;
    public double freeMB;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("totalMB", String.format("%.2f", totalMB));
        json.put("usedMB", String.format("%.2f", usedMB));
        json.put("freeMB", String.format("%.2f", freeMB));
        return json;
    }
}
