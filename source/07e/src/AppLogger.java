import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *  A logger class which formats and stores application logs for the running operation of our
 *  program. Currently globally manages the logger levels for all classes and stores them
 *  within an external log.
 * @author MD485
 * @version 20190310
 */
class AppLogger {
    //The file logger used to write the logs to a file.
    private static FileHandler fileLogger;
    private static ConsoleHandler consoleLogger;
    //The theoretical time the application starts. It's roughly 20ms too slow on average,
    //I believe this is because this class gets initialised when it's used and that's
    //some ms after the app controller gets initialized.
    private final static long INIT_TIME = System.currentTimeMillis();

    /*
      A static block initialising the main logger for the class.
     */
    static {
        //Initializes the file logger to log to a specific location,
        // if a file already exists in that location, it will append to it.
        try {
            fileLogger = new FileHandler(
                    System.getProperty("user.home") + "\\yuconzLogs.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        consoleLogger = new ConsoleHandler();
        consoleLogger.setLevel(Level.FINEST);

        //Makes sure the logger follows a specialised format, to be human readable if required.
        //Stores things in the format:
        //XXXXms : {ClassName} : {Log.Level} : Message
        Formatter format = new Formatter() {
            @Override
            public String format(LogRecord record) {
                String fString = "";
                fString += record.getMillis() - INIT_TIME + "ms : ";
                fString += record.getLoggerName() + " : ";
                fString += record.getLevel() + " : ";
                fString += record.getMessage() + ".\n";
                return fString;
            }
        };

        //Appends to the file.
        fileLogger.setFormatter(format);
        //consoleLogger.setFormatter(format);
    }

    /**
     * A method used to configure all loggers used by our program globally.
     * @param logger A logger containing class logs we wish to store.
     */
    static void addHandler(Logger logger) {
        //Gets the name of the logger passed into it, then assigns it a logger level,
        // logger levels are used to specify the severity level of log messages that are logged.
        switch (logger.getName()) {
            case "AppController":
                logger.setLevel(Level.INFO);
                break;
            case "Authenticator":
                logger.setLevel(Level.INFO);
                break;
            case "Authoriser":
                logger.setLevel(Level.INFO);
                break;
            case "DatabaseController":
                logger.setLevel(Level.INFO);
                break;
            case "PersonalDetails":
                logger.setLevel(Level.INFO);
                break;
            case "Session":
                logger.setLevel(Level.INFO);
                break;
        }

        //Logs are by default printed to system.err output, this is because the global logger
        // by default prints messages to system.err and all loggers are initialised as children
        // of the parent logger, to prevent this behaviour setUseParentHandlers is set to false.
        logger.setUseParentHandlers(false);
        //This appends all loggers passed into this method to the file logger, meaning they'll
        // be written to the log file specified by the fileLogger instance.
        logger.addHandler(fileLogger);
        logger.addHandler(consoleLogger);
    }

    /**
     * A method used with systemHooks to make sure that files aren't left open if the program
     * closes unexpectedly.
     */
    static void shutdown() {
        fileLogger.close();
    }

    static void startLogging() {
        fileLogger.publish(new LogRecord(Level.INFO, "Program started at : " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + ".\n"));
    }
}
