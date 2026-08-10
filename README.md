# Lead List

[![Backend Build](https://github.com/bjjon/LeadList/actions/workflows/ci.yml/badge.svg)](https://github.com/bjjon/LeadList/actions/workflows/ci.yml)
[![Backend SonarQube Analysis](https://github.com/bjjon/LeadList/actions/workflows/backend.yml/badge.svg)](https://github.com/bjjon/LeadList/actions/workflows/backend.yml)
[![Frontend SonarQube Analysis](https://github.com/bjjon/LeadList/actions/workflows/frontend.yml/badge.svg)](https://github.com/bjjon/LeadList/actions/workflows/frontend.yml)
[![Deploy to VPS](https://github.com/bjjon/LeadList/actions/workflows/deploy.yml/badge.svg)](https://github.com/bjjon/LeadList/actions/workflows/deploy.yml)

[![Backend Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=bjjon_LeadList_backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bjjon_LeadList_backend)
[![Frontend Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=bjjon_LeadList_frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bjjon_LeadList_frontend)

## Description

LeadList is a collaborative lead management application. Employees jointly manage a shared list of leads: every lead can be assigned to individual team members, contact attempts are logged as call logs, and each lead is tagged with a defined status. Changes made by one team member (status updates, assignments) are pushed to everyone else in real time over WebSockets. A built-in team chat lets everyone communicate directly in the app.

## Features

- **Authentication** — JWT-based login and stateless session handling.
- **Shared lead list** — leads are visible to the whole team and can be assigned to individual users.
- **Call logging** — every contact attempt with a lead is recorded and tied to a status.
- **Status tracking** — leads move through a defined set of statuses (e.g. new, contacted, converted).
- **CSV import** — bulk-import leads from a CSV file.
- **Search & filtering** — search leads and filter by status or assigned user.
- **Team chat** — a shared, persisted chat where every team member's messages are labeled with their name and online status, updated live for all connected clients.
- **Live updates** — lead changes, chat messages, and team members' online presence are broadcast in real time via WebSocket (STOMP over SockJS), so the app stays in sync across all connected clients without a page reload.

## Tech Stack

### Backend
- Java 25, Spring Boot 4.1 (Web MVC, WebSocket, Data JPA, Validation, Security)
- PostgreSQL, schema managed with Flyway migrations
- JWT authentication (jjwt)
- JUnit 5 + Testcontainers (PostgreSQL) for integration tests, JaCoCo for coverage

### Frontend
- React 19 + TypeScript, built with Vite
- Zustand for state management
- react-hook-form + zod for form handling and validation
- Axios for REST calls, @stomp/stompjs + sockjs-client for the WebSocket connection

## Getting Started

### Prerequisites
- Docker and Docker Compose — always required (runs Postgres; optionally the full stack)
- Additionally, to run backend/frontend individually instead of via Docker: Java 25 + Maven and Node.js 22 + npm

### Run the full stack with Docker Compose
```bash
cp .env.example .env   # fill in DB_USER, DB_PASSWORD, DB_NAME, JWT_SECRET, JWT_EXPIRATION
docker compose up -d --build
```
The app is then reachable via the nginx container on port 80.

### Run backend and frontend individually (development)
Start Postgres via Docker first (`docker compose up -d postgres`, published on `localhost:5432` via
`docker-compose.override.yml`), then run the backend against it.

Backend (reads `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`
from the environment — e.g. via an IDE run configuration that loads `.env`, or by exporting them in your
shell). Unlike Docker Compose, where `DB_HOST`/`DB_PORT` are hardcoded to `postgres`/`5432` in
`docker-compose.yaml`, a local/IDE run needs them set explicitly — use `DB_HOST=localhost` and
`DB_PORT=5432` (see `.env.example`):
```bash
cd backend
./mvnw spring-boot:run
```

Frontend (Vite dev server, proxies `/api` and `/ws` to `localhost:8080`):
```bash
cd frontend
npm install
npm run dev
```

### Database migrations

Schema changes are managed with Flyway (`backend/src/main/resources/db/migration`). Flyway validates
the checksum of every already-applied migration on startup — **never edit a migration file that has
already been merged/applied**, even for a tiny fix, since that breaks validation for anyone who already
ran it and forces a full DB drop/recreate to get past it. Always add a new, additive migration
(`V{next}__description.sql`) instead.