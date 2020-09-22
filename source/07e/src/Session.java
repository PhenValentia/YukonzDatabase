import java.util.logging.Logger;

/**
 * A session that holds information on a logged-in user.
 * After a session is created, it can only be destroyed, not changed.
 * Stores the login timestamp, logged-in user, authenticated role, and exit code.
 * If the exit code is LOGIN_SUCCESS, then it's a valid session.
 * Otherwise it cannot be used to authorise an action.
 * When a session is destroyed, the exit code is set to LOGGED_OUT (Session ended).
 * @author James jd556, Marin md485
 * @version 20190217
 */
public class Session {
    private Long timestamp;
    private String username;
    private Role role;
    private ExitCode exitCode;
    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());

    /**
     * Creates an invalid session.
     * This is the default session when a client first connects, before logging in.
     */
    Session() {
        this.destroySession();
    }

    /**
     * Creates a session with the supplied details.
     * Validity of the session is based on the supplied exitCode.
     * A session with exit code LOGIN_SUCCESS is valid. All others are invalid.
     * @param loginTime The time when the user was authenticated.
     * @param username The authenticated user.
     * @param role The role which the user was authenticated as.
     * @param exitCode The exit code during authentication.
     */
    Session(long loginTime, String username, Role role, ExitCode exitCode) {
        timestamp = loginTime;
        this.username = username;
        this.role = role;
        this.exitCode = exitCode;
    }

    /**
     * Returns the time the user was authenticated.
     * Should be checked before actions are carried out if sessions can time out.
     * @return When the user was authenticated.
     */
    long getLoginTime() {
        return timestamp;
    }

    /**
     * Returns the name of the authenticated user.
     * @return The authenticated user's name.
     */
    String getUsername() {
        return username;
    }

    /**
     * Returns the role the user has been authenticated as.
     * @return The authenticated user's role in this session.
     */
    Role getRole() {
        return role;
    }

    /**
     * Returns the exit code produced during authentication,
     *  or LOGGED_OUT if the session has been destroyed.
     * @return The exit code for this session produced during authentication.
     */
    ExitCode getExitCode() {
        return exitCode;
    }

    /**
     * Returns whether this session is valid and can be used for authorisation.
     * Sessions are valid only if the exit code is 0.
     * @return Whether this session can be used for authorisation.
     */
    boolean isValidSession() {
        return exitCode == ExitCode.LOGIN_SUCCESS;
    }

    /**
     * Destroys this session.
     * This should be called when the user logs out or the session otherwise ends.
     * e.g. If the session times out.
     * Sets stored details to null, and the exit code to LOGGED_OUT: This session has ended.
     */
    void destroySession() {
        timestamp = null;
        username = null;
        role = null;
        exitCode = ExitCode.LOGGED_OUT;
    }
}
