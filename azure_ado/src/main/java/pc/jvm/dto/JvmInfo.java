package pc.jvm.dto;

import org.json.JSONObject;

public class JvmInfo {

    public String jvmName;
    public String jvmVersion;
    public String jvmVendor;
    public long startTime;
    public long pid;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("jvmName", jvmName);
        json.put("jvmVersion", jvmVersion);
        json.put("jvmVendor", jvmVendor);
        json.put("startTime", startTime);
        json.put("pid", pid);
        return json;
    }
}
