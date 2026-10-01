package Studyforge.service;

import Studyforge.model.Course;
import Studyforge.model.GeneratedQuiz;
import Studyforge.model.QuizAttempt;

import Studyforge.repository.CourseRepository;
import Studyforge.repository.GeneratedQuizRepository;
import Studyforge.repository.QuizAttemptRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final CourseRepository courseRepository;
    private final GeneratedQuizRepository generatedQuizRepository;

    public QuizAttemptService(
            QuizAttemptRepository quizAttemptRepository,
            CourseRepository courseRepository,
            GeneratedQuizRepository generatedQuizRepository
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.courseRepository = courseRepository;
        this.generatedQuizRepository = generatedQuizRepository;
    }

    public QuizAttempt saveAttempt(
            Long courseId,
            Long quizId,
            int score,
            int totalQuestions
    ) {

        if (score < 0) {
            throw new IllegalArgumentException(
                    "Score cannot be negative."
            );
        }

        if (totalQuestions < 1) {
            throw new IllegalArgumentException(
                    "Total questions must be at least 1."
            );
        }

        if (score > totalQuestions) {
            throw new IllegalArgumentException(
                    "Score cannot be greater than total questions."
            );
        }

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found."
                        )
                );

        GeneratedQuiz quiz = generatedQuizRepository
                .findById(quizId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Quiz not found."
                        )
                );

        /*
         * Make sure the quiz actually belongs
         * to the requested course.
         */
        if (!quiz.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException(
                    "Quiz does not belong to this course."
            );
        }

        QuizAttempt attempt = new QuizAttempt(
                score,
                totalQuestions,
                course,
                quiz
        );

        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getAttemptsForCourse(
            Long courseId
    ) {
        return quizAttemptRepository
                .findByCourseIdOrderByCompletedAtDesc(courseId);
    }

    public List<QuizAttempt> getAttemptsForQuiz(
            Long quizId
    ) {
        return quizAttemptRepository
                .findByQuizIdOrderByCompletedAtDesc(quizId);
    }
}
