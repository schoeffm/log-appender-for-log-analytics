package de.bender.logging.log4j.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bender.logging.common.HttpLogTransport;
import de.bender.logging.common.Signature;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.JsonLayout;

import java.io.Serializable;
import java.util.Objects;

/**
 * log4j2 {@link org.apache.logging.log4j.core.Appender} that sends all log-events to the configured
 * log-analytics workspace (see
 * <a href="https://docs.microsoft.com/en-us/azure/azure-monitor/logs/data-collector-api">the data collection API</a>
 * for more information).<p/>
 * When configuring the de.bender.logging.log4j.appender you have to provide both the <code>workspaceId</code> as well as
 * the <code>sharedSecret</code> of your Log-Analytics Workspace. Since the latter should be kept safe it can be
 * provided as an environment variable where you only have to configure the name of that variable on your
 * de.bender.logging.log4j.appender-configuration.<p/>
 * Since the API-endpoint expects a JSON-formatted payload the usage of {@link Layout} is limited - when nothing is
 * configured the {@link LogAnalyticsAppender} will take care of the formatting for you.<br/>
 * Still, it is possible to define a custom {@link JsonLayout} which is then picked up and used for formatting the
 * payload that is finally sent to MSFT API (be aware thought that this might lead to errors - use the
 * <code>status="debug"</code> flag to get more debugging output when you have issues).<br/>
 * Any other {@link Layout}-implementation (i.e. {@link org.apache.logging.log4j.core.layout.HtmlLayout}) isn't
 * supported and any configuration wouldn't lead to an exception but is skipped.
 */
@Plugin(name = "LogAnalyticsAppender",
        category = "Core",
        elementType = "appender",
        printObject = true)
public class LogAnalyticsAppender extends AbstractAppender {

    String workspaceId;
    String sharedSecret;
    ObjectMapper objectMapper;
    HttpLogTransport httpTransport;

    LogAnalyticsAppender(String name,
                         String sharedSecret,
                         String sharedSecretEnvironmentVariableName,
                         String workspaceId,
                         Layout<? extends Serializable> layout,
                         Filter filter) {
        super(name, filter, layout, false, Property.EMPTY_ARRAY);
        this.sharedSecret = Objects.requireNonNull(
                determineSecret(sharedSecretEnvironmentVariableName, sharedSecret),
                "Missconfiguration of LogAnalyticsAppender - either 'sharedSecret' or 'sharedSecretEnvironmentVariableName' has to be defined");
        this.workspaceId = workspaceId;

        this.httpTransport = new HttpLogTransport(workspaceId, name);
        this.objectMapper = new ObjectMapper();
    }

    private String determineSecret(String sharedSecretEnvironmentVariableName, String sharedSecret) {
        return Objects.nonNull(sharedSecretEnvironmentVariableName)
                ? System.getenv(sharedSecretEnvironmentVariableName)
                : sharedSecret;
    }

    @PluginFactory
    public static LogAnalyticsAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("sharedSecret") String sharedSecret,
            @PluginAttribute("sharedSecretEnvVariable") String sharedSecretEnvironmentVariableName,
            @PluginAttribute("workspaceId") String workspaceId,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {
        return new LogAnalyticsAppender(name, sharedSecret, sharedSecretEnvironmentVariableName, workspaceId, layout, filter);
    }

    @Override
    public void append(LogEvent logEvent) {
        if (!this.isStarted()) {
            LOGGER.warn("Appender not initialized yet ...");
            return;
        }

        try {
            String logMessage;
            if (Objects.nonNull(this.getLayout()) && JsonLayout.class.isAssignableFrom(this.getLayout().getClass())) {
                logMessage = new String(this.getLayout().toByteArray(logEvent));
            } else {
                logMessage = this.objectMapper.writeValueAsString(new LogAnalyticsEvent(logEvent));
            }

            LOGGER.debug(logMessage);

            httpTransport.send(new Signature(logMessage, this.workspaceId, this.sharedSecret));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
