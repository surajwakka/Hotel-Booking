# Staywise — Hotel Booking Application

A full-stack hotel discovery and reservation demo built with React + Vite, Java 17 / Spring Boot, Spring Data JPA and PostgreSQL. Includes an H2 local-development fallback.

## Features
- Search and filter seeded hotels by destination
- Hotel details, date and guest selection, booking price calculation
- Create reservations and retrieve bookings by guest email
- Cancel reservations
- Responsive desktop/mobile interface
- Render Blueprint for public hosting (API + static frontend + PostgreSQL)

## Run locally

### Backend
```bash
cd backend
mvn spring-boot:run
```
The API starts at `http://localhost:8080`; H2 is used by default.

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Open the Vite URL (normally `http://localhost:5173`). Set `VITE_API_URL` if the API is not at `http://localhost:8080/api`.

## Deploy publicly on Render
1. Push this folder to a GitHub repository.
2. Sign in to Render and choose **New → Blueprint**.
3. Connect the repository and approve the `render.yaml` blueprint. It creates the Spring Boot API, static React site and PostgreSQL database.
4. Wait for both services to finish deploying. Open the `staywise-web` service URL.

The blueprint wires the frontend API URL and database connection settings. Free-tier services may sleep when idle; the first request after inactivity can take a little longer. Confirm the current plan/pricing in Render before provisioning.

## API
- `GET /api/hotels?city=Goa`
- `GET /api/hotels/{id}`
- `POST /api/bookings`
- `GET /api/bookings?email=guest@example.com`
- `DELETE /api/bookings/{id}?email=guest@example.com`

## Demo limitations
This is a booking-flow demo, not a production OTA. It does not process payments, implement user accounts, or provide an admin dashboard. For production, add authentication/authorization, transactional inventory locking, date-overlap availability checks, payment integration, rate limiting, secrets management and stronger validation. The current booking inventory count is intentionally simple.
