package Studyforge.service;

import Studyforge.model.StudyMaterial;
import Studyforge.model.StudySummary;
import Studyforge.model.GeneratedSummary;
import Studyforge.model.Course;

import Studyforge.repository.StudyMaterialRepository;
import Studyforge.repository.GeneratedSummaryRepository;
import Studyforge.repository.CourseRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class StudySummaryService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final GeneratedSummaryRepository generatedSummaryRepository;
    private final CourseRepository courseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StudySummaryService(
            StudyMaterialRepository studyMaterialRepository,
            GeneratedSummaryRepository generatedSummaryRepository,
            CourseRepository courseRepository) {

        this.studyMaterialRepository = studyMaterialRepository;
        this.generatedSummaryRepository = generatedSummaryRepository;
        this.courseRepository = courseRepository;
    }

    // =========================
    // GET COURSE CONTENT
    // =========================

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

    // =========================
    // GENERATE SUMMARY
    // =========================

    public StudySummary generateSummary(Long courseId)
            throws Exception {

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
                Create a study summary using ONLY the academic
                information contained in the study material below.

                Focus on concepts and information that would be useful
                for a student reviewing the material.

                Ignore:
                - student scores and grades
                - dates and times
                - URLs
                - page numbers
                - administrative information
                - grading totals

                Return ONLY valid JSON using exactly this structure:

                {
                  "title": "Study Summary",
                  "overview": "A concise overview of the material",
                  "keyConcepts": [
                    {
                      "concept": "Concept name",
                      "explanation": "Clear explanation"
                    }
                  ],
                  "importantPoints": [
                    "Important point"
                  ],
                  "reviewTopics": [
                    "Topic the student should review"
                  ]
                }

                Requirements:
                - Base every factual statement on the provided material.
                - Do not introduce outside facts or definitions.
                - If the material only names a concept without explaining
                  it, identify it as a review topic instead of inventing
                  an explanation.
                - Focus on academic content rather than grading feedback.
                - Keep the overview concise.
                - Include the most important concepts supported by the material.
                - Avoid unnecessary repetition.
                - Do not use Markdown.
                - Do not include anything before or after the JSON.

                STUDY MATERIAL:
                %s
                """.formatted(courseContent);

        String requestBody = """
                {
                  "model": "gpt-5.6-luna",
                  "input": %s
                }
                """.formatted(toJsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://api.openai.com/v1/responses"
                ))
                .header(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(requestBody)
                )
                .build();

        HttpClient client =
                HttpClient.newHttpClient();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new RuntimeException(
                    "OpenAI request failed: "
                            + response.body()
            );
        }

        JsonNode root =
                objectMapper.readTree(
                        response.body()
                );

        for (JsonNode outputItem :
                root.path("output")) {

            if ("message".equals(
                    outputItem
                            .path("type")
                            .asText())) {

                for (JsonNode contentItem :
                        outputItem.path("content")) {

                    if ("output_text".equals(
                            contentItem
                                    .path("type")
                                    .asText())) {

                        String jsonText =
                                contentItem
                                        .path("text")
                                        .asText();

                        // Convert JSON into StudySummary
                        // for the frontend.
                        StudySummary studySummary =
                                objectMapper.readValue(
                                        jsonText,
                                        StudySummary.class
                                );

                        // Find the course that this
                        // summary belongs to.
                        Course course =
                                courseRepository
                                        .findById(courseId)
                                        .orElseThrow(() ->
                                                new IllegalArgumentException(
                                                        "Course not found."
                                                )
                                        );

                        // Save the original JSON in
                        // PostgreSQL.
                        GeneratedSummary generatedSummary =
                                new GeneratedSummary(
                                        studySummary.getTitle(),
                                        jsonText,
                                        course
                                );

                        generatedSummaryRepository.save(
                                generatedSummary
                        );

                        // Return the parsed summary
                        // to React.
                        return studySummary;
                    }
                }
            }
        }

        throw new RuntimeException(
                "OpenAI response did not contain a study summary."
        );
    }

    // =========================
    // JSON STRING HELPER
    // =========================

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