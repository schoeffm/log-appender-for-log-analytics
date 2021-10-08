package de.bender.logging.logback.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bender.logging.common.HttpLogTransport;
import de.bender.logging.common.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogAnalyticsAppenderTest {

    LogAnalyticsAppender underTest;

    @Mock
    HttpLogTransport httpTransport;

    @Mock
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        underTest = prepareSystemUnderTest(false);
    }

    @Test
    void append_whenAppenderIsNotStarted_willLogAndExit() throws Exception {
        // given
        ILoggingEvent input = Mockito.mock(ILoggingEvent.class);

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
        underTest = prepareSystemUnderTest(true);
        ILoggingEvent input = prepareEvent();
        when(objectMapper.writeValueAsString(any(LogAnalyticsEvent.class))).thenReturn(TEST_OUTPUT);

        // when
        this.underTest.append(input);

        // then
        verify(httpTransport).send(any(Signature.class));
    }

    // ------------------------------------- private helper / test fixtures ---------------------------------------

    private ILoggingEvent prepareEvent() {
        LoggingEvent mock = mock(LoggingEvent.class);
        when(mock.getLevel()).thenReturn(Level.INFO);
        when(mock.getLoggerName()).thenReturn("TestLogger");
        when(mock.getFormattedMessage()).thenReturn("this is a test");
        when(mock.getThreadName()).thenReturn("main");
        when(mock.getTimeStamp()).thenReturn(12345678L);
        when(mock.getThrowableProxy()).thenReturn(null);
        return mock;
    }

    private LogAnalyticsAppender prepareSystemUnderTest(final boolean startIt) {
        LogAnalyticsAppender result = new LogAnalyticsAppender();
        result.setSharedSecret("LogAnalyticsSecret");
        result.setName("Name");
        result.setWorkspaceId("Log-Analytics-Workspace-Id");

        if (startIt) { result.start(); }
        result.httpTransport = httpTransport;
        result.objectMapper = objectMapper;
        return result;
    }
}