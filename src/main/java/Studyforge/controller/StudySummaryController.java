package Studyforge.controller;

import Studyforge.model.StudySummary;
import Studyforge.model.GeneratedSummary;

import Studyforge.service.StudySummaryService;
import Studyforge.repository.GeneratedSummaryRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/summary")
@CrossOrigin(origins = "http://localhost:5173")
public class StudySummaryController {

    private final StudySummaryService studySummaryService;
    private final GeneratedSummaryRepository generatedSummaryRepository;

    public StudySummaryController(
            StudySummaryService studySummaryService,
            GeneratedSummaryRepository generatedSummaryRepository) {

        this.studySummaryService = studySummaryService;
        this.generatedSummaryRepository = generatedSummaryRepository;
    }

    // =========================
    // GENERATE SUMMARY
    // =========================

    @PostMapping("/generate")
    public StudySummary generateSummary(
            @PathVariable Long courseId)
            throws Exception {

        return studySummaryService.generateSummary(courseId);
    }

    // =========================
    // GET SAVED SUMMARIES
    // =========================

    @GetMapping
    public List<GeneratedSummary> getSavedSummaries(
            @PathVariable Long courseId) {

        return generatedSummaryRepository
                .findByCourseId(courseId);
    }

    // =========================
    // DELETE SAVED SUMMARY
    // =========================

    @DeleteMapping("/{summaryId}")
    public ResponseEntity<Void> deleteSavedSummary(
            @PathVariable Long courseId,
            @PathVariable Long summaryId) {

        GeneratedSummary summary =
                generatedSummaryRepository
                        .findById(summaryId)
                        .orElse(null);

        if (summary == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent deleting a summary belonging
        // to a different course.
        if (!summary.getCourse().getId().equals(courseId)) {
            return ResponseEntity.notFound().build();
        }

        generatedSummaryRepository.delete(summary);

        return ResponseEntity.noContent().build();
    }
}