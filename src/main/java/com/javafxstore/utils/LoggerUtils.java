package com.javafxstore.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LoggerUtils Class
 * Logs messages based on priority to file
 * @version 0.1
 */
public class LoggerUtils {

    private static final Logger logger = Logger.getLogger(LoggerUtils.class.getName());

    /**
     * Open a new file for writing formatted messages with current date
     * Implemented using error handling
     * @param msg used for logger information
     * @param level used to control log output level
     */
    public void log(String msg, Level level) {
        FileHandler file;

        try {
            file = new FileHandler("log.txt", true);
            logger.addHandler(file);
            SimpleFormatter sf = new SimpleFormatter();
            file.setFormatter(sf);
            logger.setLevel(Level.parse(level.getName()));
            logger.log(Level.parse(level.getName()), msg);
            file.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"Error: " + ex);
        }
    }
}
