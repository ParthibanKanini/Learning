package pc.jvm.monitor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.jvm.dto.ClassLoadingStats;
import pc.jvm.dto.GarbageCollectionStats;
import pc.jvm.dto.GcCollectorStats;
import pc.jvm.dto.MemoryPoolStats;
import pc.jvm.dto.SystemStatistics;
import pc.jvm.dto.ThreadStatistics;

/**
 * Handles logging of JVM memory and system statistics. Provides formatted table
 * output for various monitoring metrics.
 */

public class MemoryStatsLogger {

    private static final Logger logger = LoggerFactory.getLogger(MemoryStatsLogger.class);

    /**
     * Logs table header.
     */
    private void logTableHeader(String title) {
        logger.info("{}:", title);
        logger.info("+---------------+---------------+");
        logger.info("| Metric        | Value         |");
        logger.info("+---------------+---------------+");
    }

    /**
     * Logs table footer.
     */
    private void logTableFooter() {
        logger.info("+---------------+---------------+");
    }

    /**
     * Logs a table row with formatted value.
     */
    private void logTableRow(String metric, String value) {
        logger.info("| {} | {} |", String.format("%-13s", metric), String.format("%13s", value));
    }

    /**
     * Unified method to log memory usage statistics.
     * 
     * @param memoryType Type of memory (HEAP, NON-HEAP, or empty for pools)
     * @param initMB Initial memory in MB (-1 if not applicable)
     * @param usedMB Used memory in MB
     * @param committedMB Committed memory in MB
     * @param maxMB Maximum memory in MB (-1 if unlimited)
     * @param usagePercent Usage percentage (-1 if not applicable)
     */
    public void logMemoryUsage(String memoryType, double initMB, double usedMB,
            double committedMB, double maxMB, double usagePercent) {
        String title = memoryType.isEmpty() ? "" : memoryType + " Memory Statistics";
        logTableHeader(title);

        if (initMB != -1) {
            logTableRow("Initial", String.format("%.2f MB", initMB));
        }
        logTableRow("Committed", String.format("%.2f MB", committedMB));
        logTableRow("Used", String.format("%.2f MB", usedMB));

        if (maxMB == -1) {
            logTableRow("Max", "Unlimited");
        } else {
            logTableRow("Max", String.format("%.2f MB", maxMB));
            if (usagePercent != -1) {
                logTableRow("Usage", String.format("%.2f %%", usagePercent));
            }
        }

        logTableFooter();
    }

    /**
     * Logs detailed memory pool statistics from collected data.
     * 
     * @param pools List of memory pool statistics to log
     */
    public void logMemoryPools(List<MemoryPoolStats> pools) {
        for (MemoryPoolStats pool : pools) {
            logger.info("Pool({}) : [{}]", pool.type, pool.name);
            logMemoryUsage("", -1, pool.usedMB, pool.committedMB, pool.maxMB, pool.usagePercent);
        }
    }

    /**
     * Logs garbage collection statistics from collected data.
     * 
     * @param gcStats Garbage collection statistics to log
     */
    public void logGarbageCollection(GarbageCollectionStats gcStats) {
        for (GcCollectorStats collector : gcStats.collectors) {
            logTableHeader("GC [" + collector.name + "]");

            if (collector.collectionCount > 0) {
                logTableRow("Collections", String.format("%d", collector.collectionCount));
                logTableRow("Total Time", String.format("%d ms", collector.collectionTimeMs));
                logTableRow("Avg Time", String.format("%.2f ms", collector.avgCollectionTimeMs));
            } else {
                logTableRow("Collections", "0");
                logTableRow("Status", "Not yet run");
            }

            logTableFooter();
        }

        if (gcStats.totalCollectionCount > 0) {
            logger.info("GC Total: Collections={} | Total Time={} ms",
                    gcStats.totalCollectionCount, gcStats.totalCollectionTimeMs);
        }
    }

    /**
     * Logs thread statistics from collected data.
     * 
     * @param threadStats Thread statistics to log
     */
    public void logThreads(ThreadStatistics threadStats) {
        logTableHeader("Thread Statistics");
        logTableRow("Current", String.format("%d", threadStats.counts.current));
        logTableRow("Peak", String.format("%d", threadStats.counts.peak));
        logTableRow("Total Started", String.format("%d", threadStats.counts.totalStarted));
        logTableRow("Daemon", String.format("%d", threadStats.counts.daemon));
        logTableFooter();

        logTableHeader("Thread States");
        logTableRow("RUNNABLE", String.format("%d", threadStats.states.runnable));
        logTableRow("BLOCKED", String.format("%d", threadStats.states.blocked));
        logTableRow("WAITING", String.format("%d", threadStats.states.waiting));
        logTableRow("TIMED_WAITING", String.format("%d", threadStats.states.timedWaiting));
        logTableRow("NEW", String.format("%d", threadStats.states.newState));
        logTableRow("TERMINATED", String.format("%d", threadStats.states.terminated));
        logTableFooter();

        if (threadStats.deadlockedThreadCount > 0) {
            logger.warn("DEADLOCK DETECTED: {} threads are deadlocked!", threadStats.deadlockedThreadCount);
        }
    }

    /**
     * Logs system statistics from collected data.
     * 
     * @param sysStats System statistics to log
     */
    public void logSystem(SystemStatistics sysStats) {
        logger.info("System: Available Processors={} | System Load Average={}",
                sysStats.availableProcessors,
                sysStats.systemLoadAverage >= 0 ? String.format("%.2f", sysStats.systemLoadAverage) : "Not available");

        if (sysStats.physicalMemory != null) {
            logger.info("Physical Memory: Total={} MB | Used={} MB | Free={} MB | Usage={} %",
                    String.format("%.2f", sysStats.physicalMemory.totalMB),
                    String.format("%.2f", sysStats.physicalMemory.usedMB),
                    String.format("%.2f", sysStats.physicalMemory.freeMB),
                    String.format("%.2f", sysStats.physicalMemory.usagePercent));
        }

        if (sysStats.swapSpace != null) {
            logger.info("Swap Space: Total={} MB | Used={} MB | Free={} MB",
                    String.format("%.2f", sysStats.swapSpace.totalMB),
                    String.format("%.2f", sysStats.swapSpace.usedMB),
                    String.format("%.2f", sysStats.swapSpace.freeMB));
        }

        if (sysStats.cpuLoad != null) {
            logger.info("CPU Load: Process={} % | System={} %",
                    sysStats.cpuLoad.processPercent >= 0 ? String.format("%.2f", sysStats.cpuLoad.processPercent * 100) : "Not available",
                    sysStats.cpuLoad.systemPercent >= 0 ? String.format("%.2f", sysStats.cpuLoad.systemPercent * 100) : "Not available");
        }

        if (sysStats.committedVirtualMemoryMB > 0) {
            logger.info("Committed Virtual Memory: {} MB", String.format("%.2f", sysStats.committedVirtualMemoryMB));
        }

        if (sysStats.uptime != null) {
            logger.info("JVM Uptime: {} ms ({} minutes, {} seconds)",
                    sysStats.uptime.uptimeMs, sysStats.uptime.uptimeMinutes, sysStats.uptime.uptimeSeconds % 60);
        }
    }

    /**
     * Logs class loading statistics from collected data.
     * 
     * @param classStats Class loading statistics to log
     */
    public void logClassLoading(ClassLoadingStats classStats) {
        logger.info("Classes: Loaded={} | Total Loaded={} | Unloaded={}",
                classStats.loadedClassCount, classStats.totalLoadedClassCount, classStats.unloadedClassCount);
    }
}
