package Studyforge.model;

import jakarta.persistence.*;

@Entity
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    public StudyMaterial() {
    }

    public StudyMaterial(String fileName, String filePath, Course course) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}
