package org.otherobjects.cms.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LoggerUtils
{
    public static Level getLoggerLevel(Class<?> loggedClass)
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger(loggedClass);
        return logger.getLevel();
    }

    public static void setLoggerLevel(Class<?> loggedClass, Level level)
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger(loggedClass);
        logger.setLevel(level);
    }
}
