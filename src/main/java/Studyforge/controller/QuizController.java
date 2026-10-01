package Studyforge.controller;

import Studyforge.service.QuizService;
import org.springframework.web.bind.annotation.*;
import Studyforge.model.Quiz;
import org.springframework.web.bind.annotation.RequestParam;

import Studyforge.model.GeneratedQuiz;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/quiz")
@CrossOrigin(origins = "http://localhost:5173")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/content")
    public String getCourseContent(
            @PathVariable Long courseId) {

        return quizService.getCourseContent(courseId);
    }

    @PostMapping("/generate")
    public Quiz generateQuiz(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "5") int questionCount,
            @RequestParam(defaultValue = "medium") String difficulty
    ) throws Exception {

        return quizService.generateQuiz(
                courseId,
                questionCount,
                difficulty
        );
    }

    @GetMapping
    public List<GeneratedQuiz> getSavedQuizzes(
            @PathVariable Long courseId) {

        return quizService.getSavedQuizzes(courseId);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<GeneratedQuiz> getSavedQuiz(
            @PathVariable Long courseId,
            @PathVariable Long quizId) {

        GeneratedQuiz quiz =
                quizService.getSavedQuiz(
                        courseId,
                        quizId
                );

        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteSavedQuiz(
            @PathVariable Long courseId,
            @PathVariable Long quizId) {

        boolean deleted =
                quizService.deleteSavedQuiz(
                        courseId,
                        quizId
                );

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
