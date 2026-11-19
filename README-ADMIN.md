
# Staywise Admin Dashboard

## Admin features
- HTTP Basic authentication on all `/api/admin/**` endpoints.
- Dashboard counts for hotels, bookings, distinct guest emails, and non-cancelled booking value.
- Hotel create, edit, and delete (deletion is blocked if booking history exists).
- Booking list and status changes (cancel/reinstate).
- Admin UI at `/admin`.

## Required Render configuration
Before deploying the updated backend, open the `staywise-api` service in Render → Environment and set:
- `ADMIN_USERNAME`: choose an administrator username (default in Blueprint: `admin`).
- `ADMIN_PASSWORD`: set a unique, high-entropy password of at least 16 characters.

The backend intentionally refuses to start if `ADMIN_PASSWORD` is missing or shorter than 16 characters. Do not commit credentials to GitHub or paste them into chat. Use Render's environment-variable secret field.

The Blueprint declares `ADMIN_PASSWORD` with `sync: false`; set it manually in the existing backend service. If Render asks to add it during Blueprint sync, supply it there. Do not rotate the PostgreSQL password as part of this admin feature.

## Deploy
1. Commit and push all updated files to the existing `main` branch.
2. In Render, set `ADMIN_USERNAME` and `ADMIN_PASSWORD` on `staywise-api` before the new backend deployment starts.
3. Sync the Render Blueprint and deploy the `staywise-api` service.
4. Deploy `staywise-web` after the backend is Live.
5. Visit `https://YOUR-FRONTEND.onrender.com/admin`.
6. Sign in using the admin credentials configured in Render.

The frontend uses the existing `VITE_API_URL` build variable. The Render Blueprint rewrites `/admin` to the SPA entry point.

## API
- `GET /api/admin/dashboard`
- `GET /api/admin/hotels`
- `POST /api/admin/hotels`
- `PUT /api/admin/hotels/{id}`
- `DELETE /api/admin/hotels/{id}`
- `GET /api/admin/bookings`
- `PATCH /api/admin/bookings/{id}/status`

All admin routes require HTTP Basic authentication over HTTPS. Public customer endpoints remain unchanged.

## Security notes
- Use a unique password (16+ characters); do not reuse your Render or GitHub password.
- Admin credentials are held only in in-memory page state and are cleared when signing out or refreshing.
- Adminer is a separate database tool; restrict or remove public access to it.
- This is a starter admin console. Before production use with real customer data, add stronger identity management (for example, OAuth/MFA), audit logging, rate limiting, and formal security testing.
