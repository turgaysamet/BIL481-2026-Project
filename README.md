# BIL481 — Fall 2026 Term Project

Replace the example group name and member details below with your group's information.

[Project Assignment 1 description](f26_assignment_1.pdf)

## Group Name

Sangre y Oro

## Group Members


| Full Name | GitHub Username | Email | School Number |
| --- | --- | --- | --- |
| Turgay Samet Şentürk | @turgaysamet | turgaysametsenturk@etu.edu.tr | 221104087 |
| Alejandro Blas Piris | @Blas-24 | apiris@etu.edu.tr | 261118081 |
| Carlos Molina Perez | @Camope345 | cperez@etu.edu.tr | 261118083 |
| Alberto Alvarez Polo | @903155 | apolo@etu.edu.tr | 261118082 |


## Project: CampusBridge — Erasmus & Local Student Matching Platform

| Assignment | Documents |
| --- | --- |
| PA-0 | [Concept Notes](docs/Concept_Notes.pdf) |
| PA-1 | [Project Definition](docs/PA-1/Project_Definition_Document.md) · [Requirements](docs/PA-1/Requirements_Document.md) · [Project Plan](docs/PA-1/Project_Plan_Document.md) |
| PA-2 | [Design Document](docs/PA-2/Design_Document.md) · [QA Plan](docs/PA-2/QA_Plan.md) |

### Repository structure

```text
backend/          Spring Boot REST API (Java 21, Maven)
frontend/         React + TypeScript single-page app (Vite)
tech-comparison/  code snippets for the technology comparisons of the Design Document
docs/             project documents (PDF + Markdown) and screenshots
.github/          CI workflow, bug report and pull request templates
```

### How to run

Backend (needs Java 21 and a PostgreSQL database, or set `DB_URL`, `DB_USER`, `DB_PASSWORD`):

```bash
cd backend
mvn spring-boot:run      # API on http://localhost:8080
mvn test                 # unit tests + JaCoCo report in target/site/jacoco
```

Frontend (needs Node.js 20+):

```bash
cd frontend
npm install
npm run dev              # http://localhost:5173 (calls to /api go to the backend)
npm test                 # Vitest unit tests
```
