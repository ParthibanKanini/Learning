package pc.jvm.dto;

import org.json.JSONObject;

public class ThreadCounts {

    public int current;
    public int peak;
    public long totalStarted;
    public int daemon;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("current", current);
        json.put("peak", peak);
        json.put("totalStarted", totalStarted);
        json.put("daemon", daemon);
        return json;
    }
}
