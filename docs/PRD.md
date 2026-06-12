# Product Requirements Document: Roomio Smart Campus Booking System

Version: 1.0  
Date: 2026-06-12  
Product: Roomio / Smart Campus Booking System  
Repository: Smart-Campus-Booking-System  
Prepared for: Product planning, client pitching, and implementation roadmap

## 1. Executive Summary

Roomio is a web-based resource booking platform for campuses and campus-like organisations. It lets students or members browse shared resources, check real-time availability, reserve rooms, labs, and equipment, and receive alternative suggestions when a selected slot is unavailable. Administrators can manage the resource inventory, monitor bookings, update booking outcomes, and review analytics.

The current implementation is suitable as a minimum viable product for education institutions that need a lightweight alternative to spreadsheets, chat-based booking, counter-based booking, or fragmented room reservation tools. With targeted enhancements, Roomio can also be adapted for libraries, training centres, coworking spaces, corporate offices, municipalities, community centres, and sports or recreation facilities.

The best first commercial fit is Malaysian higher education and TVET institutions because the Ministry of Higher Education lists a large reachable market: 20 public universities, 36 polytechnics, 106 community colleges, and 374 private higher education institutions as of the MoHE portal snapshot reviewed on 2026-06-12. Existing Malaysian campus examples show demand for discussion room and study space booking, with policies such as maximum booking duration, minimum users, online booking, and check-in/no-show rules.

## 2. Research Sources

The following sources were used to ground market fit, target clients, and enhancement recommendations:

1. Ministry of Higher Education Malaysia official portal, "Number of Higher Education Institute" section: https://www.mohe.gov.my/en
2. Monash University Malaysia Library spaces page: https://www.monash.edu.my/library/services-facilities/library-spaces
3. Monash University Malaysia discussion room LibCal upgrade notice: https://www.monash.edu.my/library/about/news/2020/articles/discussion-room-libcal-booking-system-upgrade
4. Taylor's University library facilities page: https://university.taylors.edu.my/en/student-life/campus-facilities/taylors-library.html
5. Swinburne University of Technology Sarawak "Book a discussion room" page: https://www.swinburne.edu.my/library/computers-study-space/book-a-discussion-room-or-computer/
6. Perdana University Library portal FAQ: https://lrc.perdanauniversity.edu.my/
7. Springshare LibCal academic libraries product page: https://www.springshare.com/academic-libraries/libcal
8. Springshare LibCal product page: https://www.springshare.com/libcal
9. Skedda room booking software guide: https://www.skedda.com/insights/room-booking-software-hybrid-meetings
10. Skedda facility booking software guide: https://www.skedda.com/insights/facility-booking-software
11. Skedda workplace intelligence page: https://www.skedda.com/platform/workplace-intelligence
12. Robin room scheduling product page: https://robinpowered.com/platform/room-scheduling
13. Robin workplace management product page: https://robinpowered.com/

## 3. Product Vision

Roomio should become the simplest way for an organisation to make shared spaces and equipment visible, bookable, governed, and measurable.

The product should:

- Reduce double bookings and manual coordination.
- Give users confidence that a selected resource is available.
- Help administrators enforce booking policies without repetitive manual checks.
- Provide analytics that support facility planning, staffing, and resource investment.
- Be configurable enough to serve different organisation types without forking the application for every client.

## 4. Problem Statement

Many campuses and shared-space organisations still manage room, lab, or equipment reservations through emails, messaging apps, spreadsheets, paper logs, front-desk requests, or disconnected systems. This creates several operational issues:

- Users cannot easily see live resource availability.
- Staff spend time confirming, rejecting, or changing bookings manually.
- Double bookings and user schedule conflicts happen when data is not centralised.
- No-show bookings waste limited rooms and equipment.
- Administrators lack reliable analytics for peak hours, underused spaces, demand patterns, or cancellation/no-show trends.
- Different departments may apply different booking rules, creating inconsistent user experiences.

Roomio addresses the core problem by centralising booking inventory, enforcing backend availability rules, showing conflict feedback, suggesting alternatives, and giving admins usage visibility.

## 5. Current Product Snapshot

### 5.1 Current Stack

- Frontend: Vue 3, Vite, Pinia, Vue Router, Tailwind CSS, Axios, Chart.js, Vitest.
- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, JWT, Maven.
- Database: MySQL for runtime, H2 for backend tests.

### 5.2 Current Roles

- Student: register, login, browse resources, view resource details, check availability, create bookings, view own bookings, update/cancel own bookings, manage profile and notification preferences.
- Admin: login, manage resources, view all bookings, update booking status, view analytics, manage profile.

### 5.3 Current Resource Types

- Room.
- Lab.
- Equipment.

### 5.4 Current Booking Statuses

- Confirmed.
- Cancelled.
- Completed.
- No-show.

### 5.5 Current Core Capabilities

- JWT authentication and role-based navigation.
- User registration and login.
- Password reset flow with mailer support.
- Profile update, password change, email verification, two-factor setup, and notification preference endpoints.
- Resource list, detail, creation, update, and deactivation.
- Availability checking.
- Conflict prevention for confirmed bookings.
- Same-user overlapping booking prevention.
- Booking duration rule: minimum 30 minutes and maximum 2 hours.
- Capacity validation.
- Smart suggestions for nearby time slots or similar resources.
- Student "My Bookings" view.
- Admin booking management.
- Admin analytics summary, resource usage, and booking status distribution.

## 6. Market And Organisation Suitability

### 6.1 Best-Fit Organisation Types

Roomio is most suitable for organisations where:

- Resources are shared by many users.
- Time-slot availability matters.
- Conflicts are costly or disruptive.
- Staff need to enforce policy rules.
- Usage data can improve planning.

### 6.2 Primary Segment: Higher Education Institutions

Fit level: Very high.

Why this segment fits:

- Campuses have many shared resources: study rooms, discussion rooms, classrooms, labs, studios, halls, sports courts, equipment, and computer stations.
- Students expect self-service digital access.
- Admins need rules such as booking duration, minimum group size, no-show handling, and opening hours.
- MoHE Malaysia lists 536 higher education-related institutions across public universities, polytechnics, community colleges, and private higher education institutions, creating a meaningful local market.

Relevant evidence:

- Monash University Malaysia lists 8 group discussion rooms with rules including minimum group size and maximum daily booking duration.
- Taylor's University lists discussion rooms where booking is required beforehand.
- Swinburne Sarawak has an online discussion room booking flow and policies such as maximum 2 hours per booking, minimum users, and booking termination for missed check-in.
- Perdana University Library states that study rooms can be reserved through an online booking system or at the library counter.

Best buyer personas:

- Library director or library operations manager.
- Student affairs director.
- Facilities management office.
- Academic services office.
- IT services department.
- Deputy vice-chancellor office for student experience or operations.

### 6.3 Secondary Segment: TVET, Polytechnics, And Training Centres

Fit level: High.

Why this segment fits:

- Labs, workshops, machines, classrooms, studios, and training equipment require controlled scheduling.
- Safety, capacity, instructor approval, and prerequisite checks may matter.
- Many campuses need low-cost digital tools rather than enterprise facility platforms.

Recommended positioning:

"A practical booking system for labs, workshops, classrooms, and shared equipment, with approval rules and usage reporting."

### 6.4 Secondary Segment: Corporate Offices And Hybrid Workplaces

Fit level: Medium to high after customization.

Why this segment fits:

- Modern offices need meeting room, desk, parking, visitor, and equipment booking.
- Competitors such as Skedda and Robin highlight real-time availability, resource matching, QR check-in, mobile booking, analytics, visitor management, and smart room suggestions.

Required customization:

- Replace student/admin language with employee/facility-admin language.
- Add floor maps, desk booking, recurring meetings, calendar integrations, visitor management, and check-in/release workflows.

### 6.5 Secondary Segment: Public Libraries And Community Facilities

Fit level: High after customization.

Why this segment fits:

- Public libraries and community centres manage rooms, halls, computers, maker spaces, equipment, events, and appointments.
- Springshare LibCal positions library booking as space bookings, appointment scheduling, equipment booking, and event management.

Required customization:

- Patron accounts or guest booking.
- Staff mediation and approval.
- Public event calendar.
- Payment or deposit support for paid facility rentals.
- Multi-branch support.

### 6.6 Secondary Segment: Coworking Spaces And Incubators

Fit level: Medium.

Why this segment fits:

- Coworking spaces need room, desk, event space, and visitor booking.
- Booking rules often depend on membership tier, monthly quota, credits, or payments.

Required customization:

- Membership plans.
- Usage credits.
- Billing integration.
- Guest invitation.
- Door access or QR check-in.

### 6.7 Secondary Segment: Sports And Recreation Facilities

Fit level: Medium.

Why this segment fits:

- Courts, studios, gyms, fields, and equipment are schedule-based resources.
- Capacity, peak pricing, cancellation windows, and recurring team reservations are common.

Required customization:

- Resource categories for court, field, lane, room, equipment.
- Paid booking support.
- Recurring booking seasons.
- Waiver and safety acknowledgement.

## 7. Potential Client Shortlist

This shortlist contains organisations or organisation categories where Roomio can be pitched. Inclusion does not mean the organisation currently lacks a booking system; it means the organisation has visible booking-relevant facilities, a likely operational need, or a segment fit.

### 7.1 Malaysian Higher Education Target Accounts

1. Small and mid-sized private higher education institutions
   - Why suitable: Often need cost-effective systems and may not want heavyweight enterprise facility software.
   - Pitch angle: "Campus booking with fast setup, role-based admin, and custom institutional rules."

2. Community colleges and polytechnics
   - Why suitable: Shared labs, workshops, classrooms, and equipment need scheduling and utilisation data.
   - Pitch angle: "Lab and resource booking with safety/approval rules and usage reporting."

3. University libraries
   - Why suitable: Study rooms and discussion rooms are high-demand resources with policy constraints.
   - Pitch angle: "Library room booking with check-in, no-show release, and student-friendly availability."

4. Student affairs or facilities departments in public universities
   - Why suitable: Large campuses have many scattered resources and departments.
   - Pitch angle: "One campus-wide booking layer for rooms, halls, labs, and equipment."

### 7.2 Specific Prospect Examples To Research Further

1. Perdana University Library
   - Public signal: Study rooms can be reserved online or at the library counter.
   - Opportunity: Improve or complement the current booking flow with analytics, no-show handling, and admin reporting.

2. Taylor's University Library
   - Public signal: Discussion rooms require booking; room types include wireless display and touch screen display.
   - Opportunity: Strong fit for equipment-aware room matching and smart suggestions by room capability.

3. Monash University Malaysia Library and Learning Commons
   - Public signal: Group discussion room rules include minimum group size and maximum booking duration.
   - Opportunity: Useful benchmark client for rule configuration and calendar-based availability.

4. Swinburne University of Technology Sarawak Library
   - Public signal: Uses an online booking process with policies such as maximum duration, minimum users, and check-in/no-show termination.
   - Opportunity: Shows advanced rule expectations; Roomio would need check-in and auto-release features to compete.

5. Private university colleges and foreign branch campuses in Malaysia
   - Public signal: The higher education market includes hundreds of PHEIs under MoHE.
   - Opportunity: Position Roomio as a locally customizable booking platform with lower implementation friction.

### 7.3 Non-Campus Prospects

1. Coworking spaces and incubators in Kuala Lumpur, Selangor, Penang, and Johor
   - Need: Meeting rooms, event spaces, hot desks, phone booths, visitor check-in.
   - Required feature additions: Membership quotas, payments, QR check-in, visitor invitations.

2. Corporate training centres
   - Need: Classrooms, labs, trainers, equipment, event schedules.
   - Required feature additions: Approval flow, recurring classes, instructor assignment, attendance.

3. Public libraries and local councils
   - Need: Public rooms, computer stations, halls, workshops, public events.
   - Required feature additions: Guest bookings, branch management, moderation, public-facing calendar.

4. Community centres, clubs, and associations
   - Need: Hall, room, sports, and equipment reservations.
   - Required feature additions: Payments, deposits, member/non-member pricing, cancellation rules.

## 8. Competitive And Feature Benchmark

### 8.1 LibCal Benchmark

LibCal is positioned for libraries and supports room reservations, appointment scheduling, equipment lending, event calendars, payments, and APIs. This validates that Roomio should not only handle rooms; it should grow toward spaces, equipment, events, appointments, and integrations.

Feature implication for Roomio:

- Add equipment lending workflows.
- Add appointment booking for library help, academic advising, or support teams.
- Add event registration and public calendars.
- Add optional payment/deposit flows.
- Add APIs and embeddable booking widgets.

### 8.2 Skedda Benchmark

Skedda emphasizes real-time availability, conflict prevention, resource matching, mobile booking, QR check-ins, push notifications, analytics, automation, visitor management, and auto-release of ghost bookings.

Feature implication for Roomio:

- Add QR check-in.
- Add automatic release when a user does not check in within a configured grace period.
- Add notification reminders.
- Add facility policy rules and quota automation.
- Add stronger analytics by room, user group, department, and check-in method.

### 8.3 Robin Benchmark

Robin positions room scheduling around availability previews, on-the-go booking, smart suggestions based on capacity/resources/technology, workplace management, space planning, and analytics.

Feature implication for Roomio:

- Enhance suggestions to match requested capabilities, not only resource type.
- Add floor/facility context.
- Add room equipment metadata such as projector, display, whiteboard, video conferencing, accessibility, and seating layout.
- Add planning analytics for utilisation and demand.

## 9. Target Users And Personas

### 9.1 Student / Member / Employee

Goals:

- Find a suitable room or resource quickly.
- Know whether a slot is available before committing.
- Avoid back-and-forth messages.
- Manage upcoming reservations.

Pain points:

- Unclear availability.
- Inconsistent rules by room or department.
- No easy alternatives when a room is full.
- Forgetting bookings or not knowing check-in rules.

Success criteria:

- Booking can be completed in under 2 minutes.
- Conflicts are explained clearly.
- Alternative slots are useful.
- Reminders reduce missed bookings.

### 9.2 Resource Administrator

Goals:

- Keep resource inventory accurate.
- Approve or reject bookings where needed.
- Reduce double bookings and policy violations.
- Identify no-shows, cancellations, and peak demand.

Pain points:

- Manual booking checks.
- Users ignoring booking limits.
- No easy way to see utilisation.
- Rooms booked but unused.

Success criteria:

- Common rules are automated.
- Admin can view and filter all bookings.
- No-show and cancellation data is visible.
- Admin workload decreases.

### 9.3 Department Manager / Facility Director

Goals:

- Understand demand and utilisation.
- Improve space allocation.
- Decide whether to add, repurpose, or retire resources.
- Monitor service quality.

Pain points:

- Data scattered across departments.
- No reliable utilisation reports.
- Peak demand not measured.
- Procurement decisions based on anecdote.

Success criteria:

- Dashboard shows usage trends and bottlenecks.
- Reports can be exported.
- Data supports planning and budget requests.

### 9.4 IT Administrator

Goals:

- Integrate with campus identity and security standards.
- Manage access safely.
- Support deployment, backup, and monitoring.

Pain points:

- Local accounts can become hard to manage.
- Security review requires audit trails.
- Production maintenance needs predictable configuration.

Success criteria:

- SSO integration is available.
- Role and permission management is clear.
- Audit logs exist.
- Deployment uses documented environment configuration.

## 10. Product Goals

### 10.1 MVP Goals

- Provide authenticated self-service booking.
- Prevent confirmed booking overlaps.
- Support resource browsing and filtering.
- Provide useful conflict messages and suggestions.
- Let admins manage resources and booking statuses.
- Provide basic analytics.

### 10.2 Next Release Goals

- Make rules configurable per organisation and resource.
- Add approval workflows.
- Add calendar view.
- Add QR check-in and no-show release.
- Add notification reminders.
- Add exports and better analytics.
- Add multi-organisation or multi-campus foundations.

### 10.3 Long-Term Goals

- Become a configurable reservation platform for education, workplace, library, and community organisations.
- Support integrations with identity providers, calendars, email/SMS/push notification services, payments, door access, and BI tools.
- Provide data-backed recommendations for space planning.

## 11. Scope

### 11.1 In Scope For Current Product

- Student and admin authentication.
- Student resource browsing.
- Student booking creation and cancellation.
- Booking update for confirmed bookings.
- Admin resource management.
- Admin booking status management.
- Availability checks and conflict prevention.
- Basic smart suggestions.
- Basic analytics.
- Profile and security preference endpoints.

### 11.2 Recommended Scope For Production Pilot

- Organisation settings.
- Configurable booking policies.
- Calendar/schedule view.
- Email notifications.
- QR check-in.
- No-show automation.
- Approval workflow.
- Audit log.
- Export CSV.
- Basic SSO readiness.
- Data backup and deployment documentation.

### 11.3 Out Of Scope For First Production Pilot

- Native mobile apps.
- Full AI scheduling assistant.
- Complex payment settlement.
- Door lock hardware integration.
- Advanced multi-tenant billing.
- Full ERP/SIS integration.

## 12. Functional Requirements

### 12.1 Authentication And Account Management

FR-AUTH-001: Users must be able to register with name, email, and password.  
FR-AUTH-002: Users must be able to login with email and password.  
FR-AUTH-003: System must issue JWT tokens after successful authentication.  
FR-AUTH-004: System must route users based on role.  
FR-AUTH-005: Users must be able to update profile information.  
FR-AUTH-006: Users must be able to change passwords.  
FR-AUTH-007: Users must be able to request and complete password reset.  
FR-AUTH-008: Users should be able to verify email.  
FR-AUTH-009: Users should be able to enable/disable two-factor verification.  
FR-AUTH-010: Future production versions should support SSO through SAML or OIDC.

### 12.2 Roles And Permissions

FR-RBAC-001: Student/member role can view resources and manage own bookings.  
FR-RBAC-002: Admin role can manage resources and all bookings.  
FR-RBAC-003: Admin role can update booking status.  
FR-RBAC-004: Future production versions should support custom roles such as Library Staff, Facility Staff, Department Admin, Approver, Security, and Super Admin.  
FR-RBAC-005: Future production versions should support permission scopes by campus, branch, building, department, or resource group.

### 12.3 Resource Management

FR-RES-001: Admin can create a resource.  
FR-RES-002: Admin can update resource name, type, location, capacity, status, description, image, and features.  
FR-RES-003: Admin can deactivate resources instead of hard deleting them.  
FR-RES-004: Users can browse active resources.  
FR-RES-005: Users can filter by type, location, capacity, and availability.  
FR-RES-006: Future production versions should support resource-specific policies, opening hours, buffers, equipment, layout, accessibility, approval requirement, and owner department.  
FR-RES-007: Future production versions should support nested locations: campus, building, floor, zone, room.

### 12.4 Availability And Booking Rules

FR-AVL-001: System must reject bookings where end time is not after start time.  
FR-AVL-002: System must reject past bookings.  
FR-AVL-003: System must enforce minimum booking duration.  
FR-AVL-004: System must enforce maximum booking duration.  
FR-AVL-005: System must reject inactive resources.  
FR-AVL-006: System must reject bookings where requested pax exceeds capacity.  
FR-AVL-007: System must prevent overlapping confirmed bookings for the same resource.  
FR-AVL-008: System must prevent overlapping confirmed bookings for the same user across resources.  
FR-AVL-009: System must allow back-to-back bookings.  
FR-AVL-010: System must allow cancelled slots to be reused.  
FR-AVL-011: Future production versions should support configurable business hours.  
FR-AVL-012: Future production versions should support resource setup/cleanup buffers.  
FR-AVL-013: Future production versions should support quotas such as max hours per day, max bookings per week, max active bookings, and min/max group size.

### 12.5 Booking Creation And Management

FR-BOOK-001: Student/member can create a booking by selecting resource, event name, pax, start date/time, and end date/time.  
FR-BOOK-002: Student/member can view own bookings.  
FR-BOOK-003: Student/member can cancel own bookings.  
FR-BOOK-004: Student/member can update own confirmed booking within policy.  
FR-BOOK-005: Admin can view all bookings.  
FR-BOOK-006: Admin can update booking status to confirmed, completed, cancelled, or no-show.  
FR-BOOK-007: Future production versions should support pending approval status.  
FR-BOOK-008: Future production versions should support booking rejection with reason.  
FR-BOOK-009: Future production versions should support recurring bookings.  
FR-BOOK-010: Future production versions should support waitlist when a slot is full.

### 12.6 Smart Suggestions

FR-SUG-001: System must suggest nearby available times for the same resource when a conflict occurs.  
FR-SUG-002: System must suggest similar active resources when suitable.  
FR-SUG-003: Suggestions must pass the same backend validation as normal bookings.  
FR-SUG-004: Future production versions should rank suggestions by capacity fit, location distance, equipment match, accessibility, user preference, and policy score.  
FR-SUG-005: Future production versions should allow users to apply a suggestion in one action.

### 12.7 Check-In And No-Show Management

FR-CHECK-001: Future production versions should generate a QR code for each booking.  
FR-CHECK-002: Future production versions should allow check-in by scanning room QR or booking QR.  
FR-CHECK-003: Future production versions should apply a configurable grace period, such as 10 or 15 minutes.  
FR-CHECK-004: Future production versions should auto-release bookings not checked in after the grace period.  
FR-CHECK-005: Future production versions should mark repeated missed check-ins as no-show events.  
FR-CHECK-006: Future production versions should expose no-show analytics by user group, resource, and time.

### 12.8 Notifications

FR-NOTIF-001: System should send booking confirmation email.  
FR-NOTIF-002: System should send booking update and cancellation email.  
FR-NOTIF-003: System should send reminder before booking start.  
FR-NOTIF-004: System should send check-in reminder when applicable.  
FR-NOTIF-005: Future production versions should support admin announcements for affected bookings.  
FR-NOTIF-006: Future production versions should support SMS, WhatsApp, push, Microsoft Teams, or Slack depending on organisation type.

### 12.9 Analytics And Reporting

FR-AN-001: Admin can view total bookings.  
FR-AN-002: Admin can view active bookings.  
FR-AN-003: Admin can view cancellation count.  
FR-AN-004: Admin can view no-show count.  
FR-AN-005: Admin can view most booked resource.  
FR-AN-006: Admin can view peak booking hour.  
FR-AN-007: Admin can view per-resource usage.  
FR-AN-008: Admin can view booking status distribution.  
FR-AN-009: Future production versions should support date range filters.  
FR-AN-010: Future production versions should support export CSV/PDF.  
FR-AN-011: Future production versions should support utilisation rate by available hours, not just booking count.  
FR-AN-012: Future production versions should support dashboard segmentation by building, department, role, resource type, and user group.

### 12.10 Admin Configuration

FR-CONFIG-001: Future production versions should allow admins to configure organisation name, logo, theme, timezone, and contact details.  
FR-CONFIG-002: Future production versions should allow admins to configure booking policies without code changes.  
FR-CONFIG-003: Future production versions should allow resource groups to inherit policies from organisation defaults.  
FR-CONFIG-004: Future production versions should support custom fields on resources and bookings.  
FR-CONFIG-005: Future production versions should support terms and conditions per resource or resource group.

## 13. Non-Functional Requirements

### 13.1 Security

- Passwords must be hashed with a strong algorithm.
- JWT secret must be stored outside source control.
- Admin endpoints must require admin authority.
- User endpoints must prevent access to other users' private bookings.
- Sensitive events must be audit logged.
- Production must use HTTPS.
- Production should support rate limiting for login and reset flows.
- Production should support SSO for institutional clients.

### 13.2 Privacy And Compliance

- Store only required personal data.
- Allow admins to define retention rules for booking history.
- Support data export for institutional reporting.
- Avoid exposing private booking details to unauthorised users.
- For Malaysian clients, align operational practice with PDPA expectations.

### 13.3 Reliability

- Booking creation must be transactionally safe.
- Conflict checks must be enforced on the backend.
- Database constraints or transaction isolation should prevent race-condition double bookings under concurrent load.
- System should have backup and restore procedures.

### 13.4 Performance

- Resource list should load within 2 seconds for normal campus inventory sizes.
- Availability checks should respond within 1 second under typical load.
- Admin booking tables should support pagination and filters.
- Analytics should remain responsive by using database aggregation or materialised summaries when data grows.

### 13.5 Accessibility

- Forms must have labels.
- Error messages must be clear.
- Status must not rely only on colour.
- Keyboard navigation must work for booking flows.
- Colour contrast should meet WCAG AA where practical.

### 13.6 Maintainability

- Business rules should be isolated in service classes or policy modules.
- Organisation-specific configuration should live in database tables or config, not hard-coded branches.
- Tests should cover booking policy edge cases.

## 14. Success Metrics

### 14.1 User Adoption Metrics

- Percentage of active users who create at least one booking per month.
- Average time to complete a booking.
- Booking completion rate.
- Suggestion acceptance rate.

### 14.2 Operations Metrics

- Number of prevented conflicts.
- Reduction in manual booking requests.
- No-show rate.
- Auto-release recovery hours.
- Resource utilisation rate.
- Peak demand by hour/day.

### 14.3 Admin Metrics

- Time spent managing booking requests.
- Number of resources actively managed.
- Number of policy violations prevented.
- Report/export usage.

### 14.4 Business Metrics

- Number of pilot organisations.
- Conversion from pilot to paid deployment.
- Monthly active organisations.
- Retention by organisation type.
- Support tickets per active organisation.

## 15. Customization Strategy By Organisation Type

The recommended architecture is a configurable core, not separate codebases per organisation. The product should support organisation profiles, role labels, policy sets, resource taxonomies, custom fields, notification templates, branding, and integrations.

### 15.1 Higher Education / University

Terminology:

- User: Student, Staff, Lecturer.
- Admin: Facility Admin, Library Admin, Department Admin.
- Resource: Study Room, Discussion Room, Lab, Classroom, Equipment, Hall.

Custom policies:

- Max 2 hours per booking.
- Max 1 or 2 bookings per day.
- Minimum group size for discussion rooms.
- Opening hours by building.
- Exam-week policy overrides.
- Department-only rooms.
- Approval for halls, labs, or special equipment.

Recommended features:

- SSO with campus identity.
- QR check-in.
- No-show release.
- Calendar view.
- Resource equipment matching.
- CSV reports for management.

### 15.2 TVET / Polytechnic / Training Centre

Terminology:

- User: Trainee, Instructor, Staff.
- Resource: Workshop, Machine, Lab, Classroom, Tool Set.

Custom policies:

- Instructor approval for high-risk labs.
- Safety certification prerequisite.
- Setup/cleanup buffer.
- Equipment checkout and return.
- Maintenance blockout.

Recommended features:

- Approval workflow.
- Safety acknowledgement.
- Equipment condition checklist.
- Maintenance scheduling.
- Instructor assignment.

### 15.3 Library

Terminology:

- User: Patron, Student, Member.
- Resource: Study Room, Discussion Room, Computer Station, Studio, Equipment Kit.

Custom policies:

- Minimum users for group rooms.
- Maximum daily or weekly hours.
- Check-in grace period.
- Auto-release if unattended.
- Staff mediation for premium rooms.

Recommended features:

- Public booking widget.
- QR check-in.
- Equipment lending.
- Staff appointment scheduler.
- Event calendar.

### 15.4 Corporate Office / Hybrid Workplace

Terminology:

- User: Employee, Contractor, Visitor.
- Resource: Meeting Room, Desk, Phone Booth, Parking Bay, Event Space.

Custom policies:

- Team or floor access rules.
- Visitor approval.
- Recurring meeting reservations.
- Room release when meeting is cancelled.
- Booking quota by team.

Recommended features:

- Microsoft 365 or Google Calendar integration.
- SSO/OIDC.
- Interactive floor map.
- QR or Wi-Fi check-in.
- Visitor management.
- Workplace analytics.

### 15.5 Coworking Space

Terminology:

- User: Member, Guest, Host.
- Resource: Meeting Room, Desk, Studio, Event Space.

Custom policies:

- Membership-tier booking quotas.
- Paid booking beyond free credits.
- Cancellation window.
- Guest access rules.

Recommended features:

- Payments.
- Membership billing integration.
- Credits wallet.
- Door access integration.
- Public booking pages.

### 15.6 Public Sector / Community Centre

Terminology:

- User: Resident, Citizen, Member, Officer.
- Resource: Hall, Classroom, Court, Meeting Room, Equipment.

Custom policies:

- Public/private event categories.
- Approval before confirmation.
- Deposits and refundable fees.
- Document upload.
- Maintenance blockout.

Recommended features:

- Guest booking.
- Payment gateway.
- Admin approval board.
- Public event calendar.
- Printable permit or booking slip.

## 16. Data Model Recommendations

The current model supports User, Resource, and Booking. To support customisation, add the following entities over time:

### 16.1 Organisation

Fields:

- id.
- name.
- slug.
- logoUrl.
- primaryColor.
- timezone.
- contactEmail.
- contactPhone.
- defaultLocale.
- createdAt.

Purpose:

- Enables multi-tenant or multi-organisation operation.
- Stores branding and global settings.

### 16.2 Location

Fields:

- id.
- organisationId.
- campusName.
- buildingName.
- floorName.
- zoneName.
- address.

Purpose:

- Supports maps, filtering, and multi-campus deployments.

### 16.3 ResourcePolicy

Fields:

- id.
- organisationId.
- resourceId nullable.
- resourceGroupId nullable.
- minDurationMinutes.
- maxDurationMinutes.
- minPax.
- maxBookingsPerDay.
- maxHoursPerWeek.
- openingHoursJson.
- checkInRequired.
- checkInGraceMinutes.
- approvalRequired.
- bufferBeforeMinutes.
- bufferAfterMinutes.

Purpose:

- Makes booking rules configurable without code changes.

### 16.4 BookingApproval

Fields:

- id.
- bookingId.
- approverUserId.
- status.
- decisionReason.
- decidedAt.

Purpose:

- Supports approval workflows for labs, halls, and restricted resources.

### 16.5 CheckIn

Fields:

- id.
- bookingId.
- method.
- checkedInAt.
- checkedOutAt.
- deviceId.
- locationVerified.

Purpose:

- Supports QR check-in, check-out, no-show tracking, and utilisation analytics.

### 16.6 AuditLog

Fields:

- id.
- organisationId.
- actorUserId.
- action.
- entityType.
- entityId.
- beforeJson.
- afterJson.
- createdAt.

Purpose:

- Supports security review, operational traceability, and client trust.

### 16.7 Notification

Fields:

- id.
- organisationId.
- userId.
- bookingId nullable.
- channel.
- templateKey.
- status.
- sentAt.
- failureReason.

Purpose:

- Supports delivery tracking for reminders and confirmations.

## 17. Enhanced Implementation Roadmap

### Phase 0: Stabilise Existing MVP

Priority: Immediate.

Deliverables:

- Verify backend tests.
- Verify frontend build and tests.
- Add API pagination for admin booking list if data grows.
- Add loading, empty, and error states where missing.
- Review role guards and admin endpoint security.
- Add deployment checklist.

Acceptance criteria:

- Backend tests pass.
- Frontend lint/test/build pass.
- All documented demo flows work.

### Phase 1: Pilot-Ready Campus Product

Priority: High.

Deliverables:

- Organisation settings: name, logo, timezone, contact.
- Configurable booking policy table.
- Resource opening hours.
- Calendar availability view.
- Email booking confirmations and reminders.
- CSV export for bookings and analytics.
- Audit log for admin actions.
- Admin filters by date, status, resource, type, and user.

Acceptance criteria:

- Admin can configure booking duration and opening hours without code.
- User cannot book outside opening hours.
- Admin can export bookings for a selected date range.
- Every admin resource/status change appears in audit log.

### Phase 2: High-Value Differentiators

Priority: High.

Deliverables:

- QR check-in.
- No-show grace period and auto-release.
- Pending approval workflow.
- Waitlist for full slots.
- Improved smart suggestions using capacity, features, location, and opening hours.
- Notification templates by organisation.

Acceptance criteria:

- Booking can require QR check-in.
- Missed check-in can auto-release the room.
- Approval-required resource creates pending booking instead of confirmed booking.
- Suggestions explain why each alternative is recommended.

### Phase 3: Client Customisation Platform

Priority: Medium.

Deliverables:

- Multi-campus and nested locations.
- Custom resource categories.
- Custom booking fields.
- Custom role labels and permissions.
- Theme and branding.
- Public booking widget.
- Terms and conditions per resource.

Acceptance criteria:

- A university and a coworking space can use different labels, resource types, and policies from the same codebase.
- Admin can add custom fields such as student ID, department, purpose, or event type.
- Public booking page can be embedded or linked.

### Phase 4: Integrations And Enterprise Readiness

Priority: Medium.

Deliverables:

- OIDC/SAML SSO.
- Microsoft 365 and Google Calendar sync.
- Webhooks.
- Payment gateway for paid resources.
- Door access integration readiness.
- BI export or API endpoints.
- Background jobs for reminders and auto-release.

Acceptance criteria:

- Institutional users can login through SSO.
- Booking changes can sync to external calendars.
- External systems can subscribe to booking events.

### Phase 5: Advanced Intelligence

Priority: Later.

Deliverables:

- Utilisation forecasting.
- Demand heatmaps.
- AI-assisted room recommendation.
- Admin anomaly alerts for repeated no-shows or overloaded resources.
- Space planning recommendations.

Acceptance criteria:

- Admin can identify underused and overused resources.
- User receives ranked recommendations that match capacity, location, and required equipment.

## 18. API Enhancement Requirements

Recommended new endpoints:

- `GET /api/organisations/current`
- `PUT /api/organisations/current`
- `GET /api/policies`
- `POST /api/policies`
- `PUT /api/policies/{id}`
- `GET /api/locations`
- `POST /api/locations`
- `GET /api/resources/{id}/calendar`
- `POST /api/bookings/{id}/check-in`
- `POST /api/bookings/{id}/check-out`
- `POST /api/bookings/{id}/approve`
- `POST /api/bookings/{id}/reject`
- `GET /api/bookings/export`
- `GET /api/audit-logs`
- `GET /api/analytics/utilisation`
- `GET /api/analytics/no-shows`
- `GET /api/analytics/heatmap`

## 19. User Experience Requirements

### 19.1 Student Booking Flow

1. User logs in.
2. User browses resources.
3. User filters by type, capacity, location, features, and availability.
4. User opens resource details.
5. User selects date, time, duration, event name, and pax.
6. User checks availability.
7. If available, user confirms booking.
8. If unavailable, system explains the conflict and shows suggestions.
9. User accepts a suggestion or changes search.
10. Booking appears in "My Bookings".
11. User receives confirmation and reminder.
12. If check-in is enabled, user checks in during grace period.

### 19.2 Admin Resource Flow

1. Admin logs in.
2. Admin opens resources.
3. Admin creates or edits resource.
4. Admin assigns policy, location, capacity, and features.
5. Admin saves resource.
6. Resource becomes available according to status and policy.

### 19.3 Admin Booking Flow

1. Admin opens booking management.
2. Admin filters by date, resource, status, user, or department.
3. Admin views booking detail.
4. Admin approves, rejects, cancels, marks completed, or marks no-show.
5. System records audit log and sends notification.

### 19.4 Admin Analytics Flow

1. Admin opens dashboard.
2. Admin selects date range.
3. System shows utilisation, cancellation, no-show, and peak demand.
4. Admin exports report.

## 20. Booking Policy Examples

### 20.1 University Library Discussion Room

- Resource type: Discussion Room.
- Minimum pax: 3 or 4.
- Maximum duration: 2 hours.
- Maximum bookings per user per day: 1 or 2.
- Opening hours: Library hours.
- Check-in required: Yes.
- Grace period: 15 minutes.
- Auto-release: Yes.
- Approval required: No.

### 20.2 Computer Lab

- Resource type: Lab.
- Minimum duration: 30 minutes.
- Maximum duration: 3 hours.
- Opening hours: Department-defined.
- Approval required: Yes.
- Approver: Lab manager or lecturer.
- Setup buffer: 15 minutes.
- Safety acknowledgement: Yes.

### 20.3 Campus Hall

- Resource type: Hall.
- Minimum duration: 1 hour.
- Maximum duration: 8 hours.
- Approval required: Yes.
- Required fields: Event type, expected attendance, organiser contact, setup needs.
- Deposit/payment: Optional future feature.
- Cancellation window: Configurable.

### 20.4 Coworking Meeting Room

- Resource type: Meeting Room.
- Membership quota: Based on plan.
- Payment required after free credits.
- Check-in required: Optional.
- Guest invite: Yes.
- Calendar sync: Yes.

## 21. Risk Assessment

### 21.1 Technical Risks

Risk: Race-condition double booking under concurrent requests.  
Mitigation: Add database constraints, pessimistic locking, or serialisable transaction strategy around booking creation.

Risk: Hard-coded booking rules limit client customisation.  
Mitigation: Move rules into ResourcePolicy and OrganisationPolicy tables.

Risk: Admin tables and analytics slow down as data grows.  
Mitigation: Add pagination, indexes, date filters, database aggregation, and scheduled summaries.

Risk: Notification delivery failures reduce trust.  
Mitigation: Add notification table with retry status and admin visibility.

### 21.2 Product Risks

Risk: Institutions already use tools such as LibCal or Skedda.  
Mitigation: Position Roomio for local customisation, lower setup friction, campus-wide use, and custom policies.

Risk: Feature expectations vary widely by organisation type.  
Mitigation: Build configurable policy and terminology layers before deep vertical customisation.

Risk: Admin adoption is limited if migration is hard.  
Mitigation: Provide CSV import for resources and users, clear onboarding, and pilot templates.

### 21.3 Operational Risks

Risk: Lack of SSO creates account management burden.  
Mitigation: Add OIDC/SAML for production clients.

Risk: No check-in means ghost bookings remain.  
Mitigation: Add QR check-in and auto-release as early differentiator.

Risk: Privacy concerns around student/user data.  
Mitigation: Minimise data, restrict access, add audit logs, and provide retention controls.

## 22. Go-To-Market Recommendation

### 22.1 Beachhead Market

Start with Malaysian private higher education institutions, university colleges, and campus libraries that need a practical, branded, configurable booking tool.

Why:

- MoHE market size is large.
- Buying process may be faster in smaller/private institutions.
- Use cases are clear and demo-friendly.
- Existing public examples validate the need.

### 22.2 Pilot Offer

Offer a 4 to 8 week pilot:

- Setup for one campus or one library.
- Import up to 50 resources.
- Configure booking rules.
- Train 2 to 5 admins.
- Run with a selected student/user group.
- Deliver usage report and improvement plan.

### 22.3 Pilot Success Criteria

- At least 100 bookings during pilot or a target agreed with the client.
- Less than 2% conflict-related support issues.
- Admin can export useful usage report.
- Users rate booking flow 4 out of 5 or higher.
- Client identifies at least one operational decision supported by analytics.

### 22.4 Sales Materials To Prepare

- One-page product brief.
- Campus library demo script.
- Admin dashboard screenshots.
- Booking policy configuration examples.
- Security and deployment checklist.
- Pricing options for single campus, multi-campus, and custom deployment.

## 23. Recommended Pricing Model

This section is for planning only and should be validated with market interviews.

### 23.1 Education Pilot

- Fixed pilot fee.
- Includes setup, configuration, limited support, and report.

### 23.2 SaaS Subscription

- Base platform fee per organisation.
- Resource tier pricing, such as 1-25, 26-100, 101-500 resources.
- Optional add-ons: SSO, advanced analytics, check-in, integrations, payments.

### 23.3 Self-Hosted / Custom Deployment

- One-time implementation fee.
- Annual support and maintenance fee.
- Custom integration priced separately.

## 24. Implementation Backlog

### Must Have For Real Client Pilot

- Organisation settings.
- Configurable booking policies.
- Opening hours.
- Admin booking filters.
- CSV export.
- Email confirmation and reminders.
- Audit log.
- Production deployment guide.
- Database migration strategy.

### Should Have

- QR check-in.
- Auto-release no-show bookings.
- Approval workflow.
- Calendar availability view.
- Better smart suggestions.
- Date-range analytics.
- Resource import from CSV.

### Could Have

- Public booking widget.
- Custom fields.
- SSO.
- Google/Microsoft Calendar integration.
- Waitlist.
- Recurring bookings.
- Payment/deposit support.

### Won't Have Yet

- Native mobile app.
- Door lock integration.
- Full billing/subscription engine.
- Advanced AI planning assistant.

## 25. Acceptance Criteria Summary

The product is pilot-ready when:

- A client admin can configure basic organisation profile and booking rules.
- Users can browse, book, cancel, and receive confirmation reliably.
- Backend prevents booking conflicts under normal and tested concurrent conditions.
- Admin can manage resources and bookings without developer support.
- Admin can export operational reports.
- Notification and audit logs provide traceability.
- Deployment and backup instructions are documented.

## 26. Open Questions

1. Should the first pilot focus on libraries, whole-campus facilities, or lab/equipment booking?
2. Will the first client require SSO, or are local accounts acceptable for pilot?
3. Should bookings be confirmed instantly, approval-based, or mixed by resource?
4. Are payments or deposits required for the first target segment?
5. Should the product be deployed as SaaS, self-hosted, or both?
6. What user identity fields are required by target clients: student ID, staff ID, department, programme, phone?
7. What reporting format do admins need: CSV, PDF, dashboard only, or BI export?

## 27. Final Recommendation

Roomio is suitable for organisations that manage shared spaces and equipment, especially Malaysian higher education institutions, university libraries, TVET providers, and training centres. The current application already covers the core booking problem: browse, book, prevent conflicts, suggest alternatives, and report usage.

To become client-ready, the most important next step is not to add many vertical-specific screens. The best next step is to make the booking rules configurable. After that, add check-in/no-show release, approval workflow, notifications, calendar view, and reporting exports. These features directly match observed real-world requirements from campus libraries and benchmark platforms such as LibCal, Skedda, and Robin.

