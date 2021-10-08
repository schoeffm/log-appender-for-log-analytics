package de.bender.logging.log4j.appender;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Arrays;
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

    public LogAnalyticsEvent(LoggingEvent event) {
        ThrowableInformation thrown = event.getThrowableInformation();

        this.message = event.getRenderedMessage();
        this.level = event.getLevel().toString();
        this.levelWeigth = event.getLevel().getSyslogEquivalent();
        this.logger = event.getLoggerName();
        this.threadName = event.getThreadName();
        this.timestamp = event.getTimeStamp();

        this.exceptionMessage = Objects.nonNull(thrown) ? thrown.getThrowable().getMessage() : null;
        this.exceptionType = Objects.nonNull(thrown) ? thrown.getThrowable().getClass().getTypeName() : null;
        this.exceptionStacktrace = Objects.nonNull(thrown) ? String.join("\n", Arrays.asList(thrown.getThrowableStrRep())) : null;
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

}
