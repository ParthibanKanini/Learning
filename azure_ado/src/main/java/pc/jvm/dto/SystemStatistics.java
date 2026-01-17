package pc.jvm.dto;

import org.json.JSONObject;

public class SystemStatistics {

    public int availableProcessors;
    public double systemLoadAverage;
    public PhysicalMemoryStats physicalMemory;
    public SwapSpaceStats swapSpace;
    public CpuLoadStats cpuLoad;
    public double committedVirtualMemoryMB;
    public UptimeStats uptime;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("availableProcessors", availableProcessors);
        json.put("systemLoadAverage", systemLoadAverage >= 0
                ? String.format("%.2f", systemLoadAverage) : "Not available");

        if (physicalMemory != null) {
            json.put("physicalMemory", physicalMemory.toJSON());
        }

        if (swapSpace != null) {
            json.put("swapSpace", swapSpace.toJSON());
        }

        if (cpuLoad != null) {
            json.put("cpuLoad", cpuLoad.toJSON());
        }

        if (committedVirtualMemoryMB > 0) {
            json.put("committedVirtualMemoryMB", String.format("%.2f", committedVirtualMemoryMB));
        }

        if (uptime != null) {
            json.put("uptime", uptime.toJSON());
        }

        return json;
    }
}
