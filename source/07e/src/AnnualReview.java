import java.util.*;
import java.util.logging.Logger;
import java.sql.Date;

/**
 * The contents of an annual review file.
 * This class represents the data retrieved from the HRDatabase as it is passed within the system.
 * Changes made to this object will not be reflected in the HRDatabase unless amendAnnualReview
 *  is explicitly called and authorised.
 * @author James jd556, Marin md485
 * @version 20190323
 */
public class AnnualReview {
    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());
    private Integer reviewID;
    private LinkedHashMap<String, String> details;
    private LinkedHashMap<String, Date> signatures;
    private LinkedHashMap<String, PerformanceReview> reviews;

    AnnualReview() {
        reviewID = null;
        details = new LinkedHashMap<>();
        signatures = new LinkedHashMap<>();
        reviews = new LinkedHashMap<>();

        details.put("Staff No", null);
        details.put("Name", null);
        details.put("Supervisor", null);
        details.put("Second Reviewer", null);
        details.put("Section", null);
        details.put("Job Title", null);
        details.put("Recommendation", null);

        signatures.put("Reviewee Signed", null);
        signatures.put("Supervisor Signed", null);
        signatures.put("Second Reviewer Signed", null);

        reviews.put("A review of past performance: achievements and outcomes", null);
        reviews.put("A preview of future performance: goals/planned outcomes", null);
    }

    AnnualReview(Integer reviewID, String staffNo, String name, String supervisorNo,
                 String secondReviewerNo, String section, String jobTitle,
                 Recommendation recommendation, Date revieweeSigned, Date supervisorSigned,
                 Date secondReviewerSigned, PerformanceReview past, PerformanceReview future) {
        this.reviewID = reviewID;
        details = new LinkedHashMap<>();
        signatures = new LinkedHashMap<>();
        reviews = new LinkedHashMap<>();

        details.put("Staff No", staffNo);
        details.put("Name", name);
        details.put("Supervisor", supervisorNo);
        details.put("Second Reviewer", secondReviewerNo);
        details.put("Section", section);
        details.put("Job Title", jobTitle);
        details.put("Recommendation", recommendation.toString());

        signatures.put("Reviewee Signed", revieweeSigned);
        signatures.put("Supervisor Signed", supervisorSigned);
        signatures.put("Second Reviewer Signed", secondReviewerSigned);

        reviews.put("A review of past performance: achievements and outcomes", past);
        reviews.put("A preview of future performance: goals/planned outcomes", future);

    }

    LinkedHashMap<String, String> getAllDetails() {
        return details;
    }

    private <T> String printHashMapDetails(LinkedHashMap<String, T> input) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, T> field : input.entrySet()) {
            result.append(field.getKey());
            if (field.getValue() instanceof PerformanceReview) {
                result.append("\n");
            } else {
                result.append(": ");
            }
            if (field.getValue() != null) {
                result.append(field.getValue().toString());
            }
            result.append("\n");
        }
        return result.toString();
    }

    String printAllDetails() {
        StringBuilder printDetails = new StringBuilder();
        printDetails.append(printHashMapDetails(details));
        printDetails.append(printHashMapDetails(reviews));
        printDetails.append(printHashMapDetails(signatures));
        return printDetails.toString();
    }


    LinkedHashSet<String> returnFields() { return new LinkedHashSet<>(details.keySet()); }

    String getField(String field) {
        return details.get(field);
    }

    Boolean setField(String field, String entry) {
        if (field == null || entry == null) {
            return false;
        } else {
            return (entry.equals(details.put(field, entry)));
        }
    }

    /**
     * Returns the reviewID of this AnnualReview, or null if it isn't in the HR Database.
     * @return The ID of this annual review
     */
    Integer getReviewID() {
        return reviewID;
    }

    ArrayList<Date> getSignatures() {
        return new ArrayList<>(signatures.values());
    }

    void setPastPerformance(PerformanceReview pastPerf) {
        reviews.put("A review of past performance: achievements and outcomes", pastPerf);
    }

    void setFutureGoals(PerformanceReview futureGoals) {
        reviews.put("A preview of future performance: goals/planned outcomes", futureGoals);
    }

    PerformanceReview getPastPerformance() {
        return reviews.get("A review of past performance: achievements and outcomes");
    }

    PerformanceReview getFutureGoals() {
        return reviews.get("A preview of future performance: goals/planned outcomes");
    }

    Date getDate() {
        if (isComplete()) {
            return signatures.values().stream().max(java.sql.Date::compareTo).get();
        } else {
            return null;
        }
    }

    /**
     * Checks whether this Annual Review has been signed by all participants.
     * @return Whether all 3 participants have signed this review.
     */
    boolean isComplete() {
        return signatures.get("Reviewee") != null
                && signatures.get("Supervisor") != null
                && signatures.get("Second Reviewer") != null;
    }

    /**
     * Signs this review as the given user.
     * @param signee The staff number of the user to sign off the review as.
     */
    boolean signOff(String signee) {
        if (!isComplete()) {
            if (signee.equals(details.get("Staff No"))) {
                signatures.put("Reviewee", new Date(System.currentTimeMillis()));
            } else if (signee.equals(details.get("Supervisor"))) {
                signatures.put("Supervisor", new Date(System.currentTimeMillis()));
            } else if (signee.equals(details.get("Second Reviewer"))) {
                signatures.put("Second Reviewer", new Date(System.currentTimeMillis()));
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Signs this review as the given user.
     * @param username The user to sign off the review as.
     */
    void signReview(String username) {
        String staffNo = DatabaseController.getStaffNo(username);
        if(staffNo != null) {
            if (staffNo.equals(details.get("Staff No"))) {
                //User is the reviewee.
                signatures.put("Staff No", new Date(System.currentTimeMillis()));
            } else if (staffNo.equals(details.get("Supervisor"))) {
                //User is the related supervisor.
                signatures.put("Supervisor", new Date(System.currentTimeMillis()));
            } else if (staffNo.equals(details.get("Second Reviewer"))) {
                //User is the second reviewer.
                signatures.put("SecondReviewer", new Date(System.currentTimeMillis()));
            }
        }
    }

    /**
     * Resets the signatures on this annual review file to null if it isn't complete.
     * @return Whether the signatures were reset.
     */
    boolean resetSignatures() {
        if (!isComplete()) {
            signatures.put("Reviewee", null);
            signatures.put("Supervisor", null);
            signatures.put("Second Reviewer", null);
            return true;
        } else {
            return false;
        }
    }
}
