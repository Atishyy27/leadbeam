/**
 * FieldFlow Mock API Server
 *
 * Simulates the backend for the Android assessment.
 * Run: npm start (after npm run seed)
 */

const express = require('express');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(cors());
app.use(express.json());

// --- Configuration ---
const PORT = process.env.PORT || 3000;
const JWT_SECRET = 'fieldflow-mock-secret-key-do-not-use-in-production';
const TOKEN_TTL_SECONDS = parseInt(process.env.TOKEN_TTL_SECONDS || '3600');
const HEATMAP_THRESHOLD = 500;
const MAX_RESULTS = 10000;

// --- Load Data ---
let db;
try {
  db = JSON.parse(fs.readFileSync(path.join(__dirname, 'db.json'), 'utf8'));
} catch (e) {
  console.error('db.json not found. Run `npm run seed` first.');
  process.exit(1);
}

// In-memory route storage (persists for server lifetime)
let userRoutes = [...db.routes];
let registeredDevices = [];

// --- Helpers ---
function wrap(status, message, data) {
  return { status, message, data };
}

function generateToken(userId, email) {
  const now = Math.floor(Date.now() / 1000);
  return jwt.sign(
    { sub: userId, email, iat: now, exp: now + TOKEN_TTL_SECONDS },
    JWT_SECRET
  );
}

function authMiddleware(req, res, next) {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json(wrap(401, 'Authorization header required', null));
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    if (err.name === 'TokenExpiredError') {
      return res.status(401).json(wrap(401, 'Token expired', null));
    }
    return res.status(401).json(wrap(401, 'Invalid token', null));
  }
}

// --- Auth Routes ---

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json(wrap(400, 'Email and password are required', null));
  }

  // Accept demo credentials
  if (email === 'demo@fieldflow.com' && password === 'password123') {
    const accessToken = generateToken('user_001', email);
    const refreshToken = jwt.sign(
      { sub: 'user_001', email, type: 'refresh' },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.json(wrap(200, 'Login successful', {
      access_token: accessToken,
      refresh_token: refreshToken,
      token_type: 'Bearer',
      expires_in: TOKEN_TTL_SECONDS,
    }));
  }

  return res.status(401).json(wrap(401, 'Invalid email or password', null));
});

app.post('/api/auth/token/refresh', (req, res) => {
  const { refresh_token } = req.body;

  if (!refresh_token) {
    return res.status(400).json(wrap(400, 'Refresh token is required', null));
  }

  try {
    const decoded = jwt.verify(refresh_token, JWT_SECRET);
    const accessToken = generateToken(decoded.sub, decoded.email);
    const newRefreshToken = jwt.sign(
      { sub: decoded.sub, email: decoded.email, type: 'refresh' },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.json(wrap(200, 'Token refreshed', {
      access_token: accessToken,
      refresh_token: newRefreshToken,
      expires_in: TOKEN_TTL_SECONDS,
    }));
  } catch (err) {
    return res.status(401).json(wrap(401, 'Invalid or expired refresh token', null));
  }
});

// --- User Profile ---

app.get('/api/user/profile', authMiddleware, (req, res) => {
  res.json(wrap(200, 'Success', {
    id: req.user.sub,
    email: req.user.email,
    first_name: 'Alex',
    last_name: 'Rivera',
    company: 'Acme Sales Corp',
    title: 'Senior Field Rep',
    territory: 'Austin Metro',
    avatar_url: null,
    created_at: '2024-01-15T10:00:00Z',
  }));
});

// --- Businesses ---

app.get('/api/businesses/nearby', authMiddleware, (req, res) => {
  const startLat = parseFloat(req.query.start_lat);
  const startLong = parseFloat(req.query.start_long);
  const endLat = parseFloat(req.query.end_lat);
  const endLong = parseFloat(req.query.end_long);

  if (isNaN(startLat) || isNaN(startLong) || isNaN(endLat) || isNaN(endLong)) {
    return res.status(400).json(wrap(400, 'start_lat, start_long, end_lat, end_long are required', null));
  }

  const minLat = Math.min(startLat, endLat);
  const maxLat = Math.max(startLat, endLat);
  const minLng = Math.min(startLong, endLong);
  const maxLng = Math.max(startLong, endLong);

  // Filter by bounding box
  let results = db.businesses.filter(b =>
    b.lat >= minLat && b.lat <= maxLat &&
    b.long >= minLng && b.long <= maxLng
  );

  // Filter by categories if provided
  const categoryIds = req.query.category_ids;
  if (categoryIds) {
    const cats = categoryIds.split(',').map(c => c.trim());
    results = results.filter(b => cats.includes(b.category_group) || cats.includes(b.category_primary));
  }

  // Check limits
  if (results.length > MAX_RESULTS) {
    return res.status(400).json(
      wrap(400, `Too many businesses (${results.length}) in this area. Please zoom in to a smaller area.`, null)
    );
  }

  // Heatmap mode for large result sets
  if (results.length >= HEATMAP_THRESHOLD) {
    // Generate clustered heatmap points
    const gridSize = 0.005;
    const heatmapMap = {};

    for (const b of results) {
      const key = `${(Math.floor(b.lat / gridSize) * gridSize).toFixed(3)},${(Math.floor(b.long / gridSize) * gridSize).toFixed(3)}`;
      if (!heatmapMap[key]) {
        heatmapMap[key] = { lat: parseFloat((Math.floor(b.lat / gridSize) * gridSize + gridSize / 2).toFixed(4)), long: parseFloat((Math.floor(b.long / gridSize) * gridSize + gridSize / 2).toFixed(4)), weight: 0 };
      }
      heatmapMap[key].weight++;
    }

    return res.json(wrap(200, 'Success', {
      count: results.length,
      businesses: null,
      heatmap: Object.values(heatmapMap),
    }));
  }

  // Remove extended detail fields from list response
  const listResults = results.map(b => {
    const { employee_range, revenue_range, year_founded, ownership_type, vertical_tags, taxonomy, ...rest } = b;
    return rest;
  });

  res.json(wrap(200, 'Success', {
    count: listResults.length,
    businesses: listResults,
    heatmap: null,
  }));
});

app.get('/api/businesses/:leadbeam_id', authMiddleware, (req, res) => {
  const business = db.businesses.find(b => b.leadbeam_id === req.params.leadbeam_id);

  if (!business) {
    return res.status(404).json(
      wrap(404, `Business with leadbeam_id ${req.params.leadbeam_id} not found`, null)
    );
  }

  res.json(wrap(200, 'Success', business));
});

// --- Categories ---

app.get('/api/categories', authMiddleware, (req, res) => {
  res.json(wrap(200, 'Success', db.categories));
});

// --- Routes ---

app.get('/api/routes', authMiddleware, (req, res) => {
  // Return routes without stop details for list view
  const routeList = userRoutes.map(r => ({
    id: r.id,
    name: r.name,
    date: r.date,
    is_optimized: r.is_optimized,
    total_stops: r.total_stops,
    total_distance_km: r.total_distance_km,
    total_time_minutes: r.total_time_minutes,
    visited_stops: r.visited_stops,
    created_at: r.created_at,
    updated_at: r.updated_at,
  }));

  res.json(wrap(200, 'Success', { routes: routeList }));
});

app.get('/api/routes/:id', authMiddleware, (req, res) => {
  const route = userRoutes.find(r => r.id === req.params.id);
  if (!route) {
    return res.status(404).json(wrap(404, 'Route not found', null));
  }
  res.json(wrap(200, 'Success', route));
});

app.post('/api/routes', authMiddleware, (req, res) => {
  const { name, date, stops } = req.body;

  if (!name || !stops || stops.length < 2) {
    return res.status(400).json(wrap(400, 'Name and at least 2 stops are required', null));
  }

  const routeStops = stops.map((stop, idx) => ({
    order: idx + 1,
    leadbeam_id: stop.leadbeam_id,
    name: stop.name,
    lat: stop.lat,
    long: stop.long,
    address: stop.address || '',
    visited: false,
    distance_to_next_km: idx < stops.length - 1 ? parseFloat((Math.random() * 5 + 0.5).toFixed(1)) : null,
    time_to_next_minutes: idx < stops.length - 1 ? Math.floor(Math.random() * 15 + 3) : null,
  }));

  const totalDist = routeStops.reduce((s, st) => s + (st.distance_to_next_km || 0), 0);
  const totalTime = routeStops.reduce((s, st) => s + (st.time_to_next_minutes || 0), 0);

  const newRoute = {
    id: `route_${uuidv4().split('-')[0]}`,
    name,
    date: date || new Date().toISOString().split('T')[0],
    is_optimized: false,
    total_stops: routeStops.length,
    total_distance_km: parseFloat(totalDist.toFixed(1)),
    total_time_minutes: totalTime,
    visited_stops: 0,
    stops: routeStops,
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
  };

  userRoutes.push(newRoute);
  res.status(201).json(wrap(201, 'Route created', newRoute));
});

app.patch('/api/routes/:id/optimize', authMiddleware, (req, res) => {
  const route = userRoutes.find(r => r.id === req.params.id);
  if (!route) {
    return res.status(404).json(wrap(404, 'Route not found', null));
  }

  if (route.stops.length < 3) {
    return res.status(400).json(wrap(400, 'Need at least 3 stops to optimize', null));
  }

  const beforeDist = route.total_distance_km;
  const beforeTime = route.total_time_minutes;

  // Simulate optimization by shuffling stops and reducing distance
  const shuffled = [...route.stops].sort(() => Math.random() - 0.5);
  shuffled.forEach((stop, idx) => {
    stop.order = idx + 1;
    stop.distance_to_next_km = idx < shuffled.length - 1
      ? parseFloat((Math.random() * 3 + 0.3).toFixed(1))
      : null;
    stop.time_to_next_minutes = idx < shuffled.length - 1
      ? Math.floor(Math.random() * 10 + 2)
      : null;
  });

  const afterDist = parseFloat(shuffled.reduce((s, st) => s + (st.distance_to_next_km || 0), 0).toFixed(1));
  const afterTime = shuffled.reduce((s, st) => s + (st.time_to_next_minutes || 0), 0);

  // Ensure optimization always "improves" the route
  const finalDist = Math.min(afterDist, beforeDist * 0.7);
  const finalTime = Math.min(afterTime, Math.floor(beforeTime * 0.75));

  route.stops = shuffled;
  route.is_optimized = true;
  route.total_distance_km = finalDist;
  route.total_time_minutes = finalTime;
  route.updated_at = new Date().toISOString();

  const savings = parseFloat(((1 - finalDist / beforeDist) * 100).toFixed(1));

  res.json(wrap(200, 'Route optimized', {
    id: route.id,
    is_optimized: true,
    before: { total_distance_km: beforeDist, total_time_minutes: beforeTime },
    after: { total_distance_km: finalDist, total_time_minutes: finalTime },
    savings_percent: Math.max(savings, 10), // Always show some savings
    stops: route.stops,
  }));
});

app.patch('/api/routes/:id', authMiddleware, (req, res) => {
  const route = userRoutes.find(r => r.id === req.params.id);
  if (!route) {
    return res.status(404).json(wrap(404, 'Route not found', null));
  }

  // Update route name/date
  if (req.body.name) route.name = req.body.name;
  if (req.body.date) route.date = req.body.date;

  // Update stop visited status
  if (req.body.stops) {
    for (const update of req.body.stops) {
      const stop = route.stops.find(s => s.leadbeam_id === update.leadbeam_id);
      if (stop && update.visited !== undefined) {
        stop.visited = update.visited;
      }
    }
    route.visited_stops = route.stops.filter(s => s.visited).length;
  }

  route.updated_at = new Date().toISOString();
  res.json(wrap(200, 'Route updated', route));
});

app.delete('/api/routes/:id', authMiddleware, (req, res) => {
  const idx = userRoutes.findIndex(r => r.id === req.params.id);
  if (idx === -1) {
    return res.status(404).json(wrap(404, 'Route not found', null));
  }

  userRoutes.splice(idx, 1);
  res.json(wrap(200, 'Route deleted', null));
});

// --- Device Registration ---

app.post('/api/devices/register', authMiddleware, (req, res) => {
  const { device_id, token, platform, app_version } = req.body;

  if (!device_id || !token) {
    return res.status(400).json(wrap(400, 'device_id and token are required', null));
  }

  // Upsert device
  const existing = registeredDevices.findIndex(d => d.device_id === device_id);
  const device = {
    device_id,
    token,
    platform: platform || 'android',
    app_version: app_version || '1.0.0',
    user_id: req.user.sub,
    registered_at: new Date().toISOString(),
  };

  if (existing >= 0) {
    registeredDevices[existing] = device;
  } else {
    registeredDevices.push(device);
  }

  res.json(wrap(200, 'Device registered', {
    device_id,
    registered_at: device.registered_at,
  }));
});

// --- Version Check ---

app.get('/api/version/check', (req, res) => {
  res.json(wrap(200, 'Success', {
    update_required: false,
    latest_version: '1.0.0',
    minimum_version: '1.0.0',
    update_url: 'https://play.google.com/store/apps/details?id=com.fieldflow',
  }));
});

// --- Health Check ---

app.get('/api/health', (req, res) => {
  res.json({
    status: 'ok',
    businesses_count: db.businesses.length,
    categories_count: db.categories.groups.length,
    routes_count: userRoutes.length,
    token_ttl_seconds: TOKEN_TTL_SECONDS,
  });
});

// --- Start Server ---

app.listen(PORT, () => {
  console.log(`\nFieldFlow Mock API running on http://localhost:${PORT}`);
  console.log(`  Health:     http://localhost:${PORT}/api/health`);
  console.log(`  Login:      POST http://localhost:${PORT}/api/auth/login`);
  console.log(`  Businesses: GET  http://localhost:${PORT}/api/businesses/nearby`);
  console.log(`\n  Token TTL: ${TOKEN_TTL_SECONDS}s (set TOKEN_TTL_SECONDS env var to change)`);
  console.log(`  Credentials: demo@fieldflow.com / password123`);
  console.log(`  Businesses loaded: ${db.businesses.length}`);
  console.log(`  Heatmap threshold: ${HEATMAP_THRESHOLD} businesses\n`);
});
