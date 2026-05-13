# FieldFlow Mock API Server

A simple Express.js server that simulates the FieldFlow backend for the Android assessment.

## Prerequisites

- Node.js 18+ (or 16+)
- npm

## Setup

```bash
# Install dependencies
npm install

# Generate sample data (500 businesses around Austin, TX)
npm run seed

# Start the server
npm start
```

The server starts on `http://localhost:3000`.

## Connecting from Android Emulator

Use `http://10.0.2.2:3000/api/` as the base URL in your Android app when running on an emulator. This special IP maps to your host machine's `localhost`.

For a physical device on the same network, use your machine's local IP (e.g., `http://192.168.1.x:3000/api/`).

## Test Credentials

- **Email**: `demo@fieldflow.com`
- **Password**: `password123`

## Quick Test

```bash
# Health check
curl http://localhost:3000/api/health

# Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@fieldflow.com","password":"password123"}'

# Get nearby businesses (use the access_token from login)
curl "http://localhost:3000/api/businesses/nearby?start_lat=30.25&start_long=-97.78&end_lat=30.29&end_long=-97.72" \
  -H "Authorization: Bearer <access_token>"
```

## Configuration

| Environment Variable | Default | Description |
|---------------------|---------|-------------|
| `PORT` | 3000 | Server port |
| `TOKEN_TTL_SECONDS` | 3600 | JWT access token lifetime in seconds |

To test token refresh quickly:

```bash
TOKEN_TTL_SECONDS=60 npm start
```

This makes access tokens expire after 60 seconds, so you can verify your 80% proactive refresh logic works (should trigger refresh at ~48 seconds).

## Data

After running `npm run seed`, data files are generated in `data/`:

- `businesses.json` — 500 businesses around Austin, TX
- `categories.json` — 10 category groups with ~55 categories
- `routes.json` — 3 sample routes

Re-run `npm run seed` at any time to regenerate fresh data.

## API Endpoints

See [API_SPEC.md](../API_SPEC.md) for complete documentation.

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | No | Login |
| POST | `/api/auth/token/refresh` | No | Refresh token |
| GET | `/api/user/profile` | Yes | User profile |
| GET | `/api/businesses/nearby` | Yes | Nearby businesses (bounding box) |
| GET | `/api/businesses/:id` | Yes | Business detail |
| GET | `/api/categories` | Yes | Category taxonomy |
| GET | `/api/routes` | Yes | List routes |
| GET | `/api/routes/:id` | Yes | Route detail |
| POST | `/api/routes` | Yes | Create route |
| PATCH | `/api/routes/:id` | Yes | Update route |
| PATCH | `/api/routes/:id/optimize` | Yes | Optimize route |
| DELETE | `/api/routes/:id` | Yes | Delete route |
| POST | `/api/devices/register` | Yes | Register FCM device |
| GET | `/api/version/check` | No | App version check |
| GET | `/api/health` | No | Server health |
