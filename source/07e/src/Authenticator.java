import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Creates authentication logs, checks provided details when users log in,
 * and logs users out when users log out.
 * Currently stores details as a HashMap for mockup purposes.
 * Currently stores unhashed passwords for mockup purposes.
 * @author James jd556, Marin md485
 * @version 20190301
 */
class Authenticator {

    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());
    private static HashSet<Session> activeSessions = new HashSet<>();

    /**
     * Checks whether the specified user can successfully authenticate as the specified role.
     * This checks whether the username is stored within the authentication database,
     * then checks whether the stored password is equal to the provided password,
     * then checks whether that user has permission to log in as the specified role.
     * Regardless of result, the authentication log is updated with a new record.
     * Authenticate returns a new Session with the provided username, role and an exit code
     * based on whether the authentication succeeded or failed.
     * @param username The username to check
     * @param password The password to check
     * @param role The role requested by the user
     * @return A new Session with the provided username, role and an exit code
     */
    static Session authenticate(String username, String password, Role role) {
        ExitCode exitCode;
        if(!correctPassword(username, password)) {
            //Failure code 1: Incorrect username or password.
            exitCode = ExitCode.INVALID_LOGIN;
        } else if(!validRole(username, role)) {
            //Failure code 2: This user does not have permission for this role.
            exitCode = ExitCode.INVALID_ROLE;
        } else {
            //Authentication success.
            exitCode = ExitCode.LOGIN_SUCCESS;
        }
        long loginTime = System.currentTimeMillis();
        Session retSession = new Session(loginTime, username, role, exitCode);
        activeSessions.add(retSession);

        logAuthCheck(retSession);
        //System.err.println("Exit code is " + retSession.getExitCode());
        return retSession;
    }

    /**
     * Ends the given session, and destroys the session.
     * @param activeSession The session that should be ended.
     * @return Whether the session was successfully removed from activeSessions.
     */
    static boolean logout(Session activeSession) {
        boolean success = activeSessions.remove(activeSession);
        activeSession.destroySession();
        return success;
    }

    /**
     * Checks the supplied username and password against the stored password for that user.
     * @param username The username to lookup the password for.
     * @param password The password to check against the stored password.
     * @return Whether the provided password is equal to the stored password for that user.
     */
    private static boolean correctPassword(String username, String password) {
        return password.equals(getPassword(username));
    }

    /**
     * Gets the stored password for the provided username.
     * If the username is not found, returns null instead.
     * Passwords are currently not hashed for testing and demonstration purposes only.
     * @param username The username to lookup the password for.
     * @return The specified username's password, or null if not found.
     */
    private static String getPassword(String username) {
        String[] fields = DatabaseController.getAuthData(username);

        if(fields != null) {
            return fields[0];
        } else {
            return null;
        }
    }

    /**
     * Checks whether the supplied username has the permissions of the supplied role.
     * @param username The username to be checked.
     * @param role The role to be checked.
     * @return Whether the user has said role.
     */
    private static boolean validRole(String username, Role role) {
        Integer roleIndex = role.ordinal();

        //Looks up username, if role found under "roles" field for that username, return true.
        String[] fields = DatabaseController.getAuthData(username);
        if (fields != null) {
            return fields[1].contains(roleIndex.toString());
        } else {
            return false;
        }
    }

    /**
     * Creates a log for this authentication check.
     * Records are of the form timestamp, username, role, exitCode.
     * If the log file cannot be found at the expected location,
     *  create the file before appending this record.
     * @param createdSession The session holding the details to append to the log.
     */
    private static void logAuthCheck(Session createdSession) {
        StringBuilder logContent = new StringBuilder();
        logContent.append(createdSession.getLoginTime());
        logContent.append(",");
        logContent.append(createdSession.getUsername());
        logContent.append(",");
        logContent.append(createdSession.getRole());
        logContent.append(",");
        logContent.append(createdSession.getExitCode().name());

        File path = new File("log/AuthenticationLog.txt").getAbsoluteFile();
        //Make the log directory if it doesn't already exist
        path.getParentFile().mkdirs();
        try {
            //Make the log file if it doesn't already exist
            path.createNewFile();
            //Append timestamp and info
            Files.write(path.toPath(), Arrays.asList(logContent.toString()),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException when logging the Authentication Check.");
        }
    }
}
