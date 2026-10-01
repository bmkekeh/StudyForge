package Studyforge.repository;

import Studyforge.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByCourseIdOrderByCompletedAtDesc(
            Long courseId
    );

    List<QuizAttempt> findByQuizIdOrderByCompletedAtDesc(
            Long quizId
    );
}