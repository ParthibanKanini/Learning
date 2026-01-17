package pc.jvm.dto;

import org.json.JSONObject;

public class MemoryStatistics {

    public String logType;
    public long timestamp;
    public JvmInfo jvmInfo;
    public HeapMemoryStats heapMemory;
    public NonHeapMemoryStats nonHeapMemory;
    public GarbageCollectionStats garbageCollection;
    public ThreadStatistics threads;
    public SystemStatistics system;
    public ClassLoadingStats classLoading;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("logType", logType);
        json.put("timestamp", timestamp);

        if (jvmInfo != null) {
            json.put("jvmInfo", jvmInfo.toJSON());
        }

        json.put("heapMemory", heapMemory.toJSON());
        json.put("nonHeapMemory", nonHeapMemory.toJSON());
        json.put("garbageCollection", garbageCollection.toJSON());
        json.put("threads", threads.toJSON());
        json.put("system", system.toJSON());
        json.put("classLoading", classLoading.toJSON());

        return json;
    }
}
