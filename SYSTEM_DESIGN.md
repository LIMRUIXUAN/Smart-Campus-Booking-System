# System Design

## Problem Situation

Campus rooms, labs, and shared equipment are often booked through manual messages or separate spreadsheets. Students cannot reliably see whether a resource is available, and administrators have limited visibility into conflicts, no-shows, and usage demand.

Roomio centralizes campus resource reservations in one full-stack system. It prevents confirmed booking conflicts, suggests alternatives when a slot is unavailable, and gives administrators operational analytics.

## Target Users

- Students who need to find and reserve rooms, labs, or equipment.
- Campus administrators who manage resources, booking status, and usage patterns.

## System Goals

- Provide a clear resource browsing and booking flow.
- Enforce booking rules on the backend as the source of truth.
- Persist users, resources, and bookings in MySQL.
- Protect student and admin actions with JWT authentication.
- Give admins useful summaries of demand, cancellations, and no-shows.

## User Roles

- Student: register, log in, browse resources, check availability, create bookings, view own bookings, and cancel own bookings.
- Admin: log in, manage resources, view all bookings, update booking outcomes, and view analytics.

## Core Features

- JWT login and registration with BCrypt password hashing.
- Resource inventory for rooms, labs, and equipment.
- Availability checks before booking confirmation.
- Booking creation with conflict prevention.
- Student booking history and cancellation.
- Admin resource management and booking status updates.
- Admin analytics for totals, peak hour, resource usage, and status distribution.

## Main User Flows

1. Student logs in or registers.
2. Student browses active campus resources.
3. Student opens a resource details page.
4. Student enters date, time, event name, and pax.
5. System checks backend availability.
6. If available, student confirms the booking.
7. If unavailable, system returns conflict context and smart suggestions.
8. Student may select a suggested time or resource and retry.

Admin flow:

1. Admin logs in.
2. Admin reviews analytics dashboard.
3. Admin adds, edits, or deactivates resources.
4. Admin reviews all bookings.
5. Admin marks bookings as completed, no-show, or cancelled.

## Data Model

User:

- `id`
- `name`
- `email`
- `passwordHash`
- `role`: `STUDENT` or `ADMIN`

Resource:

- `id`
- `name`
- `type`: `ROOM`, `LAB`, or `EQUIPMENT`
- `location`
- `capacity`
- `status`: `ACTIVE` or `INACTIVE`
- `description`
- `features`

Booking:

- `id`
- `user`
- `resource`
- `eventName`
- `pax`
- `startDate`
- `startTime`
- `endDate`
- `endTime`
- `startDateTime`
- `endDateTime`
- `status`: `CONFIRMED`, `CANCELLED`, `COMPLETED`, or `NO_SHOW`

Relationships:

- One user has many bookings.
- One resource has many bookings.
- A booking belongs to exactly one user and one resource.

## Booking Conflict Logic

Backend `BookingService` enforces all booking rules:

- Reject past bookings.
- Require duration from 30 minutes to 2 hours.
- Reject inactive resources.
- Reject pax above resource capacity.
- Block only overlapping `CONFIRMED` bookings.
- Block conflicts for the same resource.
- Block conflicts for the same user across resources.
- Allow cancelled slots to be reused.
- Use the overlap rule: `newStart < existingEnd && newEnd > existingStart`.

Back-to-back bookings are allowed. For example, a new booking ending at 10:00 is allowed when an existing booking starts at 10:00.

## Smart Suggestion Logic

When a selected confirmed slot is unavailable, the backend tries:

- Same resource one hour later.
- Same resource one hour earlier.
- Same resource two hours later.
- Similar active resources with the same type and enough capacity.

Suggestions are returned only when they pass the same backend booking validation rules.

## Admin Analytics Logic

Analytics are calculated from persisted bookings:

- Total bookings.
- Active bookings, excluding cancelled records.
- Cancelled count.
- No-show count.
- Most booked resource.
- Peak booking start hour.
- Per-resource usage.
- Booking status distribution.

## Limitations And Future Improvements

- No email notification workflow yet.
- No QR check-in or automated no-show detection.
- No recurring bookings.
- No waitlist when a resource is fully booked.
- Resource deletion is implemented as deactivation to preserve booking history.
