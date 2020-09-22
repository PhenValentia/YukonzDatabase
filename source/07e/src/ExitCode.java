/**
 * The exit code produced for a session during authentication,
 * or LOGGED_OUT if the session has been destroyed.
 * @author James jd556
 * @version 20190301
 */
public enum ExitCode {
    LOGIN_SUCCESS("Authentication successful"),
    INVALID_LOGIN("Incorrect username or password"),
    INVALID_ROLE("This user does not have permission for this role"),
    LOGGED_OUT("This session has ended");

    private final String description;

    ExitCode(String description) {
        this.description = description;
    }

    /**
     * Gets the description of the exit code.
     * @return The description of the exit code
     */
    public String toString() {
        return description;
    }

}
