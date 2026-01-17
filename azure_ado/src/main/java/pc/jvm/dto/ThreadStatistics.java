package pc.jvm.dto;

import org.json.JSONObject;

public class ThreadStatistics {

    public ThreadCounts counts;
    public ThreadStates states;
    public int deadlockedThreadCount;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("counts", counts.toJSON());
        json.put("states", states.toJSON());
        json.put("deadlockedThreadCount", deadlockedThreadCount);
        return json;
    }
}
