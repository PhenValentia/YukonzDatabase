import java.io.File;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The database controller.
 * Handles connections to databases and operations on databases.
 * @author James jd556, Marin md485, Jamie jdg23
 * @version 20190323
 */
class DatabaseController {
    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());
    //The connection to the Database we use as our main point of interaction.
    private static Connection conn;
    //Dummy data is still maintained both for the initialisation of new databases,
    // and to maintain the current operation of the program.
    // (TODO: Change the operations relying on the hashmaps to use SQL statements)
    private static HashMap<String, String[]> authDb;
    private static HashMap<String, PersonalDetails> personalDetails;

    /**
     * A method used to connect to the database, or initialise a new one if a database
     * doesn't currently exist.
     */
    static void connect() {
        //Makes sure the connection isn't currently connected to a server and closes it if it is.
        //This is to avoid opened database files not being closed correctly.
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        //Places the DB into the user.home location of the OS, this location exists in all OSs
        //so there shouldn't be issues in it's creation.
        String url = "jdbc:sqlite:" + System.getProperty("user.home") +
                File.separator + "Yuconz.db";
        try {
            //Attempts to connect to the database, if it cannot a new database is created.
            conn = DriverManager.getConnection(url);
            //Checks to see if there are any tables in the database the connection connected to,
            // this is used to check whether a new database was created, or whether an existing
            // database was connected to.
            if (conn.createStatement().executeQuery("SELECT name FROM sqlite_master " +
                    "WHERE type ='table' AND name NOT LIKE 'sqlite_%'").next()) {
                System.out.println("Connection to Yuconz Database has been established.");
            } else {
                System.out.println("Database not found, initialising new database.");
                //In the case where a new database is created the following strings will
                // initialise the necessary tables for the function of database.
                // IF NOT EXISTS is used on the off-chance the previous code erroneously
                // assumes the database it's connected to has no tables.

                System.out.println("Creating Tables.");

                //Execution of the SQL statements defined above.
                List<String> sqlStatements = getTableDefinitions();
                for(String sqlStatement : sqlStatements) {
                    conn.createStatement().execute(sqlStatement);
                }

                System.out.println("Populating Users.");

                // The following for each loops populate the tables defined above with,
                // the dummy data within our initialisation hash maps.
                for(Map.Entry<String, String[]> users : authDb.entrySet()) {
                    addDummyUser(users.getKey(), users.getValue()[0],
                            users.getValue()[1], users.getValue()[2]);
                }

                System.out.println("Populating PersonalDetails.");

                for(Map.Entry<String, PersonalDetails> details : personalDetails.entrySet()) {
                    PersonalDetails values = details.getValue();
                    conn.createStatement().execute(
                            "INSERT INTO PersonalDetails VALUES ('"
                                    + values.getField("Staff No") + "','"
                                    + values.getField("Surname") + "','"
                                    + values.getField("Name") + "','"
                                    + values.getField("Date of Birth") + "','"
                                    + values.getField("Address") + "','"
                                    + values.getField("Town/City") + "','"
                                    + values.getField("Post Code") + "','"
                                    + values.getField("Telephone Number") + "','"
                                    + values.getField("Mobile Number") + "','"
                                    + values.getField("Emergency Contact") + "','"
                                    + values.getField("Emergency Contact Number") + "');");
                }
            }
        } catch (Exception e) {
            System.out.println("Critical Error: " + e.getMessage());
        }
    }

    static {
        authDb = new HashMap<>();
        personalDetails = new HashMap<>();
        //Static initialisation block to populate database if one doesn't exist already.
        authDb.put("abc123", new String[]{"1234", "trueLies2019", "02"});
        authDb.put("tut856", new String[]{"1235", "SkyFall2012", "0235"});
        authDb.put("zxc556", new String[]{"9876", "helloMoto", "04"});
        authDb.put("yup837", new String[]{"1111", "whatsUP999", "0"});
        authDb.put("lit013", new String[]{"2222", "password", "05"});
        authDb.put("mar485", new String[]{"3333", "superman", "03"});
        authDb.put("jdg890", new String[]{"4444", "passman", "01"});
        authDb.put("jdd567", new String[]{"5555", "superpass1230", "04"});
        authDb.put("asd042", new String[]{"6666", "nnmsofne038474", "014"});
        authDb.put("plc912", new String[]{"7777", "ohnononono000", "0123"});
        authDb.put("vie137", new String[]{"8888", "pizzaIsNice746", "0125"});
        authDb.put("pie922", new String[]{"0101", "andChips", "05"});

        /*
        personalDetails.put("abc123", new PersonalDetails("1234", "Harris", "Sam", "1982/12/12",
                "28 Dark Lane", "Bangle Town", "MK5 9LS", "09487321582", "05748392437",
                "Peter Shoal", "01923421543"));
        personalDetails.put("tut856", new PersonalDetails("1235", "Fire", "Logan", "1976/04/29",
                "81 Damon Close", "Summer City", "LT3 0MT", "09764321582", "05746492437",
                "Garon Dan","01923475343"));
                */
    }

    static void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Returns a list of the SQLite HR Database Table definitions.
     * @return A list of SQLite table creation statements.
     */
    private static List<String> getTableDefinitions() {
        List<String> statements = new ArrayList<>();
        String createEmployeeDetailsTable = "CREATE TABLE " +
                "IF NOT EXISTS EmployeeDetails ( " +
                "staffNo VARCHAR(255) PRIMARY KEY," +
                "username VARCHAR(255) UNIQUE," +
                "supervisor VARCHAR(255)," +
                "section VARCHAR(255)," +
                "jobTitle VARCHAR(255)," +
                "role VARCHAR(255)," +
                "FOREIGN KEY (supervisor) REFERENCES EmployeeDetails (staffNo) );";
        String createAuthenticationTable = "CREATE TABLE IF NOT EXISTS Authentication (" +
                "username VARCHAR(255) PRIMARY KEY," +
                "password VARCHAR(255)," +
                "FOREIGN KEY (username) REFERENCES EmployeeDetails (username) );";
        String createUsersView = "CREATE VIEW IF NOT EXISTS Users AS " +
                "SELECT Authentication.username AS uid, " +
                "Authentication.password AS pass, " +
                "EmployeeDetails.role AS permissions " +
                "FROM Authentication JOIN EmployeeDetails " +
                "ON Authentication.username = EmployeeDetails.username ;";
        String createPersonalDetailsTable = "CREATE TABLE " +
                "IF NOT EXISTS PersonalDetails (" +
                "staffNo varchar(255) PRIMARY KEY," +
                "surname varchar(20)," +
                "name varchar(20)," +
                "dob char(10)," +
                "address varchar(40)," +
                "town varchar(20)," +
                "postcode varchar(9)," +
                "telNo varchar(20)," +
                "mobNo varchar(20)," +
                "emergCont varchar(40)," +
                "contNo varchar(20)," +
                "FOREIGN KEY (staffNo) REFERENCES EmployeeDetails (staffNo));";
        String createAnnualReviewTable = "CREATE TABLE IF NOT EXISTS AnnualReview (" +
                "reviewID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "staffNo VARCHAR(255)," +
                "name VARCHAR(255)," +
                "supervisor VARCHAR(255)," +
                "secondReviewer VARCHAR(255)," +
                "section VARCHAR(255)," +
                "jobTitle VARCHAR(255)," +
                "recommendation VARCHAR(255)," +
                "performanceSummary TEXT," +
                "reviewerComments TEXT," +
                "revieweeSigned DATE," +
                "supervisorSigned DATE," +
                "secondReviewerSigned DATE," +
                "FOREIGN KEY (staffNo) REFERENCES EmployeeDetails (staffNo) ," +
                "FOREIGN KEY (supervisor) REFERENCES EmployeeDetails (staffNo)," +
                "FOREIGN KEY (secondReviewer) REFERENCES EmployeeDetails (staffNo) );";
        String createPastPerformanceTable = "CREATE TABLE " +
                "IF NOT EXISTS PastPerformance (" +
                "reviewID INTEGER," +
                "number INTEGER," +
                "objective TEXT," +
                "achievement TEXT," +
                "PRIMARY KEY (reviewID, number)," +
                "FOREIGN KEY (reviewID) REFERENCES AnnualReview (reviewID)" +
                ");";
        String createFutureGoalsTable = "CREATE TABLE " +
                "IF NOT EXISTS FutureGoals (" +
                "reviewID INTEGER," +
                "goalNo INTEGER," +
                "content TEXT," +
                "PRIMARY KEY (reviewID, goalNo)," +
                "FOREIGN KEY (reviewID) REFERENCES AnnualReview (reviewID)" +
                ");";

        String createUsersUpdateTrigger = "CREATE TRIGGER " +
                "IF NOT EXISTS UsersUpdate INSTEAD OF UPDATE ON Users " +
                "FOR EACH ROW BEGIN " +
                "UPDATE EmployeeDetails SET " +
                "username = NEW.username, " +
                "role = NEW.permissions " +
                "WHERE username = OLD.username; " +
                "UPDATE Authentication SET " +
                "username = NEW.username, " +
                "password = NEW.password " +
                "WHERE username = OLD.username; " +
                "END;";

        statements.add(createEmployeeDetailsTable);
        statements.add(createAuthenticationTable);
        statements.add(createUsersView);
        statements.add(createPersonalDetailsTable);
        statements.add(createAnnualReviewTable);
        statements.add(createPastPerformanceTable);
        statements.add(createFutureGoalsTable);
        statements.add(createUsersUpdateTrigger);

        return statements;
    }

    /**
     * Adds a user to the HR Database for testing purposes.
     * Creates an EmployeeDetails record and an Authentication record for that user.
     * @param username The username of the user.
     * @param staffNo The staff number of the user.
     * @param password The password of the user.
     * @param roles The roles the user can authenticate as.
     * @throws SQLException If the SQL statements could not be executed.
     */
    static void addDummyUser(String username, String staffNo, String password,
                             String roles) throws SQLException {
        String empDetails = "INSERT INTO EmployeeDetails " +
                "(staffNo, username, role) VALUES (?, ?, ?); ";
        String authDetails = "INSERT INTO Authentication (username, password) VALUES (?, ?);";
        String personalDetails = "INSERT INTO PersonalDetails VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement empSql = conn.prepareStatement(empDetails);
        empSql.setString(1, staffNo);
        empSql.setString(2, username);
        empSql.setString(3, roles);

        PreparedStatement authSql = conn.prepareStatement(authDetails);
        authSql.setString(1, username);
        authSql.setString(2, password);

        PreparedStatement perSql = conn.prepareStatement(personalDetails);
        perSql.setString(1, staffNo);
        for(int i = 0; i < 10; i++) {
            StringBuilder dummyData = new StringBuilder();
            for(int j = 0; j < 9; j++) {
                char a = (char) (32 + Math.round(Math.random()*94));
                dummyData.append(a);
            }
            perSql.setString(i+2, dummyData.toString());
        }

        empSql.execute();
        authSql.execute();
        perSql.execute();
    }

    /**
     * Removes a dummy user from the system.
     * Deletes the authentication record and employee details record of the specified user.
     * For testing purposes only.
     * @param username The username of the dummy user to delete.
     */
    static void removeDummyUser(String username) {
        removeDummyUser(username, getStaffNo(username));
    }

    /**
     * Removes a dummy user from the system.
     * Deletes the authentication record and employee details record of the specified user.
     * For testing purposes only.
     * @param username The username of the dummy user to delete.
     * @param staffNo The staff number of the dummy user to delete.
     */
    static void removeDummyUser(String username, String staffNo) {
        removeUser(username);
        removeEmpDetails(staffNo);
    }

    /**
     * Gets the staff number of the user with the specified login.
     * @param username Which user to get the staff number of.
     * @return The specified user's staff number, or null if not found.
     */
    static String getStaffNo(String username) {
        //Checks to see if the input is already a staffNo
        if (Character.isDigit(username.charAt(0))) {
            return username;
        }
        String sql = "SELECT staffNo FROM EmployeeDetails WHERE username = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, username);
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                return rSet.getString("staffNo");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Gets the fields associated with the given username.
     * Returns fields in [password, roles] format.
     * @param username The username to lookup.
     * @return The fields associated with the given username as an array, or null if not found.
     */
    static String[] getAuthData(String username) {
        String sql = "SELECT pass, permissions FROM Users WHERE uid = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, username);
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                return new String[] {rSet.getString("pass"), rSet.getString("permissions")};
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Gets the personal details file related to the specified userId, or null if it doesn't exist.
     * @param userID The username of the user to get the personal details for.
     * @return The personal details for that user, or null if not present.
     */
    static PersonalDetails getPersonalDetails(String userID) {
        String sql = "SELECT staffNo, surname, name, dob, address, town, postcode, telNo, mobNo, " +
                "emergCont, contNo FROM PersonalDetails WHERE staffNo = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, getStaffNo(userID));
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                return new PersonalDetails(
                        rSet.getString("staffNo"),
                        rSet.getString("surname"),
                        rSet.getString("name"),
                        rSet.getString("dob"),
                        rSet.getString("address"),
                        rSet.getString("town"),
                        rSet.getString("postcode"),
                        rSet.getString("telNo"),
                        rSet.getString("mobNo"),
                        rSet.getString("emergCont"),
                        rSet.getString("contNo")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Adds the specified personal details record for userID to the personal details database.
     * @param details The personal details to be added to the database.
     */
    static void addPersonalDetails(PersonalDetails details) {
        String sql = "INSERT INTO PersonalDetails VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, details.getField("Staff No"));
            pStatement.setString(2, details.getField("Surname"));
            pStatement.setString(3, details.getField("Name"));
            pStatement.setString(4, details.getField("Date of Birth"));
            pStatement.setString(5, details.getField("Address"));
            pStatement.setString(6, details.getField("Town/City"));
            pStatement.setString(7, details.getField("Post Code"));
            pStatement.setString(8, details.getField("Telephone Number"));
            pStatement.setString(9, details.getField("Mobile Number"));
            pStatement.setString(10, details.getField("Emergency Contact"));
            pStatement.setString(11, details.getField("Emergency Contact Number"));
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates a personal details file.
     * @param details The updated personal details file to put in the database.
     */
    static void updatePersonalDetails(PersonalDetails details) {
        String sql = "UPDATE PersonalDetails SET " +
                "staffNo = ?," +
                "surname = ?," +
                "name = ?," +
                "dob = ?," +
                "address = ?," +
                "town = ?," +
                "postcode = ?," +
                "telNo = ?," +
                "mobNo = ?," +
                "emergCont = ?," +
                "contNo = ? " +
                "WHERE staffNo = ?;";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, details.getField("Staff No"));
            pStatement.setString(2, details.getField("Surname"));
            pStatement.setString(3, details.getField("Name"));
            pStatement.setString(4, details.getField("Date of Birth"));
            pStatement.setString(5, details.getField("Address"));
            pStatement.setString(6, details.getField("Town/City"));
            pStatement.setString(7, details.getField("Post Code"));
            pStatement.setString(8, details.getField("Telephone Number"));
            pStatement.setString(9, details.getField("Mobile Number"));
            pStatement.setString(10, details.getField("Emergency Contact"));
            pStatement.setString(11, details.getField("Emergency Contact Number"));
            pStatement.setString(12, details.getField("Staff No"));
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes a user's personal details from the system.
     * This will not be used in the final version of the system,
     * this only exists for the purposes of testing mockups.
     * @param staffNo The staff number of the user whose record to remove.
     */
    static void removePersonalDetails(String staffNo) {
        String sql = "DELETE FROM PersonalDetails WHERE staffNo = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, staffNo);
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a new user to the authentication database.
     * Replaces the old details if the user already exists.
     * @param username The username to add to the database
     * @param password The password for the user.
     */
    static void addUser(String username, String password) {
        String sql = "INSERT INTO Authentication VALUES (?,?) " +
                "ON CONFLICT DO UPDATE SET username = excluded.username, " +
                "password = excluded.password";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes a user from the authentication database
     * @param username Which user to remove
     */
    static void removeUser(String username) {
        String sql = "DELETE FROM Authentication WHERE username = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, username);
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Removes a user from the Employee Details table.
     * @param staffNo The staff number of the user to remove.
     */
    private static void removeEmpDetails(String staffNo) {
        String sql = "DELETE FROM EmployeeDetails WHERE staffNo = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, staffNo);
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates an annual review for the specified user.
     * Fills in staff number, name, supervisor, section and job title from employeeDetails table.
     * @param username The username of the employee to create an annual review for.
     */
    static void createAnnualReview(String username) {
        String sql = "SELECT Emp.staffNo, Pers.name, Emp.supervisor, Emp.section, Emp.jobTitle " +
                "FROM EmployeeDetails AS Emp JOIN PersonalDetails AS Pers " +
                "ON Emp.staffNo = Pers.staffNo WHERE username = ?;";

        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setString(1, username);
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                AnnualReview review = new AnnualReview();
                review.setField("Staff No", rSet.getString("staffNo"));
                review.setField("Name", rSet.getString("name"));
                review.setField("Supervisor", rSet.getString("supervisor"));
                review.setField("Section", rSet.getString("section"));
                review.setField("Job Title", rSet.getString("jobTitle"));
                insertAnnualReview(review);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts a new annual review record in the database.
     * @param review The newly created annual review to store.
     */
    static void insertAnnualReview(AnnualReview review) {
        String reviewInsert = "INSERT INTO AnnualReview (staffNo, name, supervisor, " +
                "secondReviewer, section, jobTitle, recommendation, performanceSummary, " +
                "reviewerComments, revieweeSigned, supervisorSigned, secondReviewerSigned) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        String pastPerfInsert = "INSERT INTO PastPerformance VALUES (?,?,?,?);";
        String FutureGoalsInsert = "INSERT INTO FutureGoals VALUES (?,?,?);";

        ArrayList<Date> signatures = review.getSignatures();
        PerformanceReview pastReview = review.getPastPerformance();
        PerformanceReview futureGoals = review.getFutureGoals();

        try ( PreparedStatement rInsert = conn.prepareStatement(reviewInsert);
              PreparedStatement ppInsert = conn.prepareStatement(pastPerfInsert);
              PreparedStatement fgInsert = conn.prepareStatement(FutureGoalsInsert)){
            rInsert.setString(1, review.getField("Staff No"));
            rInsert.setString(2, review.getField("Name"));
            rInsert.setString(3, review.getField("Supervisor"));
            rInsert.setString(4, review.getField("Second Reviewer"));
            rInsert.setString(5, review.getField("Section"));
            rInsert.setString(6, review.getField("Job Title"));
            rInsert.setString(7, review.getField("Recommendation"));
            if (pastReview == null) {
                rInsert.setNull(8, Types.VARCHAR);
            } else {
                rInsert.setString(8, pastReview.getSummary());
            }
            if (futureGoals == null) {
                rInsert.setNull(9, Types.VARCHAR);
            } else {
                rInsert.setString(9, futureGoals.getSummary());
            }
            for (int i = 0; i < 3; i++) {
                if (signatures.get(i) == null) {
                    rInsert.setNull(i+10, Types.DATE);
                } else {
                    rInsert.setDate(i+10, signatures.get(i));
                }
            }
            rInsert.execute();

            if (review.getReviewID() != null) {
                ppInsert.setInt(1, review.getReviewID());
                //Loop to insert all past performance.
                ArrayList<String> objectives = pastReview.getGoals();
                for (int i = 0; i < objectives.size(); i++) {
                    ppInsert.setInt(2, i);
                    ppInsert.setString(3, objectives.get(i));
                    ppInsert.setString(4, pastReview.getResult(objectives.get(i)));

                    ppInsert.execute();
                }

                fgInsert.setInt(1, review.getReviewID());
                //Loop to insert all future goals.
                ArrayList<String> goals = futureGoals.getGoals();
                for (int i = 0; i < goals.size(); i++) {
                    fgInsert.setInt(2, i);
                    fgInsert.setString(3, goals.get(i));

                    fgInsert.execute();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates an existing annual review record in the database.
     * Fails if the review doesn't already exist.
     * @param updatedReview The updated annual review to store.
     */
    static void updateAnnualReview(AnnualReview updatedReview) {
        //Update Annual Review table, then update adjacent tables.
        String reviewUpdate = "UPDATE AnnualReview SET " +
                "staffNo = ?," +
                "name = ?," +
                "supervisor = ?," +
                "secondReviewer = ?," +
                "section = ?," +
                "jobTitle = ?," +
                "recommendation = ?," +
                "performanceSummary = ?," +
                "reviewerComments = ?," +
                "revieweeSigned = ?," +
                "supervisorSigned = ?," +
                "secondReviewerSigned = ? " +
                "WHERE reviewID = ?;";
        String pastPerfUpdate = "INSERT INTO PastPerformance VALUES(?,?,?,?)";/* ON CONFLICT(reviewID, number) DO " +
                "UPDATE SET reviewID = excluded.reviewID," +
                "number = excluded.number," +
                "objective = excluded.objective," +
                "achievement = excluded.achievement;";*/
        String FutureGoalsUpdate = "INSERT INTO FutureGoals VALUES(?,?,?)";/* ON CONFLICT(reviewID, number) DO " +
                "UPDATE SET reviewID = excluded.reviewID," +
                "number = excluded.number," +
                "content = excluded.content;";*/

        ArrayList<Date> signatures = updatedReview.getSignatures();
        PerformanceReview pastReview = updatedReview.getPastPerformance();
        PerformanceReview futureGoals = updatedReview.getFutureGoals();

        try ( PreparedStatement rUpdate = conn.prepareStatement(reviewUpdate);
                PreparedStatement ppUpdate = conn.prepareStatement(pastPerfUpdate);
                PreparedStatement fgUpdate = conn.prepareStatement(FutureGoalsUpdate)){
            rUpdate.setString(1, updatedReview.getField("Staff No"));
            rUpdate.setString(2, updatedReview.getField("Name"));
            rUpdate.setString(3, updatedReview.getField("Supervisor"));
            rUpdate.setString(4, updatedReview.getField("Second Reviewer"));
            rUpdate.setString(5, updatedReview.getField("Section"));
            rUpdate.setString(6, updatedReview.getField("Job Title"));
            rUpdate.setString(7, updatedReview.getField("Recommendation"));
            if (pastReview == null) {
                rUpdate.setNull(8, Types.VARCHAR);
            } else {
                rUpdate.setString(8, pastReview.getSummary());
            }
            if (futureGoals == null) {
                rUpdate.setNull(9, Types.VARCHAR);
            } else {
                rUpdate.setString(9, futureGoals.getSummary());
            }
            for (int i = 0; i < 3; i++) {
                if (signatures.get(i) == null) {
                    rUpdate.setNull(i+10, Types.DATE);
                } else {
                    rUpdate.setDate(i+10, signatures.get(i));
                }
            }
            rUpdate.execute();

            ppUpdate.setInt(1, updatedReview.getReviewID());
            //Loop to update all past performance.
            ArrayList<String> objectives = pastReview.getGoals();
            for (int i = 0; i < objectives.size(); i++) {
                ppUpdate.setInt(2, i);
                ppUpdate.setString(3, objectives.get(i));
                ppUpdate.setString(4, pastReview.getResult(objectives.get(i)));

                ppUpdate.execute();
            }

            fgUpdate.setInt(1, updatedReview.getReviewID());
            //Loop to update all future goals.
            ArrayList<String> goals = futureGoals.getGoals();
            for (int i = 0; i < goals.size(); i++) {
                fgUpdate.setInt(2, i);
                fgUpdate.setString(3, goals.get(i));

                fgUpdate.execute();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Deletes the specified annual review file.
     * For testing purposes only.
     * @param reviewID Which review file to delete.
     */
    static void deleteAnnualReview(Integer reviewID) {
        String sql = "DELETE FROM AnnualReview WHERE reviewID = ?;";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
            pStatement.setInt(1, reviewID);
            pStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets the specified annual review.
     * @param reviewID The reviewID of the annual review to retrieve from the database.
     * @return The annual review stored in the database, or null if not found.
     */
    static AnnualReview getAnnualReview(Integer reviewID) {
        String sql = "SELECT reviewID, staffNo, name, supervisor, secondReviewer, section, " +
                "jobTitle, recommendation, performanceSummary, reviewerComments, revieweeSigned, " +
                "supervisorSigned, secondReviewerSigned FROM AnnualReview WHERE reviewID = ?";
        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setInt(1, reviewID);
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                String recommendation = rSet.getString("recommendation");
                Recommendation recommended;
                if (recommendation != null) {
                    recommended = Recommendation.valueOf(recommendation);
                } else {
                    recommended = Recommendation.NOT_SET;
                }
                AnnualReview review = new AnnualReview(
                        rSet.getInt("reviewID"),
                        rSet.getString("staffNo"),
                        rSet.getString("name"),
                        rSet.getString("supervisor"),
                        rSet.getString("secondReviewer"),
                        rSet.getString("section"),
                        rSet.getString("jobTitle"),
                        recommended,
                        rSet.getDate("revieweeSigned"),
                        rSet.getDate("supervisorSigned"),
                        rSet.getDate("secondReviewerSigned"),
                        null,
                        null
                );
                //Add past performance and future goals related to this review.
                return findAndSetFutureGoals(findAndSetPastPerformance(review));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Gets all completed reviews corresponding to the given user.
     * @param username The username of the employee to find Annual Reviews for.
     * @return The list of that user's completed Annual Reviews.
     */
    static List<AnnualReview> getPastReviews(String username) {
        String staffNo = getStaffNo(username);
        List<AnnualReview> results = new ArrayList<>();
        String sql = "SELECT reviewID FROM AnnualReview WHERE " +
                "(revieweeSigned NOTNULL AND supervisorSigned NOTNULL AND " +
                "secondReviewerSigned NOTNULL) AND staffNo = ?;";

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, staffNo);
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()) {
                //Get each record corresponding to a returned recordID,
                //Add each found record to the list of returned records.
                results.add(getAnnualReview(rSet.getInt("reviewID")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

    /**
     * Gets all active and inactive reviews belonging to the given user.
     * @param username The username of the employee to find Annual Reviews for.
     * @return The list of that user's active and inactive Annual Reviews.
     */
    static List<AnnualReview> getReviews(String username) {
        String staffNo = getStaffNo(username);
        List<AnnualReview> results = new ArrayList<>();
        String sql = "SELECT reviewID FROM AnnualReview WHERE staffNo = ?;";

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, staffNo);
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()) {
                //Get each record corresponding to a returned recordID,
                //Add each found record to the list of returned records.
                results.add(getAnnualReview(rSet.getInt("reviewID")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return results;
    }

    /**
     * Finds past performance records related to the specified annual review.
     * Adds those records to the review, and returns the amended review.
     * Note: The summary of the past performance needs to be set separately (initialised to "").
     * @param review The annual review to find past performance records for.
     * @return The amended review.
     */
    private static AnnualReview findAndSetPastPerformance(AnnualReview review) {
        Integer reviewID = review.getReviewID();
        LinkedHashMap<String, String> pastPerf = new LinkedHashMap<>();
        String sql = "SELECT * FROM PastPerformance WHERE reviewID = ? ORDER BY number ASC;";

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setInt(1, reviewID);
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()) {
                pastPerf.put(rSet.getString("objective"), rSet.getString("achievement"));
            }

            review.setPastPerformance(new PerformanceReview(pastPerf, ""));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return review;
    }

    /**
     * Finds future goal records related to the specified annual review.
     * Adds those records to the review, and returns the amended review.
     * Note: The comments on the future goals need to be set separately (initialised to "").
     * @param review The annual review to find past performance records for.
     * @return The amended review.
     */
    private static AnnualReview findAndSetFutureGoals(AnnualReview review) {
        Integer reviewID = review.getReviewID();
        ArrayList<String> goals = new ArrayList<>();
        String sql = "SELECT * FROM FutureGoals WHERE reviewID = ? ORDER BY goalNo ASC;";

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setInt(1, reviewID);
            ResultSet rSet = pStatement.executeQuery();

            while (rSet.next()) {
                goals.add(rSet.getString("content"));
            }

            review.setPastPerformance(new PerformanceReview(false, goals, ""));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return review;
    }

    /**
     * Gets an unfinished annual review for the specified staffNo.
     * Assumes that each employee has a maximum of one unfinished review.
     * @param username The user to get an unfinished review for.
     * @return The currently active annual review of that user, or null if not found.
     */
    static AnnualReview getUnfinishedReview(String username) {
        String staffNo = getStaffNo(username);
        String currentReviewQuery = "SELECT reviewID FROM AnnualReview WHERE " +
                "(revieweeSigned IS NULL OR supervisorSigned IS NULL OR " +
                "secondReviewerSigned IS NULL) AND staffNo = ?";

        try (PreparedStatement pStatement = conn.prepareStatement(currentReviewQuery)){
            pStatement.setString(1, staffNo);
            ResultSet rSet = pStatement.executeQuery();
            if (rSet.next()) {
                return getAnnualReview(rSet.getInt("reviewID"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Gets the staff number, name and section of owners of annual reviews
     *  which are in progress but without a second reviewer.
     * @return The staffNo, name and section of employees under review without a second reviewer.
     */
    static List<Map<String, String>> reviewsWithoutASecondReviewer() {
        String sql = "SELECT staffNo, name, section FROM AnnualReview WHERE secondReviewer = NULL";
        List<Map<String, String>> result = new ArrayList<>();

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            ResultSet rSet = pStatement.executeQuery();
            while (rSet.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("staffNo", rSet.getString("staffNo"));
                row.put("name", rSet.getString("name"));
                row.put("section", rSet.getString("section"));
                result.add(row);
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Gets a list of the users with active reviews which are being reviewed by the given user.
     * @param username The username of the reviewer.
     * @return The staff number of that reviewer's active reviewees.
     */
    static List<String> listReviewees(String username) {
        String sql = "SELECT staffNo FROM AnnualReview WHERE (revieweeSigned ISNULL OR supervisorSigned ISNULL OR " +
                "secondReviewerSigned ISNULL) AND (supervisor = ? OR secondReviewer = ?);";
        List<String> reviewees = new ArrayList<>();
        String staffNo = getStaffNo(username);

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            pStatement.setString(1, staffNo);
            pStatement.setString(2, staffNo);
            ResultSet rSet = pStatement.executeQuery();
            while (rSet.next()) {
                reviewees.add(rSet.getString("staffNo"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return reviewees;
    }

    /**
     * Gets whether a user is currently reviewing another user.
     * @param username The reviewer.
     * @param targetUsername The reviewee.
     * @return Whether the first user is currently reviewing the targetUsername.
     */
    static boolean isReviewing(String username, String targetUsername) {
        List<String> reviewees = listReviewees(username);
        return reviewees.contains(getStaffNo(targetUsername));
    }

    static List<AnnualReview> getAllReviews() {
        String sql = "SELECT reviewID FROM AnnualReview";
        List<AnnualReview> result = new ArrayList<>();

        try (PreparedStatement pStatement = conn.prepareStatement(sql)){
            ResultSet rSet = pStatement.executeQuery();
            while (rSet.next()) {
                result.add(getAnnualReview(rSet.getInt("reviewID")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}
