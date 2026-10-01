package Studyforge.repository;

import Studyforge.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    /*
    By extending JpaRepository, Spring gives us methods such a
courseRepository.save(course);
courseRepository.findAll();
courseRepository.findById(id);
courseRepository.deleteById(id);
     */

}
