package de.bender.logging.log4j.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bender.logging.common.HttpLogTransport;
import de.bender.logging.common.Signature;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogAnalyticsAppenderTest {

    LogAnalyticsAppender underTest;

    @Mock
    HttpLogTransport httpTransport;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    Layout layout;

    @BeforeEach
    void setUp() {
        underTest = prepareSystemUnderTest(null, null, false);
    }

    @Test
    void append_whenAppenderIsNotStarted_willLogAndExit() throws Exception {
        // given
        LogEvent input = Mockito.mock(LogEvent.class);

        // when
        this.underTest.append(input);

        // then
        verify(httpTransport, never()).send(any(Signature.class));
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    void append_whenAppenderHasNoLayoutSet_willPostPreFormattedMessage() throws Exception {
        // given
        final String TEST_OUTPUT = "[{\"test\":\"output\"}]";
        this.underTest.start();
        LogEvent input = prepareEvent();
        when(objectMapper.writeValueAsString(any(LogAnalyticsEvent.class))).thenReturn(TEST_OUTPUT);

        // when
        this.underTest.append(input);

        // then
        verify(httpTransport).send(any(Signature.class));
    }

    // ------------------------------------- private helper / test fixtures ---------------------------------------

    private LogEvent prepareEvent() {
        Message msg = new SimpleMessage("This is a Test");
        return Log4jLogEvent.newBuilder()
                .setLevel(Level.INFO)
                .setMessage(msg)
                .setInstant(new MutableInstant())
                .setLoggerName("TestLogger")
                .setLoggerFqcn("de.bender.TestLogger")
                .setThreadId(4711L)
                .setThreadName("my-test-thread")
                .build();
    }

    private LogAnalyticsAppender prepareSystemUnderTest(final Layout<? extends Serializable> layout, final Filter filter, final boolean startIt) {
        LogAnalyticsAppender result = LogAnalyticsAppender.createAppender("Name", "LogAnalyticsSecret", null, "Log-Analytics-Workspace-Id", layout, filter);
        result.httpTransport = httpTransport;
        result.objectMapper = objectMapper;
        if (startIt) { result.start(); }
        return result;
    }
}