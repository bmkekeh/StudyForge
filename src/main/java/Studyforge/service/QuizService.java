package Studyforge.service;

import Studyforge.model.StudyMaterial;
import Studyforge.repository.StudyMaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Studyforge.model.Quiz;

import Studyforge.model.GeneratedQuiz;
import Studyforge.repository.GeneratedQuizRepository;
import Studyforge.repository.CourseRepository;
import Studyforge.model.Course;

@Service
public class QuizService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final GeneratedQuizRepository generatedQuizRepository;
    private final CourseRepository courseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizService(
            StudyMaterialRepository studyMaterialRepository,
            GeneratedQuizRepository generatedQuizRepository,
            CourseRepository courseRepository) {

        this.studyMaterialRepository = studyMaterialRepository;
        this.generatedQuizRepository = generatedQuizRepository;
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

    public Quiz generateQuiz(
            Long courseId,
            int questionCount,
            String difficulty
    ) throws Exception {

        if (questionCount < 1 || questionCount > 20) {
            throw new IllegalArgumentException(
                    "Question count must be between 1 and 20."
            );
        }

        if (!difficulty.equals("easy")
                && !difficulty.equals("medium")
                && !difficulty.equals("hard")) {

            throw new IllegalArgumentException(
                    "Difficulty must be easy, medium, or hard."
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
            throw new IllegalStateException("OPENAI_API_KEY is not configured.");
        }

        String prompt = """
        Create exactly %d multiple-choice study questions using ONLY
        the academic concepts contained in the study material below.

        Difficulty: %s

        Difficulty guidelines:
        - easy: test basic definitions and direct concepts
        - medium: test understanding and application of concepts
        - hard: require deeper reasoning, comparison, or application,
          while still using ONLY information supported by the study material

        Ignore:
        - student scores and grades
        - dates and times
        - URLs
        - page numbers
        - administrative information
        - grading totals

        Focus on concepts the student should understand and study.

        Return ONLY valid JSON using exactly this structure:

        {
          "questions": [
            {
              "question": "Question text",
              "options": [
                "Option A",
                "Option B",
                "Option C",
                "Option D"
              ],
              "correctAnswer": 0,
              "explanation": "Short explanation"
            }
          ]
        }

        Requirements:
        - Include exactly %d questions.
        - Each question must have exactly 4 options.
        - correctAnswer must be 0, 1, 2, or 3.
        - correctAnswer is the zero-based index of the correct option.
        - Do not use Markdown.
        - Do not include anything before or after the JSON.
        - Do not introduce facts unsupported by the study material.

        STUDY MATERIAL:
        %s
        """.formatted(
                questionCount,
                difficulty,
                questionCount,
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

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                    "OpenAI request failed: " + response.body()
            );
        }

        JsonNode root = objectMapper.readTree(response.body());

        for (JsonNode outputItem : root.path("output")) {

            if ("message".equals(outputItem.path("type").asText())) {

                for (JsonNode contentItem : outputItem.path("content")) {

                    if ("output_text".equals(contentItem.path("type").asText())) {

                        String jsonText =
                                contentItem.path("text").asText();

                        Quiz quiz = objectMapper.readValue(
                                jsonText,
                                Quiz.class
                        );

                        Course course = courseRepository
                                .findById(courseId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Course not found."
                                        )
                                );

                        GeneratedQuiz generatedQuiz =
                                new GeneratedQuiz(
                                        "Generated Quiz",
                                        jsonText,
                                        course
                                );

                        generatedQuizRepository.save(
                                generatedQuiz
                        );

                        return quiz;
                    }
                }
            }
        }

        throw new RuntimeException(
                "OpenAI response did not contain quiz text."
        );
    }

    public List<GeneratedQuiz> getSavedQuizzes(Long courseId) {
        return generatedQuizRepository.findByCourseId(courseId);
    }

    public GeneratedQuiz getSavedQuiz(
            Long courseId,
            Long quizId) {

        GeneratedQuiz quiz = generatedQuizRepository
                .findById(quizId)
                .orElse(null);

        if (quiz == null) {
            return null;
        }

        // Make sure this quiz actually belongs to this course
        if (!quiz.getCourse().getId().equals(courseId)) {
            return null;
        }

        return quiz;
    }

    public boolean deleteSavedQuiz(
            Long courseId,
            Long quizId) {

        GeneratedQuiz quiz = getSavedQuiz(
                courseId,
                quizId
        );

        if (quiz == null) {
            return false;
        }

        generatedQuizRepository.delete(quiz);

        return true;
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
