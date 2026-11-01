# Requirements Document

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

1. [Introduction](#1-introduction)
2. [Users and Roles](#2-users-and-roles)
3. [Functional Requirements](#3-functional-requirements)
4. [Non-Functional Requirements](#4-non-functional-requirements)
5. [Traceability: Requirements to Key Features](#5-traceability-requirements-to-key-features)
6. [Document-Specific Task Matrix](#6-document-specific-task-matrix)

<div class="page-break"></div>

## 1. Introduction

This document describes the requirements of CampusBridge, a web platform that matches Erasmus/exchange students with local students of TOBB ETÜ according to shared interests, skills and language goals. Section 3 defines **what the system must do** (functional requirements) and Section 4 defines **how well the system must do it** (non-functional requirements).

Every requirement has a unique identifier (FR-xx / NFR-xx), a priority and an acceptance criterion so that it can be verified by a test. A written specification is what allows us to decide whether the software "works": following the V-model, these acceptance criteria are the basis of the acceptance and validation tests at the end of each sprint (*validation*: did we build the right software?), while unit and integration tests check that each part is built correctly (*verification*: did we build the software right?). In Scrum, every requirement becomes a product backlog item.

Priorities follow the **MoSCoW** method:

- **Must** — required for the pilot; the project fails without it.
- **Should** — important, implemented if time allows inside the plan.
- **Could** — desirable, implemented only if there is spare time.

## 2. Users and Roles

| Role | Description |
| --- | --- |
| Guest | Visitor who is not logged in. Can only see the landing page, register and log in. |
| Local Student | Verified TOBB ETÜ student (institutional e-mail). Can generate invitation codes. |
| Erasmus Student | Exchange student verified by institutional e-mail or by a valid invitation code. |
| Administrator | Team member/moderator who manages reports, accounts and the interest catalogue. |

In the requirements below, **"user"** means any logged-in Local or Erasmus student.

## 3. Functional Requirements

### 3.1 Account and Authentication

**FR-01 — Registration with institutional e-mail** *(Must)*
A guest must be able to register with an institutional e-mail address (`@etu.edu.tr`), a password and the user type (Local / Erasmus). The system must send a verification link and the account must stay inactive until the link is opened.
*Acceptance:* registration with a non-institutional e-mail is rejected; an unverified account cannot log in; the verification link expires after 24 hours.

**FR-02 — Registration with invitation code** *(Must)*
A verified local student must be able to generate invitation codes, and an Erasmus student who does not yet have an institutional e-mail must be able to register with a valid code.
*Acceptance:* a local student can have at most 5 active codes; each code can be used once and expires after 14 days; an invalid, used or expired code is rejected with an error message.

**FR-03 — Login, logout and password reset** *(Must)*
A registered user must be able to log in with e-mail and password, log out, and reset a forgotten password via an e-mail link.
*Acceptance:* after 5 consecutive failed login attempts the account is locked for 15 minutes; the reset link expires after 1 hour.

### 3.2 Profile Management

**FR-04 — Create profile** *(Must)*
After the first login, the user must be able to create a profile with: display name, user type, home university/country, gender (optional), spoken languages with CEFR level (A1–C2), languages to practise, at least 3 interests selected from the interest catalogue, skills, a bio (max. 500 characters) and an optional profile photo (JPG/PNG, max. 2 MB).
*Acceptance:* the profile cannot be saved with fewer than 3 interests or without at least one spoken language; the user cannot receive matches until the profile is complete.

**FR-05 — View and edit profile** *(Must)*
The user must be able to view and edit their own profile at any time and view the public profile of other users (name, type, languages, interests, skills, bio, photo). E-mail and gender must never be shown to other users.
*Acceptance:* changes are visible immediately after saving; another user's profile page does not contain e-mail or gender.

### 3.3 Matching

**FR-06 — Match suggestions** *(Must)*
The system must compute a compatibility score between the user and other users based on shared interests, shared skills and complementary languages (a language one user speaks and the other wants to practise), and show the user a list of the 10 best suggestions ordered by score. Matching must not use nationality as a criterion.
*Acceptance:* for a test data set, the order of suggestions matches the documented scoring formula; users already matched, blocked or suspended do not appear.

**FR-07 — User-controlled matching filters** *(Must)*
The user must be able to apply filters to the suggestions: same gender only, language-exchange target language, interest category and user type (Erasmus / Local).
*Acceptance:* with the "same gender only" filter on, no suggestion has a different gender or no gender stated; filter settings are saved between sessions.

**FR-08 — Match requests** *(Must)*
The user must be able to send a match request to a suggested user, and the receiver must be able to accept or decline it. A user must be able to view the list of their current matches and remove a match.
*Acceptance:* a declined user cannot send a new request to the same person for 30 days; after acceptance, both users appear in each other's match list.

### 3.4 Events and Activities

**FR-09 — Create event** *(Must)*
The user must be able to create an event with title, description, category (sport, language, culture, study, other), date and time, location, maximum number of participants (2–100) and the language(s) of the event. The creator must be able to edit or cancel the event.
*Acceptance:* events with a past date or missing required fields cannot be created; when an event is cancelled, all participants receive a notification.

**FR-10 — Browse and search events** *(Must)*
The user must be able to list upcoming events and filter them by category, date range and language, and search them by keyword in the title.
*Acceptance:* past and cancelled events are not listed; every filter returns only events that satisfy it.

**FR-11 — Join and leave events** *(Must)*
The user must be able to join an event if there are free places and leave it before it starts. The event page must show the number of participants and the free places.
*Acceptance:* a user cannot join a full event; the participant counter is updated immediately after joining or leaving.

### 3.5 Communication

**FR-12 — Messaging between matched users** *(Should)*
Two matched users must be able to exchange one-to-one text messages (max. 1000 characters per message) and see the conversation history.
*Acceptance:* users who are not matched, or who have blocked each other, cannot send messages; new messages appear without reloading the page within 5 seconds.

**FR-13 — Translation of user-generated content** *(Should)*
The user must be able to translate a bio, event description or message into their selected interface language by pressing a "Translate" button.
*Acceptance:* the translated text is displayed below the original; a repeated translation of the same text is served from the cache without calling the external API again.

**FR-14 — Multi-language interface** *(Must)*
The user must be able to switch the interface language between Turkish, English and Spanish at any time; the choice must be saved in the user's settings.
*Acceptance:* after switching, all menus, buttons, labels and system messages appear in the selected language.

**FR-15 — Notifications** *(Should)*
The system must show in-app notifications for new match requests, accepted matches, new messages and changes or cancellations of joined events, with an unread counter.
*Acceptance:* each of the four events generates exactly one notification for the correct user; opening a notification marks it as read.

### 3.6 Privacy, Safety and Administration

**FR-16 — Consent and data rights** *(Must)*
During registration the user must accept the privacy notice (GDPR/KVKK). The user must be able to download all their personal data in JSON format and permanently delete their account.
*Acceptance:* registration is impossible without consent; after account deletion, the user's personal data is removed or anonymised within 30 days and the user can no longer log in.

**FR-17 — Report and block users** *(Must)*
The user must be able to block another user and report a user, event or message with a reason (harassment, spam, fake profile, other).
*Acceptance:* a blocked user can no longer see the blocker in suggestions, send requests or messages; each report is stored and visible to administrators.

**FR-18 — Administration panel** *(Should)*
An administrator must be able to view and resolve reports, suspend or reactivate user accounts, remove inappropriate events, and add/edit/remove interests in the interest catalogue.
*Acceptance:* a suspended user cannot log in; only accounts with the administrator role can access the panel.

## 4. Non-Functional Requirements

**NFR-01 — Performance** *(Must)*
95% of page loads and API requests must complete in less than **2 seconds** with up to 100 concurrent users; generating match suggestions must take less than 3 seconds.
*Verification:* load test with a tool such as JMeter or k6 on the pilot environment.

**NFR-02 — Security** *(Must)*
All traffic must use HTTPS; passwords must be stored only as salted hashes (bcrypt); every API endpoint except registration/login must require authentication; the system must be protected against the OWASP Top 10 common attacks (e.g. SQL injection, XSS, CSRF).
*Verification:* code review checklist and automated vulnerability scan (e.g. OWASP ZAP) before the pilot.

**NFR-03 — Privacy and legal compliance** *(Must)*
The system must comply with GDPR and KVKK: only the data needed for matching is collected (data minimisation), sensitive optional data (gender) is used only for the filter the user chooses, data is stored in data centres inside the EU or Türkiye, and access to personal data is restricted to the owner and administrators.
*Verification:* privacy checklist review; data export and deletion tested (FR-16).

**NFR-04 — Usability** *(Must)*
A new user must be able to complete registration and profile creation in less than **5 minutes** without help, and the application must reach a System Usability Scale score of at least 70 in the pilot.
*Verification:* usability test with at least 5 students and SUS questionnaire.

**NFR-05 — Compatibility and responsiveness** *(Must)*
The web application must work correctly on the latest two versions of Chrome, Firefox, Safari and Edge, and the layout must adapt to screen widths from 360 px (mobile) to 1920 px (desktop).
*Verification:* automated Selenium WebDriver tests run on the different browsers, plus a manual responsive design checklist.

**NFR-06 — Localisation** *(Must)*
100% of the interface texts must be available in Turkish, English and Spanish, stored in separate resource files (no hard-coded texts), and dates/times must be shown in the user's locale format.
*Verification:* automated check that every text key exists in the three language files.

**NFR-07 — Availability and reliability** *(Should)*
The system must be available at least 99% of the time during the pilot period, and the database must be backed up automatically every day.
*Verification:* uptime monitoring service and backup restore test.

**NFR-08 — Maintainability** *(Should)*
The code must follow a layered architecture (presentation, business logic, data access), every change must go through a pull request reviewed by another team member, and backend unit tests must cover at least 60% of the business-logic code.
*Verification:* GitHub pull request history and JaCoCo code coverage report generated automatically in the CI pipeline (GitHub Actions).

**NFR-09 — Scalability** *(Could)*
The architecture must allow the system to be extended to other universities without changes to the core matching logic (university as a configurable parameter).
*Verification:* design review in PA-2.

## 5. Traceability: Requirements to Key Features

| Key feature (Project Definition) | Functional requirements |
| --- | --- |
| Secure registration and login | FR-01, FR-02, FR-03 |
| Rich user profiles | FR-04, FR-05 |
| Interest-based matching | FR-06 |
| User-controlled filters | FR-07 |
| Match requests and messaging | FR-08, FR-12 |
| Event and activity management | FR-09, FR-10, FR-11 |
| Multi-language support | FR-13, FR-14 |
| Privacy and safety tools | FR-16, FR-17 |
| Notifications | FR-15 |
| Administration panel | FR-18 |

## 6. Document-Specific Task Matrix

| Task in the creation of this document | Responsible | Support |
| --- | --- | --- |
| Introduction, users and roles | Turgay Samet Şentürk | Alejandro Blas Piris |
| FR-01 – FR-03 (account and authentication) | Carlos Molina Perez | Turgay Samet Şentürk |
| FR-04 – FR-05 (profile management) | Alejandro Blas Piris | Carlos Molina Perez |
| FR-06 – FR-08 (matching) | Alberto Alvarez Polo | Alejandro Blas Piris |
| FR-09 – FR-11 (events) | Alejandro Blas Piris | Carlos Molina Perez |
| FR-12 – FR-15 (communication and languages) | Alberto Alvarez Polo | Turgay Samet Şentürk |
| FR-16 – FR-18 (privacy, safety, administration) | Turgay Samet Şentürk | Carlos Molina Perez |
| NFR-01 – NFR-09 | Carlos Molina Perez | Alberto Alvarez Polo |
| Traceability table | Alberto Alvarez Polo | — |
| Review, formatting and PDF conversion | Alberto Alvarez Polo | All members |
