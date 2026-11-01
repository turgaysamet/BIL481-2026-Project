# Project Definition Document

**Project:** CampusBridge — Erasmus & Local Student Matching Platform\
**Course:** BIL 481 Software Engineering — Fall 2026, TOBB ETÜ\
**Assignment:** Project Assignment 1 (PA-1)\
**Group:** Sangre y Oro\
**Version / Date:** 1.0 — 01-11-2026

**Contributors to this document:**

| Full Name | GitHub Username | School Number |
| --- | --- | --- |
| Turgay Samet Şentürk | @turgaysamet | 221104087 |
| Alejandro Blas Piris | @Blas-24 | 261118081 |
| Carlos Molina Perez | @Camope345 | 261118083 |
| Alberto Alvarez Polo | @903155 | 261118082 |

<div class="page-break"></div>

## Table of Contents

1. [Project Name](#1-project-name)
2. [Project Summary](#2-project-summary)
3. [Objectives](#3-objectives)
4. [Scope](#4-scope)
5. [Target Audience](#5-target-audience)
6. [Key Features](#6-key-features)
7. [Deliverables](#7-deliverables)
8. [Budget and Resources](#8-budget-and-resources)
9. [Risks and Mitigation Strategies](#9-risks-and-mitigation-strategies)
10. [Project Success Criteria](#10-project-success-criteria)
11. [Document-Specific Task Matrix](#11-document-specific-task-matrix)

<div class="page-break"></div>

## 1. Project Name

**CampusBridge — Erasmus & Local Student Matching Platform**

The name reflects the main purpose of the system: building a *bridge* between international (Erasmus/exchange) students and local students on the same campus. The project continues the idea described in our Concept Notes (Assignment-0), which are stored in the `docs` folder of this repository.

### 1.1 Relation to the Concept Notes (Assignment-0)

This document keeps the problem, users, objectives and core requirements of the Concept Notes and makes them more detailed. The Concept Notes themselves are not modified. The following points were refined after analysing them:

| Concept Notes (Assignment-0) | Project Definition (PA-1) | Reason |
| --- | --- | --- |
| Project called "Erasmus & Local Student Matching Platform" | Name "CampusBridge" added; the original title is kept as subtitle | The assignment asks for a meaningful project name |
| Authentication through eduroam plus invitation codes | Institutional e-mail verification (`@etu.edu.tr`) plus invitation codes; eduroam/eduGAIN federated login moved to *Out of Scope* | eduroam authenticates network access, and integrating with the university identity federation requires agreements with the IT department that are not possible within the term; the design allows adding it later |
| Scope: profile creation and event management | Also includes matching, match requests, messaging, notifications, privacy tools and an admin panel | These features are needed to fulfil the objectives and core requirements already described in the Concept Notes (matching, user-controlled filters, translation, right to be forgotten) |
| Four Agile phases (Requirements & UI/UX; Database & Auth; Matching & Translation; Testing & Deployment) | Seven phases in the Project Plan: P1–P2 cover the first phase, P3 the second, P4 the third, P5–P6 the fourth, and P7 adds the final documentation | More detailed planning and effort estimation required by PA-1 |
| Success criteria: 50 active users, 80% successful match rate | Kept, and extended with feature completeness, usability, performance and schedule criteria | The assignment asks for measurable success criteria |

## 2. Project Summary

International students who come to a university through Erasmus or other exchange programmes often stay inside closed groups of other foreigners. Language and cultural barriers, together with the lack of a university tool designed for this purpose, make it hard for them to meet local students. As a result, both groups lose a real opportunity for cultural exchange, language practice and friendship.

CampusBridge is a cloud-based web application that matches Erasmus and local students according to their **shared interests, skills and language goals** instead of their nationality. Users create a profile, receive match suggestions, apply their own filters (for example, same-gender matching or a specific language exchange), and join or organise real-life activities such as sports, language tandems or cultural events. The platform supports Turkish, English and Spanish, translates user-generated content automatically, and is designed following Privacy-by-Design principles to comply with both GDPR (EU) and KVKK (Türkiye).

## 3. Objectives

By the end of the project we aim to deliver a working pilot version of CampusBridge that:

- **O1.** Allows only verified members of the university community to register (institutional e-mail verification plus invitation codes created by verified local students).
- **O2.** Lets users build a complete profile with interests, skills, spoken languages (with level) and the languages they want to practise.
- **O3.** Generates match suggestions based on common interests/skills and complementary languages, respecting filters chosen by the user.
- **O4.** Allows users to create, search, join and leave events and activities.
- **O5.** Allows matched users to communicate inside the platform.
- **O6.** Offers a complete user interface in Turkish, English and Spanish and translates user-generated content (bios, event descriptions, messages) on demand.
- **O7.** Protects personal data: consent management, data export and full account deletion ("right to be forgotten").
- **O8.** Is tested and deployed as a pilot with real students of TOBB ETÜ before the end of the term.

## 4. Scope

### 4.1 In Scope

- Registration and login with institutional e-mail verification (`@etu.edu.tr`) and an invitation-code mechanism.
- User profiles (interests, skills, languages, short bio, profile photo).
- Interest-based matching algorithm with user-controlled filters.
- Match requests (send, accept, decline) and one-to-one text messaging between matched users.
- Event and activity management (create, browse/search, join, leave, capacity control).
- In-app notifications.
- Multi-language interface (TR / EN / ES) and on-demand translation of user-generated content through an external translation API.
- Privacy features: consent, data export, account deletion, report/block user.
- A simple administrator panel for moderation (reports, suspensions, interest catalogue).
- Responsive web interface usable on desktop and mobile browsers.

### 4.2 Out of Scope (NOT included)

- Native mobile applications (iOS/Android). The web app is responsive but no app store version will be built.
- Real integration with the eduroam / eduGAIN federated identity infrastructure. Accessing it requires agreements with the university IT department; in this project it is replaced by institutional e-mail verification, and the authentication module is designed so that a federated login (e.g. SAML/OAuth) could be added later.
- Integration with official university systems (student information system, Erasmus office databases).
- Voice/video calls, group chats and file sharing in messaging.
- Payments, ticket sales or any commercial features for events.
- Housing, course-recognition or bureaucratic support for Erasmus students.
- Machine-learning based recommendation; the matching algorithm will be rule/score based.
- Deployment to other universities (the pilot is limited to TOBB ETÜ).

## 5. Target Audience

- **Erasmus and exchange students** arriving at TOBB ETÜ who want to integrate into local student life, practise Turkish or other languages, and find people with similar hobbies.
- **Local (degree-seeking) students** of TOBB ETÜ interested in cultural exchange, practising foreign languages (e.g. English, Spanish) or finding partners for sports and hobbies.
- **Student clubs and student volunteers** (e.g. buddy programmes) who want to organise activities that mix international and local students.
- **Platform administrators/moderators** (in the pilot, members of the team) who manage reports and content.

## 6. Key Features

1. **Secure registration and login** — institutional e-mail verification and invitation codes generated by verified local students.
2. **Rich user profiles** — interests, skills, languages with CEFR level, languages to practise, bio and photo.
3. **Interest-based matching** — compatibility score from shared interests/skills and complementary languages; top suggestions shown to the user.
4. **User-controlled filters** — same-gender matching, language-exchange target, interest category, user type (Erasmus/local).
5. **Match requests and messaging** — send/accept/decline requests and chat one-to-one with matched users.
6. **Event and activity management** — create, search, join and leave events with category, date, place and capacity.
7. **Multi-language support** — TR/EN/ES interface and automatic translation of user-generated content.
8. **Privacy and safety tools** — consent management, data export, account deletion, report and block users.
9. **Notifications** — in-app alerts for new match requests, accepted matches, messages and event changes.
10. **Administration panel** — review reports, suspend accounts, manage the interest catalogue.

## 7. Deliverables

| # | Deliverable | Description | Planned date |
| --- | --- | --- | --- |
| D0 | Concept Notes (PA-0) | Initial concept of the project | 11-10-2026 (delivered) |
| D1 | Project Definition, Requirements and Project Plan documents (PA-1) | This document and its two companion documents, in Markdown and PDF | 01-11-2026 |
| D2 | Design documentation (PA-2) | System architecture, UML diagrams, database schema, UI wireframes | 22-11-2026 (planned) |
| D3 | Implementation / test documentation (PA-3) | Test plan, test results and progress report | 27-12-2026 (planned) |
| D4 | Source code | Complete source code in the GitHub repository with clear commit history | Continuous, final on 10-01-2027 |
| D5 | Deployed pilot | Running web application accessible to pilot users | 10-01-2027 |
| D6 | Final project documentation and presentation | Final report, user guide, demo | 12-01-2027 |

*Dates for D2–D6 are planned estimates and will be updated when the official course dates are announced.*

## 8. Budget and Resources

### 8.1 Human Resources

Four team members, each planning around 75 person-hours of work during the term (≈ 6 hours per week), **300 person-hours in total**. Roles and the detailed distribution are in the Project Plan Document.

### 8.2 Software and Services

| Resource | Purpose | Cost |
| --- | --- | --- |
| GitHub (repository, Issues, Projects, Actions) | Version control, task tracking, CI | Free (student) |
| React + TypeScript | Frontend | Free / open source |
| Java 21 + Spring Boot | Backend REST API | Free / open source |
| PostgreSQL | Relational database | Free / open source |
| Docker | Local development and deployment | Free |
| Cloud hosting (e.g. Render / Railway free tier, or GitHub Student Developer Pack credits) | Hosting the pilot | Free tier (estimated 0 TL) |
| DeepL API Free (or Google Cloud Translation free tier) | Translation of user-generated content | Free up to the monthly character limit |
| Transactional e-mail service free tier (e.g. Brevo) | Verification e-mails | Free tier |
| Figma | UI/UX wireframes and prototypes | Free (education plan) |
| Google Drive, WhatsApp, Discord | Documentation and communication | Free |

### 8.3 Hardware

Team members' personal laptops. No special hardware is needed.

### 8.4 Learning Resources

Course lecture notes, official documentation of Spring Boot, React and PostgreSQL, GDPR and KVKK official texts and guidelines, and online tutorials.

### 8.5 Budget Considerations

The project is planned with a **budget of 0 TL** by using open-source tools and free tiers. The main cost risk is exceeding the free limits of the translation API or hosting. To control this, translations are cached, translation is done only on demand, and usage is monitored weekly. If a paid plan becomes necessary, the estimated maximum cost is below 10 USD/month for the pilot period.

## 9. Risks and Mitigation Strategies

| # | Risk | Probability | Impact | Mitigation strategy |
| --- | --- | --- | --- | --- |
| R1 | Team members have little experience with some technologies (Spring Boot, React, cloud deployment) | Medium | High | Early learning tasks in the design phase, pair programming, choose well-documented technologies |
| R2 | Schedule delays because of exams and other courses | High | Medium | Two-week sprints with buffer time, prioritise "Must" requirements (MoSCoW), weekly progress check |
| R3 | Translation API or hosting free limits are exceeded | Low | Medium | Cache translations, on-demand translation only, monitor usage, fallback to a second provider |
| R4 | Personal data leak or non-compliance with GDPR/KVKK | Low | High | Privacy-by-Design, data minimisation, HTTPS, password hashing, access control, security review before pilot |
| R5 | Not enough users for the pilot (fewer than 50) | Medium | High | Promotion through Erasmus office, student clubs and social media; invitation codes from local team member |
| R6 | Misuse of the platform (harassment, fake profiles) | Medium | High | Institutional verification, report/block function, admin moderation, same-gender filter |
| R7 | Communication problems in a multicultural team (language, time) | Medium | Medium | English as working language, weekly meetings, written decisions in GitHub Issues |
| R8 | A team member leaves the course or becomes unavailable | Low | High | Shared knowledge through code reviews and documentation, every module has a backup person |
| R9 | Scope creep (new features added during development) | Medium | Medium | Formal change management process defined in the Project Plan |

## 10. Project Success Criteria

1. **Feature completeness:** 100% of the "Must" functional requirements and at least 80% of all functional requirements in the Requirements Document are implemented and pass their acceptance tests.
2. **User adoption:** at least **50 active users** register during the pilot, with at least 30% Erasmus/exchange students and at least 30% local students.
3. **Matching efficacy:** at least **80% successful match rate**, where a successful match is an accepted match request that leads to at least one message exchange or participation in a common event.
4. **Usability:** at least **80% of pilot users** rate the application as easy to use (System Usability Scale score ≥ 70).
5. **Performance and quality:** 95% of page requests are answered in less than 2 seconds, and no critical bugs remain open at the end of the testing phase.
6. **Schedule:** all course deliverables (PA-1, PA-2, PA-3, final documentation) are delivered on time.

## 11. Document-Specific Task Matrix

| Task in the creation of this document | Responsible | Support |
| --- | --- | --- |
| Project name and summary | Turgay Samet Şentürk | Carlos Molina Perez |
| Objectives | Carlos Molina Perez | Turgay Samet Şentürk |
| Scope (in / out) | Alberto Alvarez Polo | Carlos Molina Perez |
| Target audience | Turgay Samet Şentürk | Alejandro Blas Piris |
| Key features | Alejandro Blas Piris | Alberto Alvarez Polo |
| Deliverables | Turgay Samet Şentürk | — |
| Budget and resources | Carlos Molina Perez | Alberto Alvarez Polo |
| Risks and mitigation strategies | Alberto Alvarez Polo | Turgay Samet Şentürk |
| Success criteria | Alejandro Blas Piris | Turgay Samet Şentürk |
| Review, formatting and PDF conversion | Turgay Samet Şentürk | All members |
