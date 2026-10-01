package Studyforge.repository;

import Studyforge.model.GeneratedSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedSummaryRepository
        extends JpaRepository<GeneratedSummary, Long> {

    List<GeneratedSummary> findByCourseId(Long courseId);
}