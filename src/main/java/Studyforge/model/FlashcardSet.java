package Studyforge.model;

import java.util.List;

public class FlashcardSet {

    private List<Flashcard> flashcards;

    public FlashcardSet() {
    }

    public FlashcardSet(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}