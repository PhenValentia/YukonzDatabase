/**
 * The entry point into the Yuconz system.
 * This is the class that should be run to execute the program.
 * No other classes should have public methods, only package-private (until further notice).
 * @author Marin md485
 * @version 20190217
 */
public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(AppLogger::shutdown));
        AppLogger.startLogging();
        AppController app = new AppController();
        app.run();
    }
}
