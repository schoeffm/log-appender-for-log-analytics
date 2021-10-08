package de.bender.logging.log4j.appender;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;

public class LogAnalyticsEvent {

    private final String message;
    private final String level;
    private final long timestamp;
    private final int levelWeigth;
    private final String logger;
    private final String threadName;
    private final String exceptionMessage;
    private final String exceptionType;
    private final String exceptionStacktrace;
    private final Map<String, String> contextData;
    private final int threadPriority;
    private final long threadId;

    public LogAnalyticsEvent(LogEvent event) {
            Throwable thrown = event.getThrown();

            this.message = event.getMessage().getFormattedMessage();
            this.level = event.getLevel().name();
            this.levelWeigth = event.getLevel().intLevel();
            this.logger = event.getLoggerName();
            this.threadName = event.getThreadName();
            this.threadId = event.getThreadId();
            this.threadPriority = event.getThreadPriority();
            this.timestamp = event.getInstant().getEpochMillisecond();
            this.exceptionMessage = Objects.nonNull(thrown) ? thrown.getMessage() : null;
            this.exceptionType = Objects.nonNull(thrown) ? thrown.getClass().getName() : null;
            this.exceptionStacktrace = Objects.nonNull(thrown) ? Strings.join(Throwables.toStringList(thrown), '\n') : null;
            this.contextData = event.getContextData().toMap();
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLevelWeigth() {
        return levelWeigth;
    }

    public String getLogger() {
        return logger;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public Map<String, String> getContextData() {
        return contextData;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public long getThreadId() {
        return threadId;
    }
}
