package Studyforge.controller;

import Studyforge.model.QuizAttempt;
import Studyforge.model.QuizAttemptStats;
import Studyforge.service.QuizAttemptService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import Studyforge.model.WeakTopic;

@RestController
@RequestMapping("/courses/{courseId}/quiz-attempts")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(
            QuizAttemptService quizAttemptService
    ) {
        this.quizAttemptService = quizAttemptService;
    }

    /* =========================
       SAVE ATTEMPT
    ========================= */

    @PostMapping
    public QuizAttempt saveAttempt(
            @PathVariable Long courseId,
            @RequestBody QuizAttemptRequest request
    ) {
        return quizAttemptService.saveAttempt(
                courseId,
                request.getQuizId(),
                request.getScore(),
                request.getTotalQuestions(),
                request.getMistakes()
        );
    }

    /* =========================
       GET COURSE ATTEMPTS
    ========================= */

    @GetMapping
    public List<QuizAttempt> getAttempts(
            @PathVariable Long courseId
    ) {
        return quizAttemptService
                .getAttemptsForCourse(courseId);
    }

    /* =========================
       GET COURSE STATS
    ========================= */

    @GetMapping("/stats")
    public QuizAttemptStats getStats(
            @PathVariable Long courseId
    ) {
        return quizAttemptService
                .getStats(courseId);
    }

    /* =========================
       REQUEST BODY
    ========================= */

    public static class QuizAttemptRequest {

        private Long quizId;
        private int score;
        private int totalQuestions;

        private List<MistakeRequest> mistakes =
                new ArrayList<>();

        public QuizAttemptRequest() {
        }

        public Long getQuizId() {
            return quizId;
        }

        public void setQuizId(Long quizId) {
            this.quizId = quizId;
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

        public void setTotalQuestions(
                int totalQuestions
        ) {
            this.totalQuestions = totalQuestions;
        }

        public List<MistakeRequest> getMistakes() {
            return mistakes;
        }

        public void setMistakes(
                List<MistakeRequest> mistakes
        ) {
            this.mistakes =
                    mistakes == null
                            ? new ArrayList<>()
                            : mistakes;
        }
    }

    /* =========================
       MISTAKE REQUEST
    ========================= */

    public static class MistakeRequest {

        private String topic;
        private String question;

        public MistakeRequest() {
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
    }

    /* =========================
   GET WEAK TOPICS
========================= */

    @GetMapping("/weak-topics")
    public List<WeakTopic> getWeakTopics(
            @PathVariable Long courseId
    ) {
        return quizAttemptService
                .getWeakTopics(courseId);
    }
}