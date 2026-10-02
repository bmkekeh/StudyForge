package Studyforge.model;

public class QuizAttemptStats {

    private long attemptsCompleted;
    private double averageScore;
    private double bestScore;
    private double latestScore;

    public QuizAttemptStats(
            long attemptsCompleted,
            double averageScore,
            double bestScore,
            double latestScore
    ) {
        this.attemptsCompleted = attemptsCompleted;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.latestScore = latestScore;
    }

    public long getAttemptsCompleted() {
        return attemptsCompleted;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getBestScore() {
        return bestScore;
    }

    public double getLatestScore() {
        return latestScore;
    }
}