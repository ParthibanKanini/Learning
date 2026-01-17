package pc.jvm.dto;

import java.util.List;

import org.json.JSONObject;

public class NonHeapMemoryStats {

    public double usedMB;
    public double committedMB;
    public double maxMB;
    public double usagePercent;
    public List<MemoryPoolStats> pools;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("usedMB", String.format("%.2f", usedMB));
        json.put("committedMB", String.format("%.2f", committedMB));

        if (maxMB != -1) {
            json.put("maxMB", String.format("%.2f", maxMB));
            json.put("usagePercent", String.format("%.2f", usagePercent));
        } else {
            json.put("maxMB", "unlimited");
            json.put("usagePercent", "N/A");
        }

        if (pools != null && !pools.isEmpty()) {
            json.put("pools", pools.stream()
                    .map(MemoryPoolStats::toJSON)
                    .toList());
        }

        return json;
    }
}
