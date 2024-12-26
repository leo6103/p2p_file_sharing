package hust.networkprogramming.shared_utils.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {

    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        try {
            // Create a FileHandler to log to a file
            FileHandler fileHandler = new FileHandler("system.log", true);

            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println("Failed to set up the logger: " + e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warning(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void exception(Exception e) {
        logger.log(Level.SEVERE, "Exception occurred", e);
    }

}