# FieldFlow - Android Technical Assessment

## Overview

**FieldFlow** is a field sales productivity app that helps sales reps discover businesses in their territory, plan optimized visit routes, and manage their daily workflow — all from a map-first mobile experience.

You will build the Android version of this app. A mock API server is provided. The assignment is broken into **5 phases** of increasing complexity. Complete as many phases as you can.

> We evaluate depth over breadth. A polished, well-thought-out partial submission beats a rushed complete one.

---

## What You're Building

A mobile app for field sales reps who spend their days driving between businesses. Think of it as a specialized Google Maps meets lightweight CRM. The core user journey:

1. Rep logs in, opens a map of their territory
2. Sees nearby businesses plotted on the map, filtered by industry
3. Taps a business to see details — phone, hours, ratings, etc.
4. Adds interesting businesses to a visit route for the day
5. Optimizes the route order and navigates stop-by-stop
6. Works even when driving through areas with spotty cell service

How you design the UX, structure the code, and handle the edge cases is up to you.

---

## Repository Contents

```
android-screening-assignment/
|-- README.md                  # This file
|-- ASSIGNMENT.md              # Phase-by-phase requirements
|-- API_SPEC.md                # Mock API documentation
|-- mock-api/
|   |-- README.md              # How to run the mock server
|   |-- package.json
|   |-- server.js              # Express mock API server
|   |-- seed.js                # Database seeder (generates 500 businesses)
|   `-- data/                  # Generated sample data
`-- design/
    `-- WIREFRAMES.md          # Rough screen descriptions (not prescriptive)
```

---

## Tech Stack

| Requirement | |
|-------------|---|
| Kotlin | No Java |
| Min SDK 26 | Target 34+ |
| Hilt for DI | |
| Retrofit + OkHttp | |
| Room | |
| Google Maps SDK | |
| Coroutines + Flow | |
| Jetpack Compose or XML | Your call — justify in SUBMISSION.md |

For anything not listed here (navigation library, image loading, testing framework, build setup), use what you'd use on a real project and explain why.

---

## Getting Started

### 1. Set up the mock API

```bash
cd mock-api
npm install
npm run seed
npm start       # http://localhost:3000
```

### 2. Get a Google Maps API key

Enable "Maps SDK for Android" in Google Cloud Console and create a restricted API key.

### 3. Build your app

Point your base URL to `http://10.0.2.2:3000/api/` (emulator) or your machine's local IP.

---

## Submission

1. Push to a **private Git repo** and grant us read access
2. Include a `SUBMISSION.md` with:
   - Phases completed (and partial progress)
   - Architecture decisions and why you made them
   - What you'd do differently or add with more time
   - Screenshots or screen recording of the working app
   - Any setup steps beyond `./gradlew assembleDebug`
3. The project must build from a clean checkout

---

## Questions?

Document your assumption in `SUBMISSION.md` and move forward. We value engineers who can make reasonable calls with incomplete information.
