import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A past performance review or a preview of future performance in an Annual Review.
 * @author Marin md485
 * @version 20190324
 */
public class PerformanceReview {
    private Boolean past;
    private String summary;
    private ArrayList<String> goals;
    private LinkedHashMap<String, String> achievements;

    PerformanceReview (boolean past, ArrayList<String> objectives, String summary) {
        this.past = past;
        this.summary = summary;
        if (!past) {
            goals = objectives;
            achievements = null;
        } else {
            achievements = new LinkedHashMap<>();
            for (int i = 0; i < objectives.size(); i++) {
                achievements.put(objectives.get(i), "");
            }
            goals = null;
        }
    }

    PerformanceReview (LinkedHashMap<String, String> pastGoals, String summary) {
        past = true;
        this.summary = summary;
        achievements = pastGoals;
        goals = null;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if (past) {
            int iterator = 1;
            for(Map.Entry<String, String> goalAndResult : achievements.entrySet()) {
                result.append("No :");
                result.append(iterator++);
                result.append("\n");
                result.append("Goal :");
                result.append(goalAndResult.getKey());
                result.append("\n");
                result.append("Result :");
                result.append(goalAndResult.getValue());
                result.append("\n");
            }
        } else {
            for (int i = 0; i < goals.size(); i++) {
                result.append("No :");
                result.append(i);
                result.append("\n");
                result.append("Goal :");
                result.append(goals.get(i));
                result.append("\n");
            }
        }
        result.append("Summary:");
        result.append(summary);
        result.append("\n");
        return result.toString();
    }

    Boolean getPast() {
        return past;
    }

    ArrayList<String> getGoals() {
        return goals;
    }

    void addGoal(String goal) {
        goals.add(goal);
    }

    void removeGoal(int index) {
        if (!past) {
            goals.remove(index);
        }
    }

    String getResult(String goal) {
        return achievements.get(goal);
    }

    void updateResult(String goal, String result) {
        if (achievements.get(goal) != null) {
            achievements.put(goal, result);
        }
    }

    String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }
}
