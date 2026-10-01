package Studyforge.repository;

import Studyforge.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudyMaterialRepository
        extends JpaRepository<StudyMaterial, Long> {
       /*
    By extending JpaRepository, Spring gives us methods such a
save(material);
findAll();
findById(id);
deleteById(id);
     */

    List<StudyMaterial> findByCourseId(Long courseId);
}