package core.framework.impl.log;

import core.framework.impl.log.filter.LogFilter;
import core.framework.impl.log.message.ActionLogMessage;
import core.framework.impl.log.message.PerformanceStatMessage;
import core.framework.impl.log.message.StatMessage;
import core.framework.util.Network;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author neo
 */
public class MessageFactory {
    private static final int MAX_TRACE_LENGTH = 1000000; // 1M

    public static ActionLogMessage actionLog(ActionLog log, String appName, LogFilter filter) {
        var message = new ActionLogMessage();
        message.app = appName;
        message.serverIP = Network.localHostAddress();
        message.id = log.id;
        message.date = log.date;
        message.result = log.result();
        message.refId = log.refId;
        message.elapsed = log.elapsed;
        message.cpuTime = log.cpuTime;
        message.action = log.action;
        message.errorCode = log.errorCode();
        message.errorMessage = log.errorMessage;
        message.context = log.context;
        message.stats = log.stats;
        Map<String, PerformanceStatMessage> performanceStats = new HashMap<>(log.performanceStats.size());
        for (Map.Entry<String, PerformanceStat> entry : log.performanceStats.entrySet()) {
            PerformanceStat stat = entry.getValue();
            var statMessage = new PerformanceStatMessage();
            statMessage.count = stat.count;
            statMessage.totalElapsed = stat.elapsedTime;
            statMessage.readEntries = stat.readEntries;
            statMessage.writeEntries = stat.writeEntries;
            performanceStats.put(entry.getKey(), statMessage);
        }
        message.performanceStats = performanceStats;
        if (log.flushTraceLog()) {
            var builder = new StringBuilder(log.events.size() << 8);  // length * 256 as rough initial capacity
            for (LogEvent event : log.events) {
                String traceMessage = event.logMessage(filter);
                if (builder.length() + traceMessage.length() >= MAX_TRACE_LENGTH) {
                    builder.append(traceMessage, 0, MAX_TRACE_LENGTH - builder.length());
                    builder.append("...(truncated)");
                    break;
                }
                builder.append(traceMessage);
            }
            message.traceLog = builder.toString();
        }
        return message;
    }

    static StatMessage stat(Map<String, Double> stats, String appName) {
        var message = new StatMessage();
        message.id = UUID.randomUUID().toString();
        message.date = Instant.now();
        message.app = appName;
        message.serverIP = Network.localHostAddress();
        message.stats = stats;
        return message;
    }
}