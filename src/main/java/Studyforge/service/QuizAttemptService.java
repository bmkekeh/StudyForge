package Studyforge.service;

import Studyforge.controller.QuizAttemptController.MistakeRequest;

import Studyforge.model.Course;
import Studyforge.model.GeneratedQuiz;
import Studyforge.model.QuizAttempt;
import Studyforge.model.QuizAttemptStats;
import Studyforge.model.QuizMistake;

import Studyforge.repository.CourseRepository;
import Studyforge.repository.GeneratedQuizRepository;
import Studyforge.repository.QuizAttemptRepository;
import Studyforge.repository.QuizMistakeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import Studyforge.model.WeakTopic;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizMistakeRepository quizMistakeRepository;
    private final CourseRepository courseRepository;
    private final GeneratedQuizRepository generatedQuizRepository;

    public QuizAttemptService(
            QuizAttemptRepository quizAttemptRepository,
            QuizMistakeRepository quizMistakeRepository,
            CourseRepository courseRepository,
            GeneratedQuizRepository generatedQuizRepository
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizMistakeRepository = quizMistakeRepository;
        this.courseRepository = courseRepository;
        this.generatedQuizRepository = generatedQuizRepository;
    }

    /* =========================
       SAVE ATTEMPT
    ========================= */

    @Transactional
    public QuizAttempt saveAttempt(
            Long courseId,
            Long quizId,
            int score,
            int totalQuestions,
            List<MistakeRequest> mistakes
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

        QuizAttempt savedAttempt =
                quizAttemptRepository.save(attempt);

        /* =========================
           SAVE MISTAKES
        ========================= */

        if (mistakes != null) {

            for (MistakeRequest mistakeRequest : mistakes) {

                if (
                        mistakeRequest == null ||
                                mistakeRequest.getQuestion() == null ||
                                mistakeRequest
                                        .getQuestion()
                                        .isBlank()
                ) {
                    continue;
                }

                QuizMistake mistake =
                        new QuizMistake(
                                mistakeRequest.getTopic(),
                                mistakeRequest.getQuestion(),
                                savedAttempt
                        );

                quizMistakeRepository.save(mistake);
            }
        }

        return savedAttempt;
    }

    /* =========================
       GET COURSE ATTEMPTS
    ========================= */

    public List<QuizAttempt> getAttemptsForCourse(
            Long courseId
    ) {
        return quizAttemptRepository
                .findByCourseIdOrderByCompletedAtDesc(
                        courseId
                );
    }

    /* =========================
       GET QUIZ ATTEMPTS
    ========================= */

    public List<QuizAttempt> getAttemptsForQuiz(
            Long quizId
    ) {
        return quizAttemptRepository
                .findByQuizIdOrderByCompletedAtDesc(
                        quizId
                );
    }

    /* =========================
       COURSE STATISTICS
    ========================= */

    public QuizAttemptStats getStats(
            Long courseId
    ) {

        List<QuizAttempt> attempts =
                quizAttemptRepository
                        .findByCourseIdOrderByCompletedAtDesc(
                                courseId
                        );

        if (attempts.isEmpty()) {
            return new QuizAttemptStats(
                    0,
                    0.0,
                    0.0,
                    0.0
            );
        }

        double totalPercentage = 0.0;
        double bestScore = 0.0;

        for (QuizAttempt attempt : attempts) {

            double percentage =
                    attempt.getScore()
                            * 100.0
                            / attempt.getTotalQuestions();

            totalPercentage += percentage;

            if (percentage > bestScore) {
                bestScore = percentage;
            }
        }

        double averageScore =
                totalPercentage / attempts.size();

        QuizAttempt latestAttempt =
                attempts.get(0);

        double latestScore =
                latestAttempt.getScore()
                        * 100.0
                        / latestAttempt.getTotalQuestions();

        averageScore =
                roundToOneDecimal(averageScore);

        bestScore =
                roundToOneDecimal(bestScore);

        latestScore =
                roundToOneDecimal(latestScore);

        return new QuizAttemptStats(
                attempts.size(),
                averageScore,
                bestScore,
                latestScore
        );
    }

    /* =========================
       HELPER
    ========================= */

    private double roundToOneDecimal(
            double value
    ) {
        return Math.round(
                value * 10.0
        ) / 10.0;
    }

    public List<WeakTopic> getWeakTopics(
            Long courseId
    ) {

        List<QuizMistake> mistakes =
                quizMistakeRepository
                        .findByQuizAttemptCourseId(
                                courseId
                        );

        Map<String, Long> topicCounts =
                new HashMap<>();

        for (QuizMistake mistake : mistakes) {

            String topic = mistake.getTopic();

            // Ignore mistakes created before
            // topic tracking was added.
            if (
                    topic == null ||
                            topic.isBlank()
            ) {
                continue;
            }

            String normalizedTopic =
                    topic.trim();

            topicCounts.put(
                    normalizedTopic,
                    topicCounts.getOrDefault(
                            normalizedTopic,
                            0L
                    ) + 1
            );
        }

        List<WeakTopic> weakTopics =
                new ArrayList<>();

        for (
                Map.Entry<String, Long> entry :
                topicCounts.entrySet()
        ) {
            weakTopics.add(
                    new WeakTopic(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        weakTopics.sort(
                (a, b) ->
                        Long.compare(
                                b.getMistakeCount(),
                                a.getMistakeCount()
                        )
        );

        return weakTopics;
    }
}
