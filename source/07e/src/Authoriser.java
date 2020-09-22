import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Authorises users to perform certain actions based on their authenticated role in their active Session.
 * ToDo: Implement AuthorisationDB for cases such as Manager reading their supervised employees' annual review records vs reading another employee's records.
 * @author Marin md485, James jd556
 * @version 20190301
 */
class Authoriser {

    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());

    /**
     * Checks whether the user has permission to perform the specified action
     * on the targetUser's personal details record.
     * @param user The active session of the user requesting the action.
     * @param action The action requested by the user.
     * @param targetUser The user which the requested personal details record belongs to.
     * @return Whether the requesting user has permission to perform the specified action.
     */
    static boolean getAuthorisation(Session user, Permission action, String targetUser) {
        boolean success;
        switch (action) {
            case READ_PERSONAL_DETAILS: case AMEND_PERSONAL_DETAILS: case CREATE_ANNUAL_REVIEW:
            case READ_CURRENT_ANNUAL_REVIEW: case READ_PAST_ANNUAL_REVIEW:
                //Check whether the file belongs to the user requesting it
                success = user.getUsername().equals(targetUser);
                break;
            case HR_AMEND_PERSONAL_DETAILS: case HR_READ_PERSONAL_DETAILS:
            case CREATE_PERSONAL_DETAILS: case READ_ANY_ANNUAL_REVIEW:
                //Check whether the user requesting the file has the specified HR permission
                success = user.getRole().getPermissions().contains(action);
                break;
            case REVIEWER_AMEND_ANNUAL_REVIEW: case REVIEWER_READ_PAST_ANNUAL_REVIEW:
            case REVIEWER_READ_CURRENT_ANNUAL_REVIEW:
                success = user.getRole().getPermissions().contains(action) &&
                        DatabaseController.listReviewees(user.getUsername())
                                .contains(targetUser);
                break;
            case SIGN_ANNUAL_REVIEW:
                success = user.getUsername().equals(targetUser) || DatabaseController.isReviewing(user.getUsername(), targetUser);
                break;
            default:
                //This isn't a defined request
                success = false;
        }
        logAuthAttempt(user, action, targetUser, success);
        return success;
    }

    /**
     * Creates a log for this authorisation check.
     * Records are of the form timestamp, username, role, exitCode.
     * If the log file cannot be found at the expected location,
     *  create the file before appending this record.
     * @param user The session of the user that requested the action.
     * @param action The action that was requested.
     * @param success Whether the user was authorised to perform the action.
     */
    private static void logAuthAttempt(Session user, Permission action, String targetUser, boolean success) {
        long timestamp = System.currentTimeMillis();

        StringBuilder logContent = new StringBuilder();
        logContent.append(timestamp);
        logContent.append(",");
        logContent.append(user.getUsername());
        logContent.append(",");
        logContent.append(user.getRole());
        logContent.append(",");
        logContent.append(action.name());
        logContent.append(",");
        logContent.append(targetUser);
        logContent.append(",");
        if(success) {
            logContent.append("Authorised");
        } else {
            logContent.append("Not Authorised");
        }

        File path = new File("log/AuthorisationLog.txt").getAbsoluteFile();
        //Make the log directory if it doesn't already exist
        path.getParentFile().mkdirs();
        try {
            //Make the log file if it doesn't already exist
            path.createNewFile();
            //Append timestamp and info
            Files.write(path.toPath(), Arrays.asList(logContent.toString()),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException when logging the Authorisation Check.");
        }
    }
}
