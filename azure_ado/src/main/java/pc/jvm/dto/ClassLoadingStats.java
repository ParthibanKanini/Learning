package pc.jvm.dto;

import org.json.JSONObject;

public class ClassLoadingStats {

    public int loadedClassCount;
    public long totalLoadedClassCount;
    public long unloadedClassCount;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("loadedClassCount", loadedClassCount);
        json.put("totalLoadedClassCount", totalLoadedClassCount);
        json.put("unloadedClassCount", unloadedClassCount);
        return json;
    }
}
