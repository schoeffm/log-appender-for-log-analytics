package de.bender.logging.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.Date;
import java.util.UUID;

public class LogIngester {

    static final Logger logger = LogManager.getLogger(LogIngester.class);

    public static void main(String[] args) {
        ThreadContext.put("id", UUID.randomUUID().toString());
        ThreadContext.put("who", "Stefan");

        logger.info("This is my first Appender Tests #1");
        logger.debug("This is a debugging Tests {}", new Date());
        logger.warn("This is a warning Tests {}", Math.random());

        stackup(0, 10);

        ThreadContext.clearAll();
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
