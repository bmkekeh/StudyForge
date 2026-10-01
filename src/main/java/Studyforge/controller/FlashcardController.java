package Studyforge.controller;

import Studyforge.model.FlashcardSet;
import Studyforge.service.FlashcardService;

import org.springframework.web.bind.annotation.*;

import Studyforge.model.GeneratedFlashcardSet;
import Studyforge.repository.GeneratedFlashcardSetRepository;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/flashcards")
@CrossOrigin(origins = "http://localhost:5173")
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final GeneratedFlashcardSetRepository generatedFlashcardSetRepository;

    public FlashcardController(
            FlashcardService flashcardService,
            GeneratedFlashcardSetRepository generatedFlashcardSetRepository) {

        this.flashcardService = flashcardService;
        this.generatedFlashcardSetRepository =
                generatedFlashcardSetRepository;
    }

    @PostMapping("/generate")
    public FlashcardSet generateFlashcards(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "5")
            int flashcardCount) throws Exception {

        return flashcardService.generateFlashcards(
                courseId,
                flashcardCount
        );
    }

    @GetMapping
    public List<GeneratedFlashcardSet> getSavedFlashcards(
            @PathVariable Long courseId) {

        return generatedFlashcardSetRepository
                .findByCourseId(courseId);
    }

    @DeleteMapping("/{flashcardSetId}")
    public ResponseEntity<Void> deleteSavedFlashcards(
            @PathVariable Long courseId,
            @PathVariable Long flashcardSetId) {

        GeneratedFlashcardSet flashcardSet =
                generatedFlashcardSetRepository
                        .findById(flashcardSetId)
                        .orElse(null);

        if (flashcardSet == null) {
            return ResponseEntity.notFound().build();
        }

        // Make sure the flashcard set actually belongs
        // to the course in the URL.
        if (!flashcardSet.getCourse().getId().equals(courseId)) {
            return ResponseEntity.notFound().build();
        }

        generatedFlashcardSetRepository.delete(flashcardSet);

        return ResponseEntity.noContent().build();
    }
}
