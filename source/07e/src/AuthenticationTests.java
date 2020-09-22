import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class AuthenticationTests
{
    private static final Role[] roles = Role.values();
    public AuthenticationTests()
    {
    }

    @Before
    public void setUp() throws SQLException
    {
        DatabaseController.connect();
        DatabaseController.addDummyUser("zzz987", "0000", "password", "012345");
        DatabaseController.addDummyUser("cva987", "0001", "password", "0");
    }

    @After
    public void tearDown()
    {
        DatabaseController.removeDummyUser("zzz987");
        DatabaseController.removeDummyUser("cva987");
        DatabaseController.disconnect();
    }

    @Test
    public void userLoginSuccess()
    {
        //This must be a valid user with User permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[0]).getExitCode());
    }

    @Test
    public void employeeLoginSuccess()
    {
        //This must be a valid user with Employee permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[1]).getExitCode());
    }

    @Test
    public void HREmployeeLoginSuccess()
    {
        //This must be a valid user with HR Employee permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[2]).getExitCode());
    }

    @Test
    public void managerLoginSuccess()
    {
        //This must be a valid user with Manager permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[3]).getExitCode());
    }

    @Test
    public void directorLoginSuccess()
    {
        //This must be a valid user with Director permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[4]).getExitCode());
    }

    @Test
    public void reviewerLoginSuccess()
    {
        //This must be a valid user with Reviewer permissions in our filestore.
        assertEquals(ExitCode.LOGIN_SUCCESS, Authenticator.authenticate("zzz987", "password",
                roles[5]).getExitCode());
    }

    @Test
    public void loginRoleFailure()
    {
        //This must be a valid user with an invalid role in our system.
        assertEquals(ExitCode.INVALID_ROLE, Authenticator.authenticate("cva987", "password",
                roles[4]).getExitCode());
    }

    @Test
    public void loginPasswordFailure()
    {
        //This must be a valid user in our filestore with a different password.
        assertEquals(ExitCode.INVALID_LOGIN, Authenticator.authenticate("zzz987", "wrongPassword",
                roles[0]).getExitCode());
    }

    @Test
    public void loginUsernameFailure()
    {
        //This must not be a valid user in our filestore.
        assertEquals(ExitCode.INVALID_LOGIN, Authenticator.authenticate("abc129", "password",
                roles[0]).getExitCode());
    }

    @Test
    public void logOut()
    {
        AppController controller = new AppController();
        controller.login("zzz987", "password", roles[0]);
        assertTrue(controller.getActiveSession().isValidSession());
        assertTrue(controller.logout());
        assertFalse(controller.getActiveSession().isValidSession());
    }

}