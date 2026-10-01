package Studyforge.model;

import java.util.List;

public class StudySummary {

    private String title;
    private String overview;
    private List<KeyConcept> keyConcepts;
    private List<String> importantPoints;
    private List<String> reviewTopics;

    public StudySummary() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<KeyConcept> getKeyConcepts() {
        return keyConcepts;
    }

    public void setKeyConcepts(List<KeyConcept> keyConcepts) {
        this.keyConcepts = keyConcepts;
    }

    public List<String> getImportantPoints() {
        return importantPoints;
    }

    public void setImportantPoints(List<String> importantPoints) {
        this.importantPoints = importantPoints;
    }

    public List<String> getReviewTopics() {
        return reviewTopics;
    }

    public void setReviewTopics(List<String> reviewTopics) {
        this.reviewTopics = reviewTopics;
    }
}