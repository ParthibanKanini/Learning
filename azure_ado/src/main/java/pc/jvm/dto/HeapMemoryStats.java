package pc.jvm.dto;

import java.util.List;

import org.json.JSONObject;

public class HeapMemoryStats {

    public double initMB;
    public double usedMB;
    public double committedMB;
    public double maxMB;
    public double usagePercent;
    public List<MemoryPoolStats> pools;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("initMB", String.format("%.2f", initMB));
        json.put("usedMB", String.format("%.2f", usedMB));
        json.put("committedMB", String.format("%.2f", committedMB));
        json.put("maxMB", String.format("%.2f", maxMB));
        json.put("usagePercent", String.format("%.2f", usagePercent));

        if (pools != null && !pools.isEmpty()) {
            json.put("pools", pools.stream()
                    .map(MemoryPoolStats::toJSON)
                    .toList());
        }

        return json;
    }
}
