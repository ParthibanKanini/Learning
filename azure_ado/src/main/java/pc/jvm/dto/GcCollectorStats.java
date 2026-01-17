package pc.jvm.dto;

import org.json.JSONObject;

public class GcCollectorStats {

    public String name;
    public long collectionCount;
    public long collectionTimeMs;
    public double avgCollectionTimeMs;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("collectionCount", collectionCount);
        json.put("collectionTimeMs", collectionTimeMs);

        if (collectionCount > 0) {
            json.put("avgCollectionTimeMs", String.format("%.2f", avgCollectionTimeMs));
        } else {
            json.put("avgCollectionTimeMs", "N/A");
        }

        return json;
    }
}
