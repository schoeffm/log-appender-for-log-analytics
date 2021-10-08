package de.bender.logging.log4j;

import org.apache.log4j.Logger;

import java.util.Date;

public class LogIngester {

    static Logger logger = Logger.getLogger(LogIngester.class);

    public static void main(String[] args) {
        // ThreadContext.put("id", UUID.randomUUID().toString());
        // ThreadContext.put("who", "Stefan");

        logger.info("This is my first Appender Tests #1");
        logger.debug("This is a debugging Tests " + new Date());
        logger.warn("This is a warning Tests " + Math.random());

        stackup(0, 10);

        // ThreadContext.clearAll();
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
