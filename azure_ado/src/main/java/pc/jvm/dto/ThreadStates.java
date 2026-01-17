package pc.jvm.dto;

import org.json.JSONObject;

public class ThreadStates {

    public int runnable;
    public int blocked;
    public int waiting;
    public int timedWaiting;
    public int newState;
    public int terminated;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("runnable", runnable);
        json.put("blocked", blocked);
        json.put("waiting", waiting);
        json.put("timedWaiting", timedWaiting);
        json.put("new", newState);
        json.put("terminated", terminated);
        return json;
    }
}
