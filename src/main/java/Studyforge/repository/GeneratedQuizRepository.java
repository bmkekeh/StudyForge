package Studyforge.repository;

import Studyforge.model.GeneratedQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedQuizRepository
        extends JpaRepository<GeneratedQuiz, Long> {

    List<GeneratedQuiz> findByCourseId(Long courseId);
}