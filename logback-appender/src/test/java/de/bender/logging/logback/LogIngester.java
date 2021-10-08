package de.bender.logging.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.UUID;

public class LogIngester {

    static final Logger logger = LoggerFactory.getLogger(LogIngester.class);


    public static void main(String[] args) {
        MDC.put("id", UUID.randomUUID().toString());
        MDC.put("who", "Stefan");

        logger.info("This is my first Appender Tests #1");
        logger.debug("This is a debugging Tests {}", new Date());
        logger.warn("This is a warning Tests {}", Math.random());

        stackup(0, 10);

        MDC.clear();
    }

    public static void stackup(int currentLevel, int maxLevel) {
        if (currentLevel < maxLevel) {
            stackup(++currentLevel, maxLevel);
        } else {
            try {
                ((String) null).toString();
            } catch (Exception e) {
                logger.error("That went wrong - here's the error" , e);
            }
        }
    }
}
