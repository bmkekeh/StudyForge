package Studyforge.repository;

import Studyforge.model.GeneratedFlashcardSet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedFlashcardSetRepository
        extends JpaRepository<GeneratedFlashcardSet, Long> {

    List<GeneratedFlashcardSet> findByCourseId(Long courseId);
}