/**
 * Role-specific permissions for actions that can be carried out by the user.
 * Note that existence of a permission for an action doesn't guarantee that role
 *  can always perform it, only that a user in that role may attempt the action.
 * E.G. A Reviewer may read the past review records of an employee they are reviewing,
 *  but not that of an employee they aren't reviewing.
 * @author Marin md485, James jd556
 * @version 20190324
 */
public enum Permission {
    CREATE_PERSONAL_DETAILS("Create a new personal details document"),
    READ_PERSONAL_DETAILS("Read your own personal details document"),
    AMEND_PERSONAL_DETAILS("Amend your own personal details document"),
    HR_READ_PERSONAL_DETAILS("Read a personal details document"),
    HR_AMEND_PERSONAL_DETAILS("Amend a personal details document"),
    CREATE_ANNUAL_REVIEW("Create a new annual review document"),
    READ_CURRENT_ANNUAL_REVIEW("Read your currently active annual review document"),
    READ_PAST_ANNUAL_REVIEW("Read your past completed annual review documents"),
    REVIEWER_READ_CURRENT_ANNUAL_REVIEW("Read a currently active annual review document"),
    REVIEWER_READ_PAST_ANNUAL_REVIEW("Read a past completed annual review document"),
    REVIEWER_AMEND_ANNUAL_REVIEW("Amend a currently active annual review document"),
    SIGN_ANNUAL_REVIEW("Sign off on a currently active annual review"),
    READ_ANY_ANNUAL_REVIEW("Read an annual review document");

    private String description;

    Permission(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }
}
