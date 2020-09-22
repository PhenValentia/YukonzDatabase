import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class AuthoriserTests
{
    private Session UserHR, User;

    public AuthoriserTests()
    {
    }

    @Before
    public void setUp() throws SQLException
    {
        DatabaseController.connect();
        DatabaseController.addDummyUser("zzz987", "0000", "password", "012345");
        DatabaseController.addDummyUser("cva987", "0001", "password", "0");

        UserHR = Authenticator.authenticate("zzz987", "password", Role.HR_EMPLOYEE);
        User = Authenticator.authenticate("cva987", "password", Role.USER);
    }

    @After
    public void tearDown()
    {
        DatabaseController.removeDummyUser("zzz987");
        DatabaseController.removeDummyUser("cva987");
        DatabaseController.disconnect();
    }

    @Test
    public void userCanReadOwnDetails()
    {
        assertTrue(Authoriser.getAuthorisation(User, Permission.READ_PERSONAL_DETAILS, User.getUsername()));
    }

    @Test
    public void userCanAmendOwnDetails()
    {
        assertTrue(Authoriser.getAuthorisation(User, Permission.AMEND_PERSONAL_DETAILS, User.getUsername()));
    }

    @Test
    public void userCantReadOthers()
    {
        assertFalse(Authoriser.getAuthorisation(User, Permission.HR_READ_PERSONAL_DETAILS, UserHR.getUsername()));
    }

    @Test
    public void userCantAmendOthers()
    {
        assertFalse(Authoriser.getAuthorisation(User, Permission.HR_AMEND_PERSONAL_DETAILS, UserHR.getUsername()));
    }

    @Test
    public void userCantCreateDetails()
    {
        assertFalse(Authoriser.getAuthorisation(User, Permission.CREATE_PERSONAL_DETAILS, "akc342"));
    }

    @Test
    public void hrCanReadOwnDetails()
    {
        assertTrue(Authoriser.getAuthorisation(UserHR, Permission.READ_PERSONAL_DETAILS, UserHR.getUsername()));
    }

    @Test
    public void hrCanReadOthersDetails()
    {
        assertTrue(Authoriser.getAuthorisation(UserHR, Permission.HR_READ_PERSONAL_DETAILS, User.getUsername()));
    }

    @Test
    public void hrCanAmendDetails()
    {
        assertTrue(Authoriser.getAuthorisation(UserHR, Permission.HR_READ_PERSONAL_DETAILS, User.getUsername()));
    }

    @Test
    public void hrCanAmendOthersDetails()
    {
        assertTrue(Authoriser.getAuthorisation(UserHR, Permission.HR_AMEND_PERSONAL_DETAILS, User.getUsername()));
    }

    @Test
    public void hrCanCreateDetails() {
        assertTrue(Authoriser.getAuthorisation(UserHR, Permission.CREATE_PERSONAL_DETAILS, "akc342"));
    }

}