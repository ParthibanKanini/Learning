package pc.jvm.monitor;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pc.jvm.dto.ClassLoadingStats;
import pc.jvm.dto.CpuLoadStats;
import pc.jvm.dto.GarbageCollectionStats;
import pc.jvm.dto.GcCollectorStats;
import pc.jvm.dto.HeapMemoryStats;
import pc.jvm.dto.JvmInfo;
import pc.jvm.dto.MemoryPoolStats;
import pc.jvm.dto.MemoryStatistics;
import pc.jvm.dto.NonHeapMemoryStats;
import pc.jvm.dto.PhysicalMemoryStats;
import pc.jvm.dto.SwapSpaceStats;
import pc.jvm.dto.SystemStatistics;
import pc.jvm.dto.ThreadCounts;
import pc.jvm.dto.ThreadStates;
import pc.jvm.dto.ThreadStatistics;
import pc.jvm.dto.UptimeStats;

/**
 * A daemon thread that periodically monitors and logs JVM memory and system
 * statistics.
 *
 * This thread provides detailed insights into: - Heap memory usage (used,
 * committed, max) - Non-heap memory usage (Metaspace, Code Cache, etc.) -
 * Garbage collection (collection counts and times) - Thread count and states -
 * System stats (CPU load, physical memory, swap space, - Memory pool details
 */
public class MemoryMonitorThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MemoryMonitorThread.class);
    private static final long DEFAULT_INTERVAL_MS = 30_000; // 30 seconds
    private static final double BYTES_TO_MB = 1024.0 * 1024.0;

    private final long intervalMs;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean finalLogRequested = new AtomicBoolean(false);
    private final MemoryStatsLogger statsLogger = new MemoryStatsLogger();

    /**
     * Creates a memory monitor thread with default 30-second interval.
     */
    public MemoryMonitorThread() {
        this(DEFAULT_INTERVAL_MS);
    }

    /**
     * Creates a memory monitor thread with specified interval.
     *
     * @param intervalMs Interval between memory stats logging in milliseconds
     */
    public MemoryMonitorThread(long intervalMs) {
        super("MemoryMonitor");
        this.intervalMs = intervalMs;
        setDaemon(true); // Daemon thread won't prevent JVM shutdown
    }

    @Override
    public void run() {
        logger.info("Memory Monitor Thread started (logging every {} seconds)", intervalMs / 1000);
        logMemoryStats("Initial");

        while (running.get()) {
            try {
                Thread.sleep(intervalMs);
                if (running.get()) {
                    logMemoryStats("Periodic");
                }
            } catch (InterruptedException e) {
                logger.debug("Memory Monitor Thread interrupted");
                break;
            }
        }

        if (finalLogRequested.get()) {
            logMemoryStats("Final");
        }
        logger.info("Memory Monitor Thread stopped");
    }

    /**
     * Requests the thread to stop and logs final statistics.
     */
    public void requestFinalLog() {
        finalLogRequested.set(true);
        running.set(false);
        this.interrupt();
    }

    /**
     * Logs comprehensive memory and system statistics.
     *
     * @param logType Type of log (Initial, Periodic, Final)
     */
    private void logMemoryStats(String logType) {
        logger.info("========== Memory Statistics [{}] ==========", logType);

        // Collect all statistics into DTO
        MemoryStatistics stats = new MemoryStatistics();
        stats.logType = logType;
        stats.timestamp = System.currentTimeMillis();

        // 0. JVM Information
        stats.jvmInfo = collectJvmInfo();

        // 1. Heap Memory Statistics (with pools)
        stats.heapMemory = collectHeapMemoryStats();
        statsLogger.logMemoryUsage("HEAP", stats.heapMemory.initMB, stats.heapMemory.usedMB,
                stats.heapMemory.committedMB, stats.heapMemory.maxMB, stats.heapMemory.usagePercent);

        // 2. Non-Heap Memory Statistics (with pools)
        stats.nonHeapMemory = collectNonHeapMemoryStats();
        statsLogger.logMemoryUsage("NON-HEAP", -1, stats.nonHeapMemory.usedMB,
                stats.nonHeapMemory.committedMB, stats.nonHeapMemory.maxMB, stats.nonHeapMemory.usagePercent);

        // 3. Memory Pools (detailed breakdown)
        statsLogger.logMemoryPools(stats.heapMemory.pools);
        statsLogger.logMemoryPools(stats.nonHeapMemory.pools);

        // 4. Garbage Collection Statistics
        stats.garbageCollection = collectGarbageCollectionStats();
        statsLogger.logGarbageCollection(stats.garbageCollection);

        // 5. Thread Statistics
        stats.threads = collectThreadStats();
        statsLogger.logThreads(stats.threads);

        // 6. System Statistics
        stats.system = collectSystemStats();
        statsLogger.logSystem(stats.system);

        // 7. Class Loading Statistics
        stats.classLoading = collectClassLoadingStats();
        statsLogger.logClassLoading(stats.classLoading);

        logger.info("================================================");

        // Log JSON representation
        JSONObject json = stats.toJSON();
        logger.info("Memory Statistics JSON: \n{}", json.toString(2));
    }

    // ================ Utility Methods ================
    /**
     * Converts bytes to megabytes.
     */
    private double toMB(long bytes) {
        return bytes / BYTES_TO_MB;
    }

    // ================ Data Collection Methods for DTO ================
    private JvmInfo collectJvmInfo() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        JvmInfo info = new JvmInfo();
        info.jvmName = runtimeBean.getVmName();
        info.jvmVersion = runtimeBean.getVmVersion();
        info.jvmVendor = runtimeBean.getVmVendor();
        info.startTime = runtimeBean.getStartTime();
        info.pid = runtimeBean.getPid();

        return info;
    }

    private HeapMemoryStats collectHeapMemoryStats() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        /*
         * heapInit: Initial baseline heap size (in bytes) that the JVM requests from the OS at startup.
         * - Corresponds to the -Xms JVM parameter (e.g., -Xms512m sets initial heap to 512MB)
         * - Returns -1 if the initial size is undefined
         * - This is the starting point; JVM can request more memory from OS as needed
         */
        long heapInit = heapUsage.getInit();

        /*
         * heapUsed: Amount of memory (in bytes) currently occupied by live Java objects in application.
         * - Includes all reachable objects that haven't been garbage collected
         * - This value fluctuates as objects are created and GC runs
         * - Does NOT include memory reserved but not yet used
         * - If heapUsed is close to heapMax, you may see OutOfMemoryError soon
         * - If heapUsed << heapCommitted, there's buffer room before needing more OS memory
         */
        long heapUsed = heapUsage.getUsed();

        /*
         * heapCommitted: Memory (in bytes) that is guaranteed/given to be available for the JVM.
         * - The OS has allocated this physical/virtual memory to the JVM process
         * - Always satisfies: heapUsed <= heapCommitted <= heapMax
         * - JVM can use this memory without requesting more from the OS
         * - When heapUsed approaches heapCommitted, JVM may request more memory from OS
         * - The JVM dynamically grows heapCommitted from heapInit up to heapMax as needed
         * - Gap between heapCommitted and heapMax shows potential for growth without OS interaction
         */
        long heapCommitted = heapUsage.getCommitted();

        /*
         * heapMax: Maximum heap size (in bytes) that the JVM can allocate. Application's memory ceiling
         * - Corresponds to the -Xmx JVM parameter (e.g., -Xmx2g sets max heap to 2GB)
         * - Hard limit - if exceeded, OutOfMemoryError is thrown
         * - Returns -1 if the maximum size is undefined (rare, platform-dependent)
         * - Relationship: heapInit <= heapCommitted <= heapUsed <= heapMax
         * - Setting -Xms equal to -Xmx prevents dynamic heap resizing (more predictable, faster startup)
         */
        long heapMax = heapUsage.getMax();

        HeapMemoryStats stats = new HeapMemoryStats();
        stats.initMB = toMB(heapInit);
        stats.usedMB = toMB(heapUsed);
        stats.committedMB = toMB(heapCommitted);
        stats.maxMB = toMB(heapMax);
        stats.usagePercent = (heapUsed * 100.0) / heapMax;

        // Collect heap pools
        stats.pools = collectMemoryPoolsByType(java.lang.management.MemoryType.HEAP);

        return stats;
    }

    /**
     * Collects non-heap memory statistics. Non-heap memory is allocated outside
     * the Java heap from native OS memory.
     *
     * Non-heap memory includes: - Metaspace: Class metadata, method data,
     * runtime constant pool (replaced PermGen in Java 8+) - Code Cache:
     * JIT-compiled native code - Thread stacks: Native memory for thread call
     * stacks - JVM Internal structures: Symbol table, JNI data structures - GC
     * internal data structures
     *
     * Most non-heap memory is not managed by GC, except Metaspace which can be
     * garbage collected when classes are unloaded.
     */
    private NonHeapMemoryStats collectNonHeapMemoryStats() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        NonHeapMemoryStats stats = new NonHeapMemoryStats();
        stats.usedMB = toMB(nonHeapUsage.getUsed());
        stats.committedMB = toMB(nonHeapUsage.getCommitted());

        long maxBytes = nonHeapUsage.getMax();
        if (maxBytes != -1) {
            stats.maxMB = toMB(maxBytes);
            stats.usagePercent = (nonHeapUsage.getUsed() * 100.0) / maxBytes;
        } else {
            stats.maxMB = -1;
            stats.usagePercent = -1;
        }

        // Collect non-heap pools
        stats.pools = collectMemoryPoolsByType(java.lang.management.MemoryType.NON_HEAP);

        return stats;
    }

    private List<MemoryPoolStats> collectMemoryPoolsByType(java.lang.management.MemoryType type) {
        List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
        List<MemoryPoolStats> poolStatsList = new ArrayList<>();

        for (MemoryPoolMXBean pool : memoryPools) {
            // Filter by memory type
            if (pool.getType() != type) {
                continue;
            }

            MemoryUsage usage = pool.getUsage();
            if (usage != null) {
                MemoryPoolStats poolStats = new MemoryPoolStats();
                poolStats.name = pool.getName();
                poolStats.type = pool.getType().toString();
                poolStats.usedMB = toMB(usage.getUsed());
                poolStats.committedMB = toMB(usage.getCommitted());

                long max = usage.getMax();
                if (max != -1) {
                    poolStats.maxMB = toMB(max);
                    poolStats.usagePercent = (usage.getUsed() * 100.0) / max;
                } else {
                    poolStats.maxMB = -1;
                    poolStats.usagePercent = -1;
                }

                poolStatsList.add(poolStats);
            }
        }

        return poolStatsList;
    }

    private GarbageCollectionStats collectGarbageCollectionStats() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        GarbageCollectionStats gcStats = new GarbageCollectionStats();
        gcStats.collectors = new ArrayList<>();

        long totalGcCount = 0;
        long totalGcTime = 0;

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            GcCollectorStats collector = new GcCollectorStats();
            collector.name = gcBean.getName();
            collector.collectionCount = gcBean.getCollectionCount();
            collector.collectionTimeMs = gcBean.getCollectionTime();

            if (collector.collectionCount > 0) {
                collector.avgCollectionTimeMs = collector.collectionTimeMs / (double) collector.collectionCount;
            }

            totalGcCount += collector.collectionCount;
            totalGcTime += collector.collectionTimeMs;

            gcStats.collectors.add(collector);
        }

        gcStats.totalCollectionCount = totalGcCount;
        gcStats.totalCollectionTimeMs = totalGcTime;

        return gcStats;
    }

    private ThreadStatistics collectThreadStats() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        ThreadStatistics stats = new ThreadStatistics();
        stats.counts = new ThreadCounts();
        stats.counts.current = threadBean.getThreadCount();
        stats.counts.peak = threadBean.getPeakThreadCount();
        stats.counts.totalStarted = threadBean.getTotalStartedThreadCount();
        stats.counts.daemon = threadBean.getDaemonThreadCount();

        // Thread states breakdown
        long[] threadIds = threadBean.getAllThreadIds();
        stats.states = new ThreadStates();

        for (long threadId : threadIds) {
            ThreadInfo threadInfo = threadBean.getThreadInfo(threadId);
            if (threadInfo != null) {
                Thread.State state = threadInfo.getThreadState();
                switch (state) {
                    case RUNNABLE ->
                        stats.states.runnable++;
                    case BLOCKED ->
                        stats.states.blocked++;
                    case WAITING ->
                        stats.states.waiting++;
                    case TIMED_WAITING ->
                        stats.states.timedWaiting++;
                    case NEW ->
                        stats.states.newState++;
                    case TERMINATED ->
                        stats.states.terminated++;
                }
            }
        }

        // Deadlock detection
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();
        stats.deadlockedThreadCount = (deadlockedThreads != null) ? deadlockedThreads.length : 0;

        return stats;
    }

    private SystemStatistics collectSystemStats() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        SystemStatistics stats = new SystemStatistics();
        stats.availableProcessors = osBean.getAvailableProcessors();
        stats.systemLoadAverage = osBean.getSystemLoadAverage();

        // Extended OS statistics (if available)
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {

            // Physical Memory
            stats.physicalMemory = new PhysicalMemoryStats();
            long totalPhysicalMemory = sunOsBean.getTotalMemorySize();
            long freePhysicalMemory = sunOsBean.getFreeMemorySize();
            long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;

            stats.physicalMemory.totalMB = toMB(totalPhysicalMemory);
            stats.physicalMemory.freeMB = toMB(freePhysicalMemory);
            stats.physicalMemory.usedMB = toMB(usedPhysicalMemory);
            stats.physicalMemory.usagePercent = (usedPhysicalMemory * 100.0) / totalPhysicalMemory;

            // Swap Space
            long totalSwapSpace = sunOsBean.getTotalSwapSpaceSize();
            long freeSwapSpace = sunOsBean.getFreeSwapSpaceSize();

            if (totalSwapSpace > 0) {
                stats.swapSpace = new SwapSpaceStats();
                stats.swapSpace.totalMB = toMB(totalSwapSpace);
                stats.swapSpace.freeMB = toMB(freeSwapSpace);
                stats.swapSpace.usedMB = toMB(totalSwapSpace - freeSwapSpace);
            }

            // CPU Load
            stats.cpuLoad = new CpuLoadStats();
            stats.cpuLoad.processPercent = sunOsBean.getProcessCpuLoad();
            stats.cpuLoad.systemPercent = sunOsBean.getCpuLoad();

            // Committed Virtual Memory
            long committedVirtualMemory = sunOsBean.getCommittedVirtualMemorySize();
            if (committedVirtualMemory > 0) {
                stats.committedVirtualMemoryMB = toMB(committedVirtualMemory);
            }
        }

        // Runtime information
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        stats.uptime = new UptimeStats();
        stats.uptime.uptimeMs = runtimeBean.getUptime();
        stats.uptime.uptimeSeconds = stats.uptime.uptimeMs / 1000;
        stats.uptime.uptimeMinutes = stats.uptime.uptimeSeconds / 60;

        return stats;
    }

    private ClassLoadingStats collectClassLoadingStats() {
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();

        ClassLoadingStats stats = new ClassLoadingStats();
        stats.loadedClassCount = classLoadingBean.getLoadedClassCount();
        stats.totalLoadedClassCount = classLoadingBean.getTotalLoadedClassCount();
        stats.unloadedClassCount = classLoadingBean.getUnloadedClassCount();

        return stats;
    }

}
