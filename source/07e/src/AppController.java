import javax.xml.crypto.Data;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The client-side controller.
 * Displays the user interface and stores the client's active session.
 * TODO: Hash passwords client-side before sending them over the network to Authenticator.
 * @author James jd556, Marin md485
 * @version 20190323
 */
class AppController{
    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());
    private final ArrayList<Role> roles = new ArrayList<>(Arrays.asList(Role.values()));
    private Boolean exitApplication;
    private Session activeSession;
    private Scanner input;

    /**
     * Creates an AppController that reads from System.in.
     * For backwards-compatibility.
     */
    AppController() {
        this(System.in);
    }

    AppController(InputStream in){
        exitApplication = false;
        activeSession = new Session();
        input = new Scanner(in);
        AppLogger.addHandler(LOGGER);
        LOGGER.log(Level.FINEST, "App Controller Constructed");
        DatabaseController.connect();
    }

    /**
     * Runs the client-side user interface until the user chooses the "exit" command.
     * This should be called on a new AppController when the program starts.
     */
    void run() {
        LOGGER.log(Level.INFO, "Began run Method");
        System.out.println("Welcome to the Yuconz System!");
        while(!exitApplication) {
            if (!activeSession.isValidSession()) {
                loginPage();
            } else {
                menu();
            }
        }
        System.out.println("Thank you for using the Yuconz System!");
    }

    /**
     * Displays the login page to the user and prompts for input.
     * Calls the method relevant to the action the user selects.
     */
    private void loginPage(){
        LOGGER.log(Level.INFO, "Began loginPage Method");
        Integer selection = menuSelection("What would you like to do, your options are: ",
                Arrays.asList("Login", "Exit"));

        switch (selection) {
            case 0:
                LOGGER.log(Level.INFO, "Began credentials Method");
                credentials();
                break;
            case 1:
                exitApplication = true;
                break;
            default:
                break;
        }
    }

    /**
     * Returns the currently active Session for this client.
     * If the exitCode is non-zero, the Session is invalid and cannot be used for authorisation.
     * @return This client's active session.
     */
    Session getActiveSession(){
        return activeSession;
    }

    /**
     * Attempts to log in the current user as the supplied role,
     *  using the username and password supplied.
     * Sets the active session to the returned session,
     *  which may be invalid if the supplied details were incorrect.
     * @param user The username to log in as.
     * @param password The password to log in with.
     * @param role The role the user has requested for this session.
     */
    void login(String user, String password, Role role) {
        activeSession = Authenticator.authenticate(user, password, role);
        LOGGER.log(Level.INFO, "Began login Method");
    }

    /**
     * Prompts the user for role, username and password to log in with, then attempts to log in.
     * Afterwards, prints whether the login was successful.
     */
    private void credentials(){
        String id, password;

        Integer roleIndex = menuSelection("Please enter the role you wish to login as:", roles);
        Role role = roles.get(roleIndex);

        System.out.println("You have selected: " + (roleIndex + 1) + ". " + role.toString());

        System.out.println("Please enter your username:");
        id = input.nextLine();

        System.out.println("Please enter your password:");
        password = input.nextLine();

        login(id, password, role);

        if (activeSession.isValidSession()) {
            System.out.println("Login successful.");
            LOGGER.log(Level.FINEST, "Login successful");
        } else if (activeSession.getExitCode() == ExitCode.INVALID_LOGIN){
            System.out.println("Incorrect username or password.");
            LOGGER.log(Level.FINEST, "Incorrect login credentials");
        } else if (activeSession.getExitCode() == ExitCode.INVALID_ROLE){
            System.out.println("This user does not have permission for this role.");
            LOGGER.log(Level.FINEST, "Incorrect login permission");
        }
    }

    /**
     * Displays a list of options and prompts the user for a selection.
     * Returns the index of the option selected by the user.
     * @param prompt The text prompt to display to the user.
     * @param options The list of options which the user can select.
     * @return The index of the option selected by the user.
     */
    private <E> Integer menuSelection(String prompt, List<E> options) {
        int result;
        do {
            System.out.println(prompt);
            for (int i = 0; i < options.size(); i++) {
                System.out.println("    " + (i + 1) + ". " + options.get(i).toString());
            }

            try {
                result = Integer.parseInt(input.nextLine()) - 1;
            } catch (NumberFormatException e) {
                result = - 1;
            }

            if ((result >= options.size()) || (result < 0)) {
                System.out.println("Invalid selection, please try again.");
                LOGGER.log(Level.FINEST, "Invalid Selection");
            }
        } while ((result >= options.size()) || (result < 0));
        return result;
    }

    /**
     * Displays the prompt for a logged-in user and prompts user to select an option.
     * Then runs the methods relevant to the option the user selected.
     */
    private void menu(){
        System.out.println("You have successfully logged in as "+ activeSession.getUsername() +
                " you have " + activeSession.getRole() + " permissions.");

        ArrayList<Object> options = new ArrayList<>(Arrays.asList("Logout", "Exit"));
        options.addAll(activeSession.getRole().getPermissions());

        Integer selection = menuSelection("What would you like to do, your options are: ",
                options);

        switch (selection) {
            case 0:
                if (logout()) {
                    System.out.println("You have successfully logged out!");
                    LOGGER.log(Level.FINEST, "Logout successful");
                } else {
                    System.out.println("Logout error.");
                    LOGGER.log(Level.SEVERE, "Logout error");
                }
                break;
            case 1:
                LOGGER.log(Level.INFO, "Exiting application");
                logout();
                exitApplication = true;
                break;
            default:
                runAction((Permission) options.get(selection));
                break;
        }
    }

    /**
     * Runs the appropriate method for a user-selected action
     * @param chosenAction The action to run the method for
     */
    private void runAction(Permission chosenAction) {
        String targetID;
        switch (chosenAction) {
            case READ_PERSONAL_DETAILS:
                //Read this user's details
                targetID = activeSession.getUsername();
                if(Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    readPersonalDetails(targetID);
                    LOGGER.log(Level.INFO, "Began readPersonalDetails Method");
                }
                break;

            case AMEND_PERSONAL_DETAILS:
                //Amend this user's details
                targetID = activeSession.getUsername();
                if(Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Began amendPersonalDetails Method");
                    amendPersonalDetails(activeSession.getUsername());
                }
                break;

            case CREATE_PERSONAL_DETAILS:
                //Create a new personal details record
                System.out.println("Please input the ID of the employee who you wish to " +
                        "create a personal details record for.");
                targetID = input.nextLine();
                if(Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Began createPersonalDetails Method");
                    createPersonalDetails(targetID);
                }
                break;

            case HR_READ_PERSONAL_DETAILS:
                //Read another user's personal details
                System.out.println("Please input the ID of the employee whose details " +
                        "you wish to read.");
                targetID = input.nextLine();
                if(Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Began HR Version of ReadPersonalDetails Method");
                    readPersonalDetails(targetID);
                }
                break;

            case HR_AMEND_PERSONAL_DETAILS:
                //Amend a user's personal details
                System.out.println("Please input the ID of the employee whose details " +
                        "you wish to amend.");
                targetID = input.nextLine();
                if(Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Began HR Version of amendPersonalDetails Method");
                    amendPersonalDetails(targetID);
                }
                break;

            case CREATE_ANNUAL_REVIEW:
                targetID = activeSession.getUsername();
                if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, targetID + " given permission to create review");
                    boolean success = createNewReview();
                    if (success) {
                        System.out.println("Review created successfully");
                    } else {
                        System.out.println("Review already exists or it hasn't been six months " +
                                "since your last review");
                    }
                }
                break;

            case READ_CURRENT_ANNUAL_REVIEW:
                targetID = activeSession.getUsername();
                if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, targetID + " given permission to read current review.");
                    AnnualReview currentReview = DatabaseController.getUnfinishedReview(targetID);
                    if(currentReview != null) {
                        readReview(currentReview);
                    } else {
                        System.out.println("No currently active reviews exist for this user.");
                    }
                }
            break;

            case READ_PAST_ANNUAL_REVIEW:
                targetID = activeSession.getUsername();
                if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Fetching past review of " + targetID);
                    ArrayList<AnnualReview> pastReviews =
                            new ArrayList<>(DatabaseController.getPastReviews(targetID));
                    if (pastReviews.size() > 0) {
                        menuSelection("Which review would you like to see?",
                                pastReviews.stream()
                                        .map((x) -> x.getField("reviewID") + " " + x.getDate())
                                        .collect(Collectors.toList()));
                    } else {
                        System.out.println("You have no past reviews.");
                    }
                }
            break;

            case READ_ANY_ANNUAL_REVIEW:
                targetID = activeSession.getUsername();
                if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)) {
                    LOGGER.log(Level.INFO, "Checking read any review permission of " + targetID);
                    System.out.println("Which review would you like to view?");
                    String reviewID = input.next();
                    AnnualReview review =
                            DatabaseController.getAnnualReview(Integer.parseInt(reviewID));
                    if (review != null) {
                        readReview(review);
                    } else {
                        System.out.println("Review could not be found.");
                    }
                }
                break;

            case SIGN_ANNUAL_REVIEW:
                if (activeSession.getRole() == Role.REVIEWER) {
                    //Signing off as a reviewer.
                    ArrayList<String> reviewees = new ArrayList<>(
                            DatabaseController.listReviewees(activeSession.getUsername()));
                    if (reviewees.size() > 0) {
                        targetID = reviewees.get(
                                menuSelection("Select the reviewee you wish to sign off on:",
                                        reviewees));
                        if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)){
                            LOGGER.log(Level.INFO, activeSession.getUsername() +
                                    " given sign off permission for " + targetID + ".");
                            AnnualReview review = DatabaseController.getUnfinishedReview(targetID);
                            review.signOff(
                                    DatabaseController.getStaffNo(activeSession.getUsername()));
                            DatabaseController.updateAnnualReview(review);
                            System.out.println("Signed off successfully");
                        } else {
                            System.out.println("Authorisation denied.");
                        }
                    } else {
                        System.out.println("You have no reviewees.");
                    }
                } else {
                    //Signing off as a reviewee.
                    targetID = activeSession.getUsername();
                    if (Authoriser.getAuthorisation(activeSession, chosenAction, targetID)){
                        LOGGER.log(Level.INFO, activeSession.getUsername() +
                                " given sign off permission for " + targetID + ".");
                        AnnualReview review = DatabaseController.getUnfinishedReview(targetID);
                        review.signOff(DatabaseController.getStaffNo(targetID));
                        DatabaseController.updateAnnualReview(review);
                        System.out.println("Signed off successfully");
                    }
                }
                break;

            default:
                System.err.println("The specified action doesn't have a method " +
                        "associated with it");
                LOGGER.log(Level.FINEST, "Unknown menu input detected");
                break;
        }
    }

    /**
     * Looks up the personal details record belonging to the specified user.
     * If the details cannot be found, prints that it cannot be found and returns false.
     * Otherwise, prints the contents of the personal details record and returns true.
     * @param targetID Which user's personal details to display.
     * @return Whether the record was found.
     */
    private boolean readPersonalDetails(String targetID) {
        PersonalDetails details = DatabaseController.getPersonalDetails(targetID);
        if(details != null) {
            System.out.println(details.printDetails());
            LOGGER.log(Level.FINEST, "Personal details found");
            return true;
        } else {
            System.out.println("The personal details record for the specified user " +
                    "could not be found.");
            LOGGER.log(Level.WARNING, "Personal details not found");
            return false;
        }
    }

    private void readReview(AnnualReview review) {
        System.out.println(review.printAllDetails());
    }

    private boolean createNewReview() {
        ArrayList<AnnualReview> reviews = new ArrayList<>(
                DatabaseController.getReviews(activeSession.getUsername()));
        if (reviews.size() > 0) {
            if (reviews.stream().allMatch((x) -> x.isComplete())) {
                Date newestReview = reviews.stream().map(AnnualReview::getDate)
                        .max(java.sql.Date::compareTo).get();
                Long sixMonthsAgo = LocalDate.now().minusMonths(6).toEpochDay();
                Long twoWeeks = (long) (2 * 7 * 24 * 60 * 60 * 1000);
                if (newestReview.getTime() >= (sixMonthsAgo - twoWeeks) &&
                    newestReview.getTime() <= (sixMonthsAgo + twoWeeks)) {
                    DatabaseController.createAnnualReview(activeSession.getUsername());
                    return true;
                }
            }
        } else {
            DatabaseController.createAnnualReview(activeSession.getUsername());
            return true;
        }
        return false;
    }

    /**
     * Looks up the personal details record belonging to the specified user.
     * Prints the record and prompts user to select which fields to amend and input new values.
     * Afterwards, updates the appropriate record in the database.
     * If the record isn't found, do nothing.
     * @param targetID Which user's personal details should be amended.
     */
    private void amendPersonalDetails(String targetID) {
        PersonalDetails details = DatabaseController.getPersonalDetails(targetID);
        //If the details exist, prompt user to amend its values.
        if(details != null) {
            ArrayList<String> fields = new ArrayList<>(details.returnFields());
            fields.add("Stop amending this file");
            boolean finished = false;
            LOGGER.log(Level.FINEST, "Begin amend loop");

            do {
                System.out.println(details.printDetails());
                int selection = menuSelection("Please select a field to update:", fields);
                if (selection == fields.size() - 1) {
                    //If the user chose to stop amending, end loop.
                    LOGGER.log(Level.FINEST, "Amend loop ended early by user choice");
                    finished = true;
                } else {
                    String selectedField = fields.get(selection);
                    System.out.println("Please input a new value for " + selectedField);
                    details.setField(selectedField, input.nextLine());
                    LOGGER.log(Level.FINEST, "Personal details field amended");
                }
            } while (!finished);
            DatabaseController.updatePersonalDetails(details);
            LOGGER.log(Level.FINEST, "Updated Personal Details file in Database.");
        } else {
            System.out.println("The personal details record for the specified user " +
                    "could not be found.");
            LOGGER.log(Level.WARNING, "Personal details not found");
        }
    }

    /**
     * Attempts to create a personal details record for the specified user.
     * Fails if the file already exists.
     * @param targetID The user to create a personal details record for.
     */
    private void createPersonalDetails(String targetID) {
        if(DatabaseController.getPersonalDetails(targetID) != null) {
            System.out.println("A personal details record for " + targetID + " already exists.");
        } else {
            PersonalDetails details = new PersonalDetails();
            LOGGER.log(Level.FINEST, "New personal details created");
            for(String field : details.returnFields()) {
                System.out.println("Please input a value for " + field);
                details.setField(field, input.nextLine());
                LOGGER.log(Level.FINEST, "Personal details field set");
            }

            //Add the new personal details record to the database
            DatabaseController.addPersonalDetails(details);
            LOGGER.log(Level.FINEST, "Added personal details to database");
        }
    }

    /**
     * Ends the current session.
     * @return Whether the Authenticator successfully ended the client's session.
     */
    boolean logout(){
        LOGGER.log(Level.FINEST, "Logging out");
        return Authenticator.logout(activeSession);
    }
}
