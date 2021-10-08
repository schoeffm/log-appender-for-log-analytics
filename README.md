# Log-Analytics Appender

Although still in preview (at the time of this writing by the end of 2021) [MSFT Azure offers an HTTP based Data Collector API][msft-api] to ingest log data into [LogAnalytics][log-analytics] (the corner stone service when it comes to storing log'n'metric information). 

Several services offer direct integration to LogAnalytics (i.e. Container based services) and still there are situations where direct support doesn't exist. In those situations a log-appender that carries logs-events to LogAnalytics via the mentioned API comes handy.   

## Log4j2

Importing the dependency into you project:
```xml
TODO: POM
```
Next, configure the `LogAnalyticsAppender` - you can either use the build-in formatter or configure a `JsonLayout`.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration packages="com.microsoft.applicationinsights.log4j.v2">
    <Appenders>
        <LogAnalyticsAppender name="LogAnalyticsAppender" 
                              workspaceId="b3110671-decf-444c-9fc0-0fd0e63d6f5b" 
                              sharedSecretEnvVariable="workspaceSecret"/>
        <LogAnalyticsAppender name="LogAnalyticsAppenderCustomLayout" 
                              workspaceId="b3110671-decf-444c-9fc0-0fd0e63d6f5b" 
                              sharedSecretEnvVariable="workspaceSecret">
            <JsonLayout complete="false" compact="false">
                <KeyValuePair key="StudytonightField" value="studytonightValue" />
            </JsonLayout>
        </LogAnalyticsAppender>
    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="LogAnalyticsAppenderTest" />
        </Root>
    </Loggers>
</Configuration>
```
For security reasons the `SharedSecret` can be provided as an environment variable - the configuration only references the name of the variable that holds the value of the secret. 

## Log4j

Importing the dependency into you project:
```xml
TODO: POM
```

Next, configure the `LogAnalyticsAppender`:

```xml
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration debug="true" xmlns:log4j='http://jakarta.apache.org/log4j/'>
    <appender name="logAnalyticsAppender" class="de.bender.logging.log4j.appender.LogAnalyticsAppender">
        <param name="workspaceId" value="b3110671-decf-444c-9fc0-0fd0e63d6f5b" />
        <param name="sharedSecretEnvVariable" value="workspaceSecret" />
    </appender>
    <root>
        <priority value ="info"></priority>
        <appender-ref ref="logAnalyticsAppender"></appender-ref>
    </root>
</log4j:configuration>
```
For security reasons the `SharedSecret` can be provided as an environment variable - the configuration only references the name of the variable that holds the value of the secret.


## Logback

Importing the dependency into you project:
```xml
TODO: POM
```

Next, configure the `LogAnalyticsAppender`:

```xml
<configuration>
    <appender name="logbackAnalytics" class="de.bender.logging.logback.appender.LogAnalyticsAppender">
        <workspaceId>b3110671-decf-444c-9fc0-0fd0e63d6f5b</workspaceId>
        <sharedSecretEnvVariable>workspaceSecret</sharedSecretEnvVariable>
    </appender>
    <root level="info">
        <appender-ref ref="logbackAnalytics"/>
    </root>d
</configuration>
```
For security reasons the `SharedSecret` can be provided as an environment variable - the configuration only references the name of the variable that holds the value of the secret.

[msft-api]:https://docs.microsoft.com/en-us/azure/azure-monitor/logs/data-collector-api
[log-analytics]:https://docs.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-overview