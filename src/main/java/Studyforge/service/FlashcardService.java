package Studyforge.service;

import Studyforge.model.FlashcardSet;
import Studyforge.model.StudyMaterial;
import Studyforge.repository.StudyMaterialRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import Studyforge.model.GeneratedFlashcardSet;
import Studyforge.model.Course;

import Studyforge.repository.GeneratedFlashcardSetRepository;
import Studyforge.repository.CourseRepository;

@Service
public class FlashcardService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final GeneratedFlashcardSetRepository generatedFlashcardSetRepository;
    private final CourseRepository courseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlashcardService(
            StudyMaterialRepository studyMaterialRepository,
            GeneratedFlashcardSetRepository generatedFlashcardSetRepository,
            CourseRepository courseRepository) {

        this.studyMaterialRepository = studyMaterialRepository;
        this.generatedFlashcardSetRepository =
                generatedFlashcardSetRepository;
        this.courseRepository = courseRepository;
    }

    public String getCourseContent(Long courseId) {

        List<StudyMaterial> materials =
                studyMaterialRepository.findByCourseId(courseId);

        StringBuilder content = new StringBuilder();

        for (StudyMaterial material : materials) {

            if (material.getExtractedText() != null) {
                content.append(material.getExtractedText());
                content.append("\n\n");
            }
        }

        return content.toString();
    }

    public FlashcardSet generateFlashcards(
            Long courseId,
            int flashcardCount) throws Exception {

        if (flashcardCount < 1 || flashcardCount > 20) {
            throw new IllegalArgumentException(
                    "Flashcard count must be between 1 and 20."
            );
        }

        String courseContent = getCourseContent(courseId);

        if (courseContent.isBlank()) {
            throw new IllegalStateException(
                    "No study material found for this course."
            );
        }

        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not configured."
            );
        }

        String prompt = """
                Create exactly %d study flashcards using ONLY the
                academic concepts contained in the study material below.

                Focus on important concepts the student should understand.

                Ignore:
                - student scores and grades
                - dates and times
                - URLs
                - page numbers
                - administrative information
                - grading totals

                Return ONLY valid JSON using exactly this structure:

                {
                  "flashcards": [
                    {
                      "front": "Question or concept",
                      "back": "Answer or explanation"
                    }
                  ]
                }

                Requirements:
                - Include exactly %d flashcards.
                - Keep the front concise.
                - Keep the back clear and useful for studying.
                - Do not use Markdown.
                - Do not include anything before or after the JSON.
                - Do not introduce facts unsupported by the study material.

                STUDY MATERIAL:
                %s
                """.formatted(
                flashcardCount,
                flashcardCount,
                courseContent
        );

        String requestBody = """
                {
                  "model": "gpt-5.6-luna",
                  "input": %s
                }
                """.formatted(toJsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new RuntimeException(
                    "OpenAI request failed: " + response.body()
            );
        }

        JsonNode root =
                objectMapper.readTree(response.body());

        for (JsonNode outputItem : root.path("output")) {

            if ("message".equals(
                    outputItem.path("type").asText())) {

                for (JsonNode contentItem :
                        outputItem.path("content")) {

                    if ("output_text".equals(
                            contentItem.path("type").asText())) {

                        String jsonText =
                                contentItem.path("text").asText();

                        FlashcardSet flashcardSet =
                                objectMapper.readValue(
                                        jsonText,
                                        FlashcardSet.class
                                );

                        Course course = courseRepository
                                .findById(courseId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Course not found."
                                        )
                                );

                        GeneratedFlashcardSet generatedFlashcardSet =
                                new GeneratedFlashcardSet(
                                        "Generated Flashcards",
                                        jsonText,
                                        course
                                );

                        generatedFlashcardSetRepository.save(
                                generatedFlashcardSet
                        );

                        return flashcardSet;
                    }
                }
            }
        }

        throw new RuntimeException(
                "OpenAI response did not contain flashcards."
        );
    }

    private String toJsonString(String text) {

        return "\"" + text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }
}