import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;

/**
 * The roles that can be selected by the user when logging in.
 * Each role has different permissions based on what they are authorised to do.
 * @author Marin md485, James jd556
 * @version 20190324
 */
public enum Role {
    USER("User",
            Arrays.asList(Permission.READ_PERSONAL_DETAILS, Permission.AMEND_PERSONAL_DETAILS)),
    EMPLOYEE("Employee",
            Arrays.asList(Permission.READ_PERSONAL_DETAILS, Permission.AMEND_PERSONAL_DETAILS,
                    Permission.CREATE_ANNUAL_REVIEW, Permission.READ_CURRENT_ANNUAL_REVIEW,
                    Permission.READ_PAST_ANNUAL_REVIEW, Permission.SIGN_ANNUAL_REVIEW)),
    HR_EMPLOYEE("HR Employee",
            Arrays.asList(Permission.CREATE_PERSONAL_DETAILS, Permission.HR_READ_PERSONAL_DETAILS,
                    Permission.HR_AMEND_PERSONAL_DETAILS, Permission.READ_ANY_ANNUAL_REVIEW)),
    MANAGER("Manager",
            Arrays.asList(Permission.READ_PERSONAL_DETAILS, Permission.AMEND_PERSONAL_DETAILS)),
    DIRECTOR("Director",
            Arrays.asList(Permission.READ_ANY_ANNUAL_REVIEW)),
    REVIEWER("Reviewer",
            Arrays.asList(Permission.REVIEWER_READ_CURRENT_ANNUAL_REVIEW,
                    Permission.REVIEWER_READ_PAST_ANNUAL_REVIEW,
                    Permission.REVIEWER_AMEND_ANNUAL_REVIEW, Permission.SIGN_ANNUAL_REVIEW));

    private final String title;
    private final LinkedHashSet<Permission> permissions;

    Role(String title, List<Permission> permissions) {
        this.title = title;
        this.permissions = new LinkedHashSet<>(permissions);
    }

    public String toString() {
        return title;
    }

    public LinkedHashSet<Permission> getPermissions() {
        return permissions;
    }
}
