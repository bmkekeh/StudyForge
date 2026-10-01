package Studyforge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    /*
     * When a course is deleted, its study materials
     * should also be deleted.
     */
    @JsonIgnore
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StudyMaterial> studyMaterials =
            new ArrayList<>();

    /*
     * Delete generated quizzes when the course
     * is deleted.
     */
    @JsonIgnore
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GeneratedQuiz> generatedQuizzes =
            new ArrayList<>();

    /*
     * Delete generated flashcard sets when the
     * course is deleted.
     */
    @JsonIgnore
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GeneratedFlashcardSet> generatedFlashcardSets =
            new ArrayList<>();

    /*
     * Delete generated summaries when the course
     * is deleted.
     */
    @JsonIgnore
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GeneratedSummary> generatedSummaries =
            new ArrayList<>();

    public Course() {
    }

    public Course(
            String name,
            String description
    ) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public List<StudyMaterial> getStudyMaterials() {
        return studyMaterials;
    }

    public void setStudyMaterials(
            List<StudyMaterial> studyMaterials
    ) {
        this.studyMaterials = studyMaterials;
    }

    public List<GeneratedQuiz> getGeneratedQuizzes() {
        return generatedQuizzes;
    }

    public void setGeneratedQuizzes(
            List<GeneratedQuiz> generatedQuizzes
    ) {
        this.generatedQuizzes = generatedQuizzes;
    }

    public List<GeneratedFlashcardSet>
    getGeneratedFlashcardSets() {
        return generatedFlashcardSets;
    }

    public void setGeneratedFlashcardSets(
            List<GeneratedFlashcardSet>
                    generatedFlashcardSets
    ) {
        this.generatedFlashcardSets =
                generatedFlashcardSets;
    }

    public List<GeneratedSummary>
    getGeneratedSummaries() {
        return generatedSummaries;
    }

    public void setGeneratedSummaries(
            List<GeneratedSummary>
                    generatedSummaries
    ) {
        this.generatedSummaries =
                generatedSummaries;
    }
}