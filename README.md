# StudyForge

StudyForge is a full-stack study application I am building to make it easier for students to study from their own course materials.

The idea behind the project is pretty simple: instead of going through notes and course files manually every time I want to study, I wanted one place where I could upload my materials and generate different study tools from them.

This repository contains the **Spring Boot backend** for StudyForge.

## What it does

Right now, StudyForge allows users to:

* Create and manage courses
* Upload study materials for each course
* Generate study summaries from uploaded materials
* Generate quizzes with different difficulty levels and question counts
* Answer generated quizzes and receive explanations
* Save generated quizzes
* Track quiz attempts and scores
* Generate and review flashcards

I am still actively working on the project, so more features will be added as I continue developing it.

## Tech Stack

The backend is built with:

* Java
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven
* REST APIs

The frontend is built separately using React and TypeScript.

## Project Structure

The backend handles the main application logic, database storage, course materials, generated study content, and quiz attempt history.

Some of the main parts of the backend include:

* Courses
* Study materials
* Generated summaries
* Generated quizzes
* Flashcards
* Quiz attempts

PostgreSQL is used to store the application data.

## Running the Backend

Make sure PostgreSQL is running and that the StudyForge database has been created.

Then run:

```bash
./mvnw spring-boot:run
```

The backend runs locally on:

```text
http://localhost:8080
```

## Frontend

The frontend for StudyForge is in a separate repository:

https://github.com/bmkekeh/StudyForge-Frontend

It is built with React and TypeScript and communicates with this backend through REST API endpoints.

## Current Status

StudyForge is still a work in progress.

The main study workflow is currently working:

```text
Create Course
     ↓
Upload Materials
     ↓
Generate Study Content
     ↓
Practice with Quizzes / Flashcards
     ↓
Track Quiz Performance
```

I plan to keep improving the UI, study features, and overall user experience as I continue working on the project.
