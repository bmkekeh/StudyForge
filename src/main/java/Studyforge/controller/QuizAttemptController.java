package Studyforge.controller;

import Studyforge.model.QuizAttempt;
import Studyforge.service.QuizAttemptService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /*
     * Save a completed quiz attempt.
     *
     * Example:
     * POST /courses/2/quiz-attempts
     *
     * {
     *   "quizId": 5,
     *   "score": 4,
     *   "totalQuestions": 5
     * }
     */
    @PostMapping
    public QuizAttempt saveAttempt(
            @PathVariable Long courseId,
            @RequestBody QuizAttemptRequest request
    ) {
        return quizAttemptService.saveAttempt(
                courseId,
                request.getQuizId(),
                request.getScore(),
                request.getTotalQuestions()
        );
    }

    /*
     * Get all attempts for a course.
     */
    @GetMapping
    public List<QuizAttempt> getAttempts(
            @PathVariable Long courseId
    ) {
        return quizAttemptService
                .getAttemptsForCourse(courseId);
    }

    /*
     * Request body used when saving an attempt.
     */
    public static class QuizAttemptRequest {

        private Long quizId;
        private int score;
        private int totalQuestions;

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
    }
}