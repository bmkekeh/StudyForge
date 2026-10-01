package Studyforge.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    private int totalQuestions;

    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private GeneratedQuiz quiz;

    public QuizAttempt() {
    }

    public QuizAttempt(
            int score,
            int totalQuestions,
            Course course,
            GeneratedQuiz quiz
    ) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.course = course;
        this.quiz = quiz;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public GeneratedQuiz getQuiz() {
        return quiz;
    }

    public void setQuiz(GeneratedQuiz quiz) {
        this.quiz = quiz;
    }
}
