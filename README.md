# Smart Campus Booking System

Roomio is a full-stack campus resource booking system. Students can browse and reserve rooms, labs, and equipment with backend availability checks. Administrators can manage resources, update booking outcomes, and monitor usage analytics.

## Stack

- Frontend: Vue 3, Vite, Pinia, Vue Router, Tailwind CSS, Axios, Chart.js, Vitest
- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, JWT, Maven
- Database: MySQL for runtime, H2 for backend tests

## Project Structure

```text
backend/      Spring Boot REST API, JPA entities, services, security, tests
frontend/     Vue 3 application using the backend API
postman/      API example collection
SYSTEM_DESIGN.md
```

## Demo Credentials

```text
Student: student@campus.test / password
Admin:   admin@campus.test / password
```

Seed data is inserted automatically on first backend startup when the database is empty.

## Backend Setup

Create a MySQL database or let the JDBC URL create it:

```sql
CREATE DATABASE roomio;
```

Run the backend:

```bash
cd backend
mvn spring-boot:run
```

Default backend URL:

```text
http://127.0.0.1:8080/api
```

Backend environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/roomio?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=change-this-to-a-long-random-secret
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://127.0.0.1:5173,http://localhost:5173
SEED_ENABLED=true
JPA_DDL_AUTO=update
```

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Optional `.env`:

```text
VITE_API_BASE_URL=http://127.0.0.1:8080/api
```

## API Summary

Auth:

- `POST /api/auth/register`
- `POST /api/auth/login`

Resources:

- `GET /api/resources`
- `GET /api/resources/{id}`
- `POST /api/resources` admin
- `PUT /api/resources/{id}` admin
- `DELETE /api/resources/{id}` admin, deactivates the resource

Bookings:

- `POST /api/bookings` student
- `GET /api/bookings/my`
- `GET /api/bookings/all` admin
- `PUT /api/bookings/{id}`
- `PATCH /api/bookings/{id}/cancel`
- `PATCH /api/bookings/{id}/status` admin

Availability and analytics:

- `GET /api/availability`
- `GET /api/availability/suggestions`
- `GET /api/analytics/summary` admin
- `GET /api/analytics/resource-usage` admin
- `GET /api/analytics/status-distribution` admin

Import `postman/roomio-api-examples.json` for example requests.

## Booking Rules

The backend `BookingService` is the source of truth:

- Reject past bookings.
- Require bookings from 30 minutes to 2 hours.
- Reject inactive resources.
- Reject pax above resource capacity.
- Block only overlapping `CONFIRMED` bookings.
- Block same-resource conflicts.
- Block same-user conflicts across resources.
- Allow cancelled slots to be reused.
- Use `newStart < existingEnd && newEnd > existingStart`.

## Tests And Build

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm run lint
npm run test
npm run build
```

## Deployment Notes

- Keep `JWT_SECRET` outside source control.
- Use a managed MySQL database in production.
- Set `CORS_ALLOWED_ORIGINS` to the deployed frontend origin.
- Build the frontend with `VITE_API_BASE_URL` pointing at the deployed backend.
- Use `JPA_DDL_AUTO=validate` or migrations for production database management.
