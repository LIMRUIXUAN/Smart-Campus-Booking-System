# Smart Campus Booking System

Vue 3 frontend for a campus resource booking platform. The app helps students browse and book study rooms, labs, and equipment while giving administrators resource management, booking status controls, and usage analytics.

## Stack

- Vue 3 + Vite
- Tailwind CSS
- Vue Router
- Pinia
- Axios mock API layer
- Chart.js + vue-chartjs
- Lucide Vue icons
- Vitest

## Setup

```bash
cd frontend
npm install
npm run dev
```

Production build:

```bash
npm run build
```

Unit tests:

```bash
npm run test
```

## Demo Accounts

Student:

```text
student@campus.test / password
```

Admin:

```text
admin@campus.test / password
```

## Main Pages

- Landing page: project overview and call-to-action.
- Login and register: demo role routing.
- Student dashboard: resource filters, resource cards, quick stats, and upcoming bookings.
- Resource details: booking form, availability check, conflict warning, and smart suggestions.
- My bookings: student booking history with cancel/re-book actions.
- Admin dashboard: analytics cards, resource usage chart, status distribution chart, and recent bookings.
- Admin resource management: add, edit, filter, and deactivate resources.
- Admin booking management: filter bookings and mark completed, no-show, or cancelled.

## Folder Structure

```text
frontend/
  src/
    components/     Shared UI, layout, resource, and suggestion cards
    data/           Runtime-relative demo data
    router/         Vue Router routes and auth guards
    services/       Thin API layer ready for backend replacement
    stores/         Pinia auth, resource, and booking stores
    utils/          Booking validation, conflict detection, and tests
    views/          Public, student, and admin pages
```

## Booking Rules

The booking utility enforces:

- Same-resource overlap blocking.
- Same-user overlap blocking.
- Maximum 2-hour bookings.
- Past-slot rejection.
- Cancelled bookings ignored during conflict checks.
- Smart suggestions for nearby times and similar resources.

## Backend Connection Plan

`src/services/api.js` is intentionally thin. Replace the mock methods with Spring Boot endpoints while keeping store methods stable:

- `login`
- `getResources`
- `getBookings`
- `createBooking`
- `updateBookingStatus`
- `getAnalytics`

## Known Limitations

- Data is in-memory mock data and resets on page refresh.
- Authentication is demo-only and stored in local storage.
- Admin edit/delete actions are simplified for project demonstration.

## Future Improvements

- Spring Boot API integration.
- Calendar grid view for resource availability.
- Email notifications.
- QR check-in and no-show automation.
- Approval workflow for special resources.
