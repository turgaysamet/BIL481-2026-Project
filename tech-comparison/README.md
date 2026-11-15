# Technology comparisons (PA-2)

Code snippets used in Section 4 ("Design Decisions") of the Design Document.
The same feature is written with the chosen technology and with an alternative, so the
differences can be compared in practice.

| Comparison | Feature implemented in both | Chosen | Alternative |
| --- | --- | --- | --- |
| Backend framework | Join an event with capacity check (FR-11, UC-4) | `backend/spring-boot` | `backend/express` |
| Database | Schema of profiles + query of match candidates (FR-06, UC-3) | `database/postgresql` | `database/mongodb` |
| Frontend framework | Match suggestion card (FR-06, FR-08, UC-3) | `frontend/react` | `frontend/vue` |

The production code of the chosen technologies is in `backend/` and `frontend/`.
