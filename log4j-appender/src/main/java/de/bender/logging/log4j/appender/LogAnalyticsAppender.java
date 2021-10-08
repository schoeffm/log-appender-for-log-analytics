package de.bender.logging.log4j.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bender.logging.common.HttpLogTransport;
import de.bender.logging.common.Signature;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Objects;

public class LogAnalyticsAppender extends AppenderSkeleton {

    private String sharedSecret;
    private String sharedSecretEnvVariable;
    private String workspaceId;
    private String secret;
    ObjectMapper objectMapper;
    HttpLogTransport httpTransport;
    boolean isInitialized = false;

    public String getSharedSecretEnvVariable() {
        return sharedSecretEnvVariable;
    }

    public void setSharedSecretEnvVariable(String sharedSecretEnvVariable) {
        this.sharedSecretEnvVariable = sharedSecretEnvVariable;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    protected void append(LoggingEvent event) {
        // it's not harmful if that happens several times ... that's just meant to reduce unnecessary work
        if (! isInitialized) {
            this.secret = Objects.requireNonNull(
                    determineSecret(this.sharedSecretEnvVariable, this.sharedSecret),
                    "Missconfiguration of LogAnalyticsAppender - either 'sharedSecret' or 'sharedSecretEnvironmentVariableName' has to be defined");
            this.httpTransport = new HttpLogTransport(workspaceId, name);
            this.objectMapper = new ObjectMapper();
            isInitialized = true;
        }

        try {
            String logMessage = this.objectMapper.writeValueAsString(new LogAnalyticsEvent(event));
            httpTransport.send(new Signature(logMessage, this.workspaceId, this.secret));
        } catch (Exception e) {
            System.err.println("LogAnalyticsAppender: Couldn't send log-message to Log-Analytics: " + e);
        }
    }

    public void close() {
        // nothing to do here
    }

    public boolean requiresLayout() {
        return false;
    }

    private String determineSecret(String sharedSecretEnvironmentVariableName, String sharedSecret) {
        return Objects.nonNull(sharedSecretEnvironmentVariableName)
                ? System.getenv(sharedSecretEnvironmentVariableName)
                : sharedSecret;
    }
}
