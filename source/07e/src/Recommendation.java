public enum Recommendation {
    STAY_IN_POST("Stay In Post"),
    SALARY_INCREASE("Salary Increase"),
    PROMOTION("Promotion"),
    PROBATION("Probation"),
    TERMINATION("Termination"),
    NOT_SET("Recommendation Not Set");

    private final String recommendation;

    Recommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String toString() {
        return recommendation;
    }

}
