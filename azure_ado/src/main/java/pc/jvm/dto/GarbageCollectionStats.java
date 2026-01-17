package pc.jvm.dto;

import java.util.List;

import org.json.JSONObject;

public class GarbageCollectionStats {

    public List<GcCollectorStats> collectors;
    public long totalCollectionCount;
    public long totalCollectionTimeMs;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("collectors", collectors.stream()
                .map(GcCollectorStats::toJSON)
                .toList());
        json.put("totalCollectionCount", totalCollectionCount);
        json.put("totalCollectionTimeMs", totalCollectionTimeMs);
        return json;
    }
}
