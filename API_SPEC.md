# FieldFlow Mock API

**Base URL**: `http://localhost:3000/api`

Authenticated endpoints require: `Authorization: Bearer <access_token>`

## Response Envelope

All responses follow:
```json
{
  "status": <http_status_code>,
  "message": "...",
  "data": { ... } | null
}
```

---

## Authentication

### `POST /api/auth/login`

```json
// Request
{ "email": "demo@fieldflow.com", "password": "password123" }

// 200
{
  "data": {
    "access_token": "eyJ...",
    "refresh_token": "eyJ...",
    "token_type": "Bearer",
    "expires_in": 3600
  }
}
```

The access token is a standard JWT. Inspect it.

### `POST /api/auth/token/refresh`

```json
// Request
{ "refresh_token": "eyJ..." }

// 200 — same shape as login response
```

### `GET /api/user/profile` (authenticated)

Returns user info: id, email, first_name, last_name, company, title, territory.

---

## Businesses

### `GET /api/businesses/nearby` (authenticated)

Fetch businesses within a bounding box.

**Required query params:** `start_lat`, `start_long`, `end_lat`, `end_long`

**Optional:** `category_ids` (comma-separated category group or category IDs)

The response shape changes depending on how many results are in the area. The server has a threshold — below it you get individual business objects, above it you get something else. Handle both.

If the area is extremely large, the API will reject the request outright.

**Business object fields** (not exhaustive — explore the response):
- `leadbeam_id` — unique business identifier
- `name`, `lat`, `long`, `address_full`, `city`, `state`, `postal_code`
- `phone_primary`, `email`, `website`
- `category_primary`, `category_group`, `category_display`, `category_group_display`
- `is_chain`, `chain_name`
- `operating_status` — one of: `open`, `closed`, `temporarily_closed`
- `operating_hours` — keyed by day of week
- `rating`, `reviews_count`
- `overall_confidence`, `data_completeness`, `source_count`
- `social_profiles`, `enrichment_data`

### `GET /api/businesses/:leadbeam_id` (authenticated)

Full business detail. Same fields as above, plus additional firmographic data.

---

## Categories

### `GET /api/categories` (authenticated)

Returns the category taxonomy — groups containing categories. Each group has an `id`, `name`, `color`, and a list of `categories`.

---

## Routes

### `GET /api/routes` (authenticated)

List all routes. Returns summary data (no stops).

### `GET /api/routes/:id` (authenticated)

Full route detail including stops.

### `POST /api/routes` (authenticated)

Create a route. Required: `name`, `stops` array (min 2). Each stop needs at minimum `leadbeam_id`, `name`, `lat`, `long`.

Optional: `date` (defaults to today).

### `PATCH /api/routes/:id` (authenticated)

Update route metadata or stop states. Send only the fields you want to change.

### `PATCH /api/routes/:id/optimize` (authenticated)

Optimize stop order. Returns the reordered stops plus before/after distance and time comparison.

### `DELETE /api/routes/:id` (authenticated)

Delete a route.

---

## Device Registration

### `POST /api/devices/register` (authenticated)

Register for push notifications. Required: `device_id`, `token`. Optional: `platform`, `app_version`.

---

## Version Check

### `GET /api/version/check?platform=android&version=1.0.0`

No auth required. Returns whether the app version is still supported.

---

## Notes

- Credentials: `demo@fieldflow.com` / `password123`
- Business data is centered around Austin, TX
- Set `TOKEN_TTL_SECONDS=60` when starting the server to test token refresh: `TOKEN_TTL_SECONDS=60 npm start`
- The API behavior around edge cases (empty bounding box, overlapping coordinates, malformed input) is intentionally undocumented — handle what you encounter
