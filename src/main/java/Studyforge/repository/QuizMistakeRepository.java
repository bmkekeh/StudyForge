package Studyforge.repository;

import Studyforge.model.QuizMistake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizMistakeRepository
        extends JpaRepository<QuizMistake, Long> {

    List<QuizMistake>
    findByQuizAttemptCourseId(Long courseId);

    List<QuizMistake>
    findByQuizAttemptId(Long quizAttemptId);
}
