package Studyforge.service;

import Studyforge.model.Course;
import Studyforge.model.GeneratedQuiz;
import Studyforge.model.GeneratedQuizResponse;
import Studyforge.model.Quiz;
import Studyforge.model.StudyMaterial;
import Studyforge.repository.CourseRepository;
import Studyforge.repository.GeneratedQuizRepository;
import Studyforge.repository.StudyMaterialRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class QuizService {

    private final StudyMaterialRepository
            studyMaterialRepository;

    private final GeneratedQuizRepository
            generatedQuizRepository;

    private final CourseRepository
            courseRepository;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public QuizService(
            StudyMaterialRepository studyMaterialRepository,
            GeneratedQuizRepository generatedQuizRepository,
            CourseRepository courseRepository
    ) {
        this.studyMaterialRepository =
                studyMaterialRepository;

        this.generatedQuizRepository =
                generatedQuizRepository;

        this.courseRepository =
                courseRepository;
    }

    /* =========================
       COURSE CONTENT
    ========================= */

    public String getCourseContent(
            Long courseId
    ) {

        List<StudyMaterial> materials =
                studyMaterialRepository
                        .findByCourseId(
                                courseId
                        );

        StringBuilder content =
                new StringBuilder();

        for (
                StudyMaterial material
                : materials
        ) {

            if (
                    material
                            .getExtractedText()
                            != null
            ) {

                content.append(
                        material
                                .getExtractedText()
                );

                content.append(
                        "\n\n"
                );
            }
        }

        return content.toString();
    }

    /* =========================
       GENERATE QUIZ
    ========================= */

    public Quiz generateQuiz(
            Long courseId,
            int questionCount,
            String difficulty
    ) throws Exception {

        if (
                questionCount < 1 ||
                        questionCount > 20
        ) {
            throw new IllegalArgumentException(
                    "Question count must be between 1 and 20."
            );
        }

        if (
                !difficulty.equals("easy")
                        &&
                        !difficulty.equals("medium")
                        &&
                        !difficulty.equals("hard")
        ) {

            throw new IllegalArgumentException(
                    "Difficulty must be easy, medium, or hard."
            );
        }

        String courseContent =
                getCourseContent(
                        courseId
                );

        if (
                courseContent.isBlank()
        ) {
            throw new IllegalStateException(
                    "No study material found for this course."
            );
        }

        String apiKey =
                System.getenv(
                        "OPENAI_API_KEY"
                );

        if (
                apiKey == null ||
                        apiKey.isBlank()
        ) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not configured."
            );
        }

        /*
         * Each generated question now
         * contains a topic.
         *
         * The topic will later be stored
         * whenever the student gets the
         * question wrong.
         */
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

        For every question, identify the specific academic topic
        being tested.

        Return ONLY valid JSON using exactly this structure:

        {
          "questions": [
            {
              "topic": "Specific study topic",
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
        - Every question must include a topic.
        - topic must be a short, specific academic concept
          supported by the study material.
        - Use consistent topic names when multiple questions
          test the same concept.
        - Do not use vague topics such as "General",
          "Other", or "Course Material".
        - correctAnswer must be 0, 1, 2, or 3.
        - correctAnswer is the zero-based index
          of the correct option.
        - Do not use Markdown.
        - Do not include anything before or after the JSON.
        - Do not introduce facts unsupported by
          the study material.

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
            """.formatted(
                toJsonString(prompt)
        );

        HttpRequest request =
                HttpRequest
                        .newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.openai.com/v1/responses"
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer " + apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest
                                        .BodyPublishers
                                        .ofString(
                                                requestBody
                                        )
                        )
                        .build();

        HttpClient client =
                HttpClient
                        .newHttpClient();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse
                                .BodyHandlers
                                .ofString()
                );

        if (
                response.statusCode() < 200
                        ||
                        response.statusCode() >= 300
        ) {

            throw new RuntimeException(
                    "OpenAI request failed: "
                            + response.body()
            );
        }

        JsonNode root =
                objectMapper.readTree(
                        response.body()
                );

        for (
                JsonNode outputItem
                : root.path("output")
        ) {

            if (
                    "message".equals(
                            outputItem
                                    .path("type")
                                    .asText()
                    )
            ) {

                for (
                        JsonNode contentItem
                        : outputItem.path(
                        "content"
                )
                ) {

                    if (
                            "output_text".equals(
                                    contentItem
                                            .path("type")
                                            .asText()
                            )
                    ) {

                        String jsonText =
                                contentItem
                                        .path("text")
                                        .asText();

                        Quiz quiz =
                                objectMapper
                                        .readValue(
                                                jsonText,
                                                Quiz.class
                                        );

                        Course course =
                                courseRepository
                                        .findById(
                                                courseId
                                        )
                                        .orElseThrow(
                                                () ->
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

                        generatedQuizRepository
                                .save(
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

    /* =========================
       SAVED QUIZZES
    ========================= */

    public List<GeneratedQuiz>
    getSavedQuizzes(
            Long courseId
    ) {

        return generatedQuizRepository
                .findByCourseId(
                        courseId
                );
    }

    public GeneratedQuiz getSavedQuiz(
            Long courseId,
            Long quizId
    ) {

        GeneratedQuiz quiz =
                generatedQuizRepository
                        .findById(
                                quizId
                        )
                        .orElse(null);

        if (quiz == null) {
            return null;
        }

        /*
         * Make sure the quiz actually
         * belongs to this course.
         */
        if (
                !quiz
                        .getCourse()
                        .getId()
                        .equals(
                                courseId
                        )
        ) {
            return null;
        }

        return quiz;
    }

    public boolean deleteSavedQuiz(
            Long courseId,
            Long quizId
    ) {

        GeneratedQuiz quiz =
                getSavedQuiz(
                        courseId,
                        quizId
                );

        if (quiz == null) {
            return false;
        }

        generatedQuizRepository
                .delete(
                        quiz
                );

        return true;
    }

    /* =========================
       JSON HELPER
    ========================= */

    private String toJsonString(
            String text
    ) {

        return "\""
                + text
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\t",
                        "\\t"
                )
                + "\"";
    }
    public GeneratedQuizResponse generateWeakTopicQuiz(
            Long courseId,
            int questionCount,
            String difficulty,
            List<String> weakTopics
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

        if (weakTopics == null || weakTopics.isEmpty()) {
            throw new IllegalArgumentException(
                    "No weak topics were provided."
            );
        }

        String courseContent =
                getCourseContent(courseId);

        if (courseContent.isBlank()) {
            throw new IllegalStateException(
                    "No study material found for this course."
            );
        }

        String apiKey =
                System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not configured."
            );
        }

        String topicsText =
                String.join(", ", weakTopics);

        String prompt = """
        Create exactly %d multiple-choice study questions
        using ONLY the academic concepts contained in the
        study material below.

        This is a targeted practice quiz.

        Focus specifically on these topics:

        %s

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

        Return ONLY valid JSON using exactly this structure:

        {
          "questions": [
            {
              "topic": "Specific study topic",
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
        - Every question must include a topic.
        - The topic should be one of the weak topics listed above,
          or a closely matching concept from the study material.
        - Prioritize the listed weak topics.
        - correctAnswer must be 0, 1, 2, or 3.
        - correctAnswer is the zero-based index of the correct option.
        - Do not use Markdown.
        - Do not include anything before or after the JSON.
        - Do not introduce facts unsupported by the study material.

        STUDY MATERIAL:
        %s
        """.formatted(
                questionCount,
                topicsText,
                difficulty,
                questionCount,
                courseContent
        );

        String requestBody = """
        {
          "model": "gpt-5.6-luna",
          "input": %s
        }
        """.formatted(
                toJsonString(prompt)
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        "https://api.openai.com/v1/responses"
                                )
                        )
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

        if (
                response.statusCode() < 200 ||
                        response.statusCode() >= 300
        ) {
            throw new RuntimeException(
                    "OpenAI request failed: "
                            + response.body()
            );
        }

        JsonNode root =
                objectMapper.readTree(
                        response.body()
                );

        for (
                JsonNode outputItem :
                root.path("output")
        ) {

            if (
                    "message".equals(
                            outputItem
                                    .path("type")
                                    .asText()
                    )
            ) {

                for (
                        JsonNode contentItem :
                        outputItem.path("content")
                ) {

                    if (
                            "output_text".equals(
                                    contentItem
                                            .path("type")
                                            .asText()
                            )
                    ) {

                        String jsonText =
                                contentItem
                                        .path("text")
                                        .asText();

                        Quiz quiz =
                                objectMapper.readValue(
                                        jsonText,
                                        Quiz.class
                                );

                        Course course =
                                courseRepository
                                        .findById(courseId)
                                        .orElseThrow(
                                                () ->
                                                        new IllegalArgumentException(
                                                                "Course not found."
                                                        )
                                        );

                        GeneratedQuiz generatedQuiz =
                                new GeneratedQuiz(
                                        "Weak Topics Practice",
                                        jsonText,
                                        course
                                );

                        GeneratedQuiz savedQuiz =
                                generatedQuizRepository.save(
                                        generatedQuiz
                                );

                        return new GeneratedQuizResponse(
                                savedQuiz.getId(),
                                quiz
                        );
                    }
                }
            }
        }

        throw new RuntimeException(
                "OpenAI response did not contain quiz text."
        );
    }
}
