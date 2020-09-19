package dynamite;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * A wrapper for the Java logger.
 */
public final class LoggerWrapper {

    private static final String LOG_FILEPATH = "out.log";
    private static final boolean DEBUG_MODE = Config.DEBUG;

    private static Logger instance = null;

    /**
     * Get the default singleton logger.
     *
     * @return the default singleton logger
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = getFileLogger(LOG_FILEPATH);
            instance.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
        }
        return instance;
    }

    /**
     * Get a new logger that logs in stderr for a class.
     *
     * @return a logger that logs in stderr
     */
    public static Logger getStderrLogger() {
        Logger logger = Logger.getGlobal();
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
            consoleHandler.setFormatter(new CustomFormatter());
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Get a new logger that logs in stdout for a class.
     *
     * @return a logger that logs in stdout
     */
    public static Logger getStdoutLogger() {
        Logger logger = Logger.getGlobal();
        try {
            StreamHandler streamHandler = new StreamHandler(System.out, new SimpleFormatter());
            streamHandler.setLevel(Level.FINE);
            logger.addHandler(streamHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Get a new logger that logs in the specified file
     *
     * @param filepath path to the specified file
     * @return a logger that logs in the file
     */
    public static Logger getFileLogger(String filepath) {
        Logger logger = Logger.getLogger(filepath);
        try {
            FileHandler fileHandler = new FileHandler(filepath);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Custom log formatter.
     */
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(record.getLevel()).append(": ");
            buffer.append(record.getMessage());
            buffer.append(System.lineSeparator());
            return buffer.toString();
        }
    }
}
