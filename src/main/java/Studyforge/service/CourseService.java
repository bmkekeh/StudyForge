package Studyforge.service;

import java.util.List;
import Studyforge.model.Course;
import Studyforge.repository.CourseRepository;
import org.springframework.stereotype.Service;

import Studyforge.model.StudyMaterial;
import Studyforge.repository.StudyMaterialRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service // tells Spring: This class is a service that Spring should create and manage.
public class CourseService {

    private final CourseRepository courseRepository;// calling CourseRepository because CourseService needs a way to talk to the database, and CourseRepository is the layer for my database

    private final StudyMaterialRepository studyMaterialRepository;

    public CourseService(
            CourseRepository courseRepository,
            StudyMaterialRepository studyMaterialRepository) {

        this.courseRepository = courseRepository;
        this.studyMaterialRepository = studyMaterialRepository;
    }
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course>getAllCourses(){
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id){
        return courseRepository.findById(id).orElse(null);
    }

    public Course updateCourse(Long id, Course updatedCourse) {

        Course course = courseRepository.findById(id).orElse(null);

        if (course == null) {
            return null;
        }

        course.setName(updatedCourse.getName());
        course.setDescription(updatedCourse.getDescription());

        return courseRepository.save(course);
    }


    public boolean deleteCourse(Long courseId) {

        Course course = courseRepository
                .findById(courseId)
                .orElse(null);

        if (course == null) {
            return false;
        }

        List<StudyMaterial> materials =
                studyMaterialRepository.findByCourseId(courseId);

        // Delete physical PDF files
        for (StudyMaterial material : materials) {
            try {
                Files.deleteIfExists(
                        Paths.get(material.getFilePath())
                );
            } catch (IOException e) {
                throw new RuntimeException(
                        "Could not delete study material file.",
                        e
                );
            }
        }

        // Delete materials first because they reference Course
        studyMaterialRepository.deleteAll(materials);

        // Now the course can safely be deleted
        courseRepository.delete(course);

        return true;
    }
}
