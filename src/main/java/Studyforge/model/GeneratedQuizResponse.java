package Studyforge.model;

public class GeneratedQuizResponse {

    private Long quizId;
    private Quiz quiz;

    public GeneratedQuizResponse() {
    }

    public GeneratedQuizResponse(
            Long quizId,
            Quiz quiz
    ) {
        this.quizId = quizId;
        this.quiz = quiz;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
