# FieldFlow - Assignment

Phases build on each other. Complete them in order.

---

## Phase 1: Foundation

**Goal**: A working app shell with authentication and clean project architecture.

### Project Setup

- Multi-module Gradle project. How you split modules is a design decision — document your reasoning.
- Dependency injection with Hilt across modules
- Version catalog for dependency management

### Networking

- Retrofit + OkHttp networking layer
- Auth interceptor that attaches the JWT token to requests
- **Token refresh**: The API uses short-lived JWTs. Your app needs to handle token expiry gracefully — the user should never see a login screen because their token quietly expired mid-session. Study the mock API's token behavior and design accordingly.
- Base URL configurable per build variant

### Authentication

- Secure token storage (tokens must not be readable by other apps or appear in logs)
- Login screen with email/password
- Input validation and meaningful error feedback
- Session persistence — app relaunch should not require re-login if the session is still valid
- Logout clears all user data

### User Profile

- After login, fetch and display the user's profile
- This data should be available to other parts of the app without re-fetching

---

## Phase 2: Map Experience

**Goal**: An interactive map that loads businesses from the API and lets users explore them.

### Plotting Businesses

- The API returns businesses within a geographic bounding box. Your map needs to fetch data as the user explores different areas.
- Think about when to trigger fetches, how to avoid excessive API calls, and what happens when the user moves the map rapidly.
- The API has a limit on how many businesses it returns. When the area is too dense, it returns a different response format — handle both cases appropriately.

### Marker Management

- With hundreds of markers on screen, you need a strategy for keeping the map usable. The Google Maps Utils library has relevant utilities.
- Businesses belong to category groups. There should be a visual way to distinguish categories on the map.

### Filtering

- Users need to control which types of businesses appear on the map. Design the filter interaction.
- The dataset includes chain businesses (Starbucks, McDonald's, etc.) and independent businesses. Some sales reps only care about independents — handle this.
- Filters should be instant (no API call) and persist across sessions.

### Interaction

- Tapping a marker should reveal key business info without navigating away from the map. Include enough info for the rep to decide whether to tap through to details.
- Provide a way to search by business name within the visible area.

---

## Phase 3: Business Details & Routes

**Goal**: Detailed business view and route planning workflow.

### Business Detail

When a user selects a business, show its full profile. The API response contains a rich set of fields — decide which ones matter most to a field sales rep and how to organize them.

Key considerations:
- Contact information should be actionable (a phone number you can't tap to call is useless)
- Operating hours should help the rep know if the business is open *right now*
- The data has quality/confidence indicators — surface these in a way that's useful without being overwhelming
- Users should be able to mark businesses they're interested in (or ones they want to hide)

### Business List

Some users prefer scanning a list over browsing a map. Provide an alternative view with appropriate sorting and pagination.

### Route Management

This is the core workflow for a field sales rep's day:

**Creating a route:**
- A rep picks several businesses they want to visit
- They should be able to add businesses from the map, from the detail screen, or from the list
- The route needs a name and date

**Optimizing a route:**
- The API has an optimization endpoint that reorders stops to minimize travel time
- Show the rep what changed — they want to see the value of optimization before accepting it

**Running a route:**
- Once a route is set, the rep needs a way to execute it: see the stops in order, navigate to each one, and track which ones they've visited
- Think about what this looks like on a phone mounted on a car dashboard

---

## Phase 4: Offline & Notifications

**Goal**: The app works in areas with poor connectivity, and proactively surfaces relevant information.

### Offline Support

Field reps drive through areas with spotty or no cell service. The app should degrade gracefully:

- Business data the user has already viewed should be available offline
- Routes should work offline (they were planned while online)
- The user should know when they're seeing cached data vs. live data
- When connectivity returns, things should sync up without manual intervention

Design a caching strategy. Consider how much data to cache, when to invalidate it, and how to partition it (the dataset is geographically distributed — that's a hint).

### Push Notifications

- Register the device for push notifications on login
- Handle different notification types (the API spec describes the registration endpoint — the notification *types* are up to you to design for a field sales use case)
- Notifications should deep-link to the relevant screen

### Background Work

Think about what should happen when the app is in the background. What data should be refreshed? Under what constraints (battery, network)?

---

## Phase 5: Polish

**Goal**: Production-quality finish.

### Testing

Write tests that give you confidence the app works. Focus on:
- Logic that's easy to get wrong (token refresh, filtering, cache invalidation)
- Data layer (does your caching actually work?)
- Critical user flows

We care more about test *quality* than test *count*.

### Error Handling

A production app doesn't show stack traces or blank screens. Handle:
- Network failures (with and without cached data)
- Empty states (no businesses, no routes, no search results)
- Permission denial (location)
- Unexpected API responses

### UI Quality

- Consistent theming (Material 3)
- Dark mode
- Loading states that don't feel janky
- Accessibility basics (content descriptions, touch targets)

### Bonus (if you're ahead)

Pick any of these if you want to show range:
- Compose-native Google Maps integration
- Business card scanning with ML Kit
- Home screen widget for today's route
- Anything else you think would make a field rep's life better

---

## What to Prioritize

If you're running short on time:

1. **Architecture and code quality** matter more than feature count
2. **Phase 1-2 done well** is a strong submission
3. **Phase 3 routes** is the most complex feature — partial credit for a good design even if not fully wired up
4. A `SUBMISSION.md` that explains your thinking is as important as the code
