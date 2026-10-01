package Studyforge.model;

import java.util.List;

public class Quiz {

    private List<QuizQuestion> questions;

    public Quiz() {
    }

    public Quiz(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }
}