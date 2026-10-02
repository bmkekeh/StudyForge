# StudyForge

StudyForge is a full-stack study application that helps students turn their own course materials into personalized study resources.

I built StudyForge around a simple idea: instead of keeping course files, summaries, quizzes, flashcards, and study progress in separate places, students should be able to manage them through one course-based workspace.

This repository contains the **Spring Boot backend** for StudyForge.

## Features

StudyForge currently supports:

* Creating and managing courses
* Uploading study materials for individual courses
* Generating study summaries from uploaded materials
* Generating flashcards from course content
* Generating quizzes with configurable difficulty and question count
* Saving generated quizzes
* Recording completed quiz attempts and scores
* Recording incorrectly answered questions
* Associating quiz mistakes with their topics
* Identifying weak topics based on previous mistakes
* Generating targeted practice quizzes from weak topics
* Tracking quiz performance over time

## Adaptive Practice

StudyForge uses previous quiz performance to help determine what a student should practice next.

When a quiz is completed, the backend records the student's incorrect answers and the topic associated with each mistake.

These mistakes are aggregated to identify topics the student has struggled with.

StudyForge can then generate a new quiz specifically targeting those weak topics.

```text
Complete Quiz
     ↓
Record Incorrect Answers
     ↓
Group Mistakes by Topic
     ↓
Identify Weak Topics
     ↓
Generate Targeted Practice Quiz
     ↓
Complete Practice Quiz
     ↓
Continue Tracking Progress
```

This creates a feedback loop where quiz history influences future practice material.

## Tech Stack

### Backend

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* REST APIs

### Frontend

The frontend is maintained in a separate repository and uses:

* React
* TypeScript
* Vite

## Architecture

The backend follows a layered structure:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

### Controllers

Expose REST API endpoints and handle incoming requests.

### Services

Contain the main application logic, including study-content generation, quiz processing, progress tracking, and weak-topic analysis.

### Repositories

Use Spring Data JPA to communicate with PostgreSQL.

### Models

Represent application data such as courses, study materials, generated quizzes, flashcards, quiz attempts, mistakes, and weak topics.

## Main Domain Areas

The backend currently manages:

```text
Course
├── Study Materials
├── Generated Summaries
├── Generated Flashcard Sets
├── Generated Quizzes
│   └── Quiz Attempts
│       └── Quiz Mistakes
└── Progress / Weak Topics
```

## Quiz Progress Tracking

Each completed quiz attempt stores information such as:

* Score
* Total number of questions
* Completion time
* Course
* Generated quiz
* Incorrectly answered questions

Mistakes also retain their associated topic. This allows StudyForge to aggregate mistakes and determine which topics appear most frequently.

The progress system can provide:

* Number of completed quizzes
* Average score
* Best score
* Latest score
* Quiz attempt history
* Weak topics

## Targeted Weak-Topic Quizzes

The backend supports generating quizzes from a list of weak topics.

For example:

```http
POST /courses/{courseId}/quiz/weak-topics
```

The generated quiz is saved like a normal quiz, receives its own database ID, and can later be associated with a quiz attempt.

This means targeted practice still contributes to the student's overall progress history.

## Running the Backend

### Requirements

Make sure you have:

* Java 21
* PostgreSQL
* Maven, or use the included Maven wrapper

### Database

Create a PostgreSQL database named:

```text
studyforge
```

Update `src/main/resources/application.properties` if your PostgreSQL username, database name, or connection settings are different.

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/studyforge
spring.datasource.username=YOUR_POSTGRES_USERNAME
```

Do not commit database passwords, API keys, or other secrets to the repository.

### Start the Backend

Run:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## Testing

Run the backend tests with:

```bash
./mvnw clean test
```

## Frontend

The React + TypeScript frontend is available here:

https://github.com/bmkekeh/StudyForge-Frontend

The frontend communicates with this backend through REST API endpoints.

## Project Status

The core StudyForge workflow is working:

```text
Create Course
     ↓
Upload Materials
     ↓
Generate Study Resources
     ↓
Study with Summaries / Flashcards / Quizzes
     ↓
Complete Quizzes
     ↓
Track Performance
     ↓
Identify Weak Topics
     ↓
Practice Weak Topics
```

The project is still being improved, but the main study workflow and progress-tracking system are functional.
