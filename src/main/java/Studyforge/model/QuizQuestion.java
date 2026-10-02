package Studyforge.model;

import java.util.List;

public class QuizQuestion {

    private String topic;

    private String question;

    private List<String> options;

    private int correctAnswer;

    private String explanation;

    public QuizQuestion() {
    }

    public QuizQuestion(
            String topic,
            String question,
            List<String> options,
            int correctAnswer,
            String explanation
    ) {
        this.topic = topic;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
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

    public void setQuestion(
            String question
    ) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(
            List<String> options
    ) {
        this.options = options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(
            int correctAnswer
    ) {
        this.correctAnswer =
                correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(
            String explanation
    ) {
        this.explanation =
                explanation;
    }
}