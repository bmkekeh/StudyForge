package Studyforge.model;

import jakarta.persistence.*;

@Entity
public class QuizMistake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String question;

    @ManyToOne
    @JoinColumn(
            name = "quiz_attempt_id",
            nullable = false
    )
    private QuizAttempt quizAttempt;

    public QuizMistake() {
    }

    public QuizMistake(
            String topic,
            String question,
            QuizAttempt quizAttempt
    ) {
        this.topic = topic;
        this.question = question;
        this.quizAttempt = quizAttempt;
    }

    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }

    public void setQuizAttempt(
            QuizAttempt quizAttempt
    ) {
        this.quizAttempt = quizAttempt;
    }
}