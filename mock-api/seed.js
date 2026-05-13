/**
 * FieldFlow Mock Data Seeder
 *
 * Generates realistic business data around Austin, TX.
 * Run: node seed.js
 */

const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

// --- Configuration ---
const BUSINESS_COUNT = 500;
const CENTER_LAT = 30.2672;
const CENTER_LNG = -97.7431;
const SPREAD_LAT = 0.12; // ~13km north-south
const SPREAD_LNG = 0.15; // ~13km east-west

// --- Category Taxonomy ---
const CATEGORY_GROUPS = [
  {
    id: 'food_and_drink', name: 'Food & Drink', color: '#FF6B35',
    categories: [
      { id: 'restaurant', name: 'Restaurant' },
      { id: 'coffee_shop', name: 'Coffee Shop' },
      { id: 'bar', name: 'Bar / Pub' },
      { id: 'bakery', name: 'Bakery' },
      { id: 'fast_food', name: 'Fast Food' },
      { id: 'ice_cream', name: 'Ice Cream / Frozen Yogurt' },
      { id: 'food_truck', name: 'Food Truck' },
    ]
  },
  {
    id: 'retail_and_shopping', name: 'Retail & Shopping', color: '#4A90D9',
    categories: [
      { id: 'clothing_store', name: 'Clothing Store' },
      { id: 'grocery', name: 'Grocery Store' },
      { id: 'convenience', name: 'Convenience Store' },
      { id: 'electronics', name: 'Electronics Store' },
      { id: 'furniture', name: 'Furniture Store' },
      { id: 'bookstore', name: 'Bookstore' },
      { id: 'pet_store', name: 'Pet Store' },
    ]
  },
  {
    id: 'health_and_medical', name: 'Health & Medical', color: '#2ECC71',
    categories: [
      { id: 'pharmacy', name: 'Pharmacy' },
      { id: 'dentist', name: 'Dentist' },
      { id: 'doctor', name: 'Doctor / Clinic' },
      { id: 'veterinarian', name: 'Veterinarian' },
      { id: 'optometrist', name: 'Optometrist' },
      { id: 'gym', name: 'Gym / Fitness Center' },
    ]
  },
  {
    id: 'automotive', name: 'Automotive', color: '#E74C3C',
    categories: [
      { id: 'auto_repair', name: 'Auto Repair' },
      { id: 'car_dealer', name: 'Car Dealer' },
      { id: 'gas_station', name: 'Gas Station' },
      { id: 'car_wash', name: 'Car Wash' },
      { id: 'tire_shop', name: 'Tire Shop' },
      { id: 'auto_parts', name: 'Auto Parts' },
    ]
  },
  {
    id: 'professional_services', name: 'Professional Services', color: '#9B59B6',
    categories: [
      { id: 'law_firm', name: 'Law Firm' },
      { id: 'accounting', name: 'Accounting / CPA' },
      { id: 'insurance', name: 'Insurance Agency' },
      { id: 'real_estate', name: 'Real Estate' },
      { id: 'marketing_agency', name: 'Marketing Agency' },
      { id: 'consulting', name: 'Consulting' },
    ]
  },
  {
    id: 'home_services', name: 'Home Services', color: '#F39C12',
    categories: [
      { id: 'plumber', name: 'Plumber' },
      { id: 'electrician', name: 'Electrician' },
      { id: 'hvac', name: 'HVAC' },
      { id: 'landscaping', name: 'Landscaping' },
      { id: 'cleaning', name: 'Cleaning Service' },
      { id: 'pest_control', name: 'Pest Control' },
    ]
  },
  {
    id: 'beauty_and_personal', name: 'Beauty & Personal Care', color: '#E91E90',
    categories: [
      { id: 'hair_salon', name: 'Hair Salon' },
      { id: 'nail_salon', name: 'Nail Salon' },
      { id: 'spa', name: 'Spa' },
      { id: 'barbershop', name: 'Barbershop' },
    ]
  },
  {
    id: 'education', name: 'Education', color: '#1ABC9C',
    categories: [
      { id: 'school', name: 'School' },
      { id: 'tutoring', name: 'Tutoring Center' },
      { id: 'daycare', name: 'Daycare / Childcare' },
      { id: 'driving_school', name: 'Driving School' },
    ]
  },
  {
    id: 'entertainment', name: 'Entertainment & Recreation', color: '#3498DB',
    categories: [
      { id: 'movie_theater', name: 'Movie Theater' },
      { id: 'bowling', name: 'Bowling Alley' },
      { id: 'escape_room', name: 'Escape Room' },
      { id: 'arcade', name: 'Arcade' },
    ]
  },
  {
    id: 'financial', name: 'Financial Services', color: '#27AE60',
    categories: [
      { id: 'bank', name: 'Bank' },
      { id: 'credit_union', name: 'Credit Union' },
      { id: 'tax_prep', name: 'Tax Preparation' },
      { id: 'financial_advisor', name: 'Financial Advisor' },
    ]
  },
];

// --- Name Generation Pools ---
const FIRST_NAMES = [
  'Austin', 'Lone Star', 'Capital City', 'Barton', 'Congress', 'Lamar', 'South',
  'North', 'East', 'West', 'Hill Country', 'Lake', 'River', 'Oak', 'Cedar',
  'Pecan', 'Magnolia', 'Bluebonnet', 'Sunrise', 'Golden', 'Summit', 'Heritage',
  'Pioneer', 'Texas', 'Longhorn', 'Maverick', 'Frontier', 'Prairie', 'Canyon',
  'Mesa', 'Valley', 'Ridge', 'Crest', 'Grove', 'Meadow', 'Brook', 'Spring',
  'Creek', 'Stone', 'Iron', 'Silver', 'Copper', 'Elm', 'Maple', 'Pine', 'Willow',
];

const LAST_NAMES = [
  'Johnson', 'Smith', 'Williams', 'Brown', 'Davis', 'Miller', 'Wilson', 'Moore',
  'Taylor', 'Anderson', 'Thomas', 'Jackson', 'White', 'Harris', 'Martin', 'Garcia',
  'Clark', 'Lewis', 'Lee', 'Walker', 'Hall', 'Allen', 'Young', 'King', 'Wright',
  'Scott', 'Green', 'Baker', 'Adams', 'Nelson', 'Mitchell', 'Roberts', 'Carter',
];

const CHAIN_NAMES = {
  coffee_shop: ['Starbucks', 'Dunkin\'', 'Peet\'s Coffee', 'Dutch Bros'],
  fast_food: ['McDonald\'s', 'Chick-fil-A', 'Wendy\'s', 'Taco Bell', 'Whataburger', 'Subway'],
  gas_station: ['Shell', 'Chevron', 'Exxon', 'BP', 'Buc-ee\'s'],
  pharmacy: ['CVS Pharmacy', 'Walgreens', 'Rite Aid'],
  grocery: ['H-E-B', 'Whole Foods', 'Trader Joe\'s', 'Kroger'],
  bank: ['Chase Bank', 'Bank of America', 'Wells Fargo', 'Capital One'],
  convenience: ['7-Eleven', 'Circle K'],
  gym: ['Planet Fitness', 'LA Fitness', '24 Hour Fitness', 'Anytime Fitness'],
  auto_parts: ['AutoZone', 'O\'Reilly Auto Parts', 'NAPA Auto Parts'],
  car_wash: ['Mister Car Wash', 'Take 5 Car Wash'],
};

const STREETS = [
  'Congress Ave', 'Guadalupe St', 'Lamar Blvd', 'Burnet Rd', 'South 1st St',
  'East 6th St', 'West 5th St', 'Red River St', 'Rainey St', 'Manor Rd',
  'Airport Blvd', 'Oltorf St', 'Riverside Dr', 'Cesar Chavez St', 'MLK Jr Blvd',
  'Anderson Ln', 'Parmer Ln', 'Slaughter Ln', 'Manchaca Rd', 'Metric Blvd',
  'Research Blvd', 'Steck Ave', 'Braker Ln', 'Rundberg Ln', 'Cameron Rd',
  'Dean Keeton St', 'Duval St', 'Speedway', 'San Jacinto Blvd', 'Trinity St',
  'Brazos St', 'Colorado St', 'Lavaca St', 'Nueces St', 'Rio Grande St',
  'West Ave', 'Shoal Creek Blvd', 'Bull Creek Rd', 'Spicewood Springs Rd',
  'Far West Blvd', 'Mopac Expy', 'Ben White Blvd', 'William Cannon Dr',
];

const SOCIAL_PLATFORMS = ['facebook', 'instagram', 'twitter', 'yelp', 'linkedin'];

// --- Helper Functions ---
function randomFloat(min, max) {
  return min + Math.random() * (max - min);
}

function randomInt(min, max) {
  return Math.floor(randomFloat(min, max + 1));
}

function pick(arr) {
  return arr[randomInt(0, arr.length - 1)];
}

function pickN(arr, n) {
  const shuffled = [...arr].sort(() => Math.random() - 0.5);
  return shuffled.slice(0, n);
}

function generateULID() {
  // Simplified ULID-like ID (26 chars, alphanumeric)
  const chars = '0123456789ABCDEFGHJKMNPQRSTVWXYZ';
  const time = Date.now().toString(32).toUpperCase().padStart(10, '0');
  let random = '';
  for (let i = 0; i < 16; i++) {
    random += chars[randomInt(0, chars.length - 1)];
  }
  return time + random;
}

function generateGeohash(lat, lng) {
  // Simplified geohash (8 chars)
  const base32 = '0123456789bcdefghjkmnpqrstuvwxyz';
  let hash = '';
  let minLat = -90, maxLat = 90, minLng = -180, maxLng = 180;
  let isLng = true;
  for (let i = 0; i < 40; i++) {
    if (isLng) {
      const mid = (minLng + maxLng) / 2;
      if (lng > mid) { minLng = mid; } else { maxLng = mid; }
    } else {
      const mid = (minLat + maxLat) / 2;
      if (lat > mid) { minLat = mid; } else { maxLat = mid; }
    }
    isLng = !isLng;
    if ((i + 1) % 5 === 0) {
      let bits = 0;
      // simplified: just pick a char
      hash += base32[randomInt(0, 31)];
    }
  }
  return '9v6k' + hash.substring(0, 4); // Austin area prefix
}

function normalizeBusinessName(name) {
  return name.toLowerCase()
    .replace(/[^a-z0-9\s]/g, '')
    .replace(/\s+/g, ' ')
    .trim();
}

function generateHours() {
  const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];
  const hours = {};
  const openTime = pick(['06:00', '07:00', '08:00', '09:00', '10:00']);
  const closeTime = pick(['17:00', '18:00', '19:00', '20:00', '21:00', '22:00']);

  for (const day of days) {
    if (day === 'sunday' && Math.random() < 0.3) {
      hours[day] = { open: 'closed', close: 'closed' };
    } else {
      hours[day] = { open: openTime, close: closeTime };
    }
  }
  return hours;
}

function generateBusinessName(category, isChain) {
  if (isChain && CHAIN_NAMES[category.id]) {
    return pick(CHAIN_NAMES[category.id]);
  }

  const patterns = [
    () => `${pick(FIRST_NAMES)} ${category.name}`,
    () => `${pick(LAST_NAMES)}'s ${category.name}`,
    () => `The ${pick(FIRST_NAMES)} ${category.name}`,
    () => `${pick(FIRST_NAMES)} ${pick(LAST_NAMES)} ${category.name}`,
    () => `${pick(FIRST_NAMES)} & ${pick(FIRST_NAMES)} ${category.name}`,
  ];

  return pick(patterns)();
}

function generateSocialProfiles(name) {
  const profiles = {};
  const slug = name.toLowerCase().replace(/[^a-z0-9]/g, '').substring(0, 20);
  const count = randomInt(0, 3);
  const platforms = pickN(SOCIAL_PLATFORMS, count);

  for (const platform of platforms) {
    if (platform === 'facebook') profiles.facebook = `https://facebook.com/${slug}`;
    if (platform === 'instagram') profiles.instagram = `https://instagram.com/${slug}`;
    if (platform === 'twitter') profiles.twitter = `https://x.com/${slug}`;
    if (platform === 'yelp') profiles.yelp = `https://yelp.com/biz/${slug}`;
    if (platform === 'linkedin') profiles.linkedin = `https://linkedin.com/company/${slug}`;
  }
  return profiles;
}

// --- Generate Businesses ---
function generateBusinesses() {
  const businesses = [];

  for (let i = 0; i < BUSINESS_COUNT; i++) {
    const group = pick(CATEGORY_GROUPS);
    const category = pick(group.categories);
    const isChain = CHAIN_NAMES[category.id] ? Math.random() < 0.25 : false;
    const name = generateBusinessName(category, isChain);
    const lat = CENTER_LAT + randomFloat(-SPREAD_LAT, SPREAD_LAT);
    const lng = CENTER_LNG + randomFloat(-SPREAD_LNG, SPREAD_LNG);
    const streetNum = randomInt(100, 9999);
    const street = pick(STREETS);
    const hasRating = Math.random() < 0.75;
    const hasPhone = Math.random() < 0.85;
    const hasEmail = Math.random() < 0.55;
    const hasWebsite = Math.random() < 0.65;
    const hasHours = Math.random() < 0.7;

    const chainName = isChain ? name.split(' ')[0] : '';
    const slug = name.toLowerCase().replace(/[^a-z0-9]/g, '');

    const business = {
      leadbeam_id: generateULID(),
      name: name,
      name_normalized: normalizeBusinessName(name),
      lat: parseFloat(lat.toFixed(6)),
      long: parseFloat(lng.toFixed(6)),
      geohash: generateGeohash(lat, lng),
      address_full: `${streetNum} ${street}, Austin, TX ${randomInt(78701, 78799)}`,
      street_address: `${streetNum} ${street}`,
      city: 'Austin',
      state: 'Texas',
      postal_code: `${randomInt(78701, 78799)}`,
      country: 'US',
      phone_primary: hasPhone ? `+1 (512) ${randomInt(200, 999)}-${String(randomInt(0, 9999)).padStart(4, '0')}` : '',
      email: hasEmail ? `info@${slug.substring(0, 15)}.com` : '',
      website: hasWebsite ? `https://${slug.substring(0, 15)}.com` : '',
      category_primary: category.id,
      category_group: group.id,
      category_display: category.name,
      category_group_display: group.name,
      is_chain: isChain,
      chain_name: chainName,
      operating_status: Math.random() < 0.92 ? 'open' : (Math.random() < 0.5 ? 'temporarily_closed' : 'closed'),
      operating_hours: hasHours ? generateHours() : {},
      rating: hasRating ? parseFloat((randomFloat(2.5, 5.0)).toFixed(1)) : null,
      reviews_count: hasRating ? randomInt(5, 2000) : null,
      overall_confidence: parseFloat(randomFloat(0.3, 0.98).toFixed(2)),
      data_completeness: parseFloat(randomFloat(0.4, 1.0).toFixed(2)),
      source_count: randomInt(1, 5),
      last_enriched_at: new Date(Date.now() - randomInt(0, 90 * 24 * 60 * 60 * 1000)).toISOString(),
      social_profiles: Math.random() < 0.4 ? generateSocialProfiles(name) : {},
      brand: isChain ? { names: { primary: chainName, common: chainName } } : {},
      enrichment_data: {
        verified: Math.random() < 0.6,
        business_status: Math.random() < 0.9 ? 'OPERATIONAL' : 'CLOSED_TEMPORARILY',
      },
      // Extended detail fields
      employee_range: pick(['1-10', '11-50', '51-200', '201-500', '']),
      revenue_range: pick(['<$100K', '$100K-$500K', '$500K-$1M', '$1M-$5M', '$5M+', '']),
      year_founded: Math.random() < 0.5 ? randomInt(1970, 2024) : null,
      ownership_type: isChain ? pick(['franchise', 'corporate']) : pick(['independent', 'partnership', '']),
      vertical_tags: pickN([category.id, group.id.replace('_and_', '_'), 'local', 'established', 'popular', 'wifi', 'parking', 'delivery'], randomInt(0, 3)),
      taxonomy: {
        primary: category.id,
        group: group.id,
        alternates: [],
      },
    };

    businesses.push(business);
  }

  return businesses;
}

// --- Generate Sample Routes ---
function generateRoutes(businesses) {
  const routeNames = [
    'Downtown Austin Loop', 'South Congress Run', 'East Side Circuit',
    'North Austin Route', 'Campus Area Visit', 'Barton Springs Route',
  ];

  return routeNames.slice(0, 3).map((name, idx) => {
    const stops = pickN(businesses, randomInt(3, 7)).map((biz, order) => ({
      order: order + 1,
      leadbeam_id: biz.leadbeam_id,
      name: biz.name,
      lat: biz.lat,
      long: biz.long,
      address: biz.address_full,
      visited: Math.random() < 0.3,
      distance_to_next_km: order < 6 ? parseFloat(randomFloat(0.5, 5.0).toFixed(1)) : null,
      time_to_next_minutes: order < 6 ? randomInt(3, 20) : null,
    }));

    const totalDist = stops.reduce((s, st) => s + (st.distance_to_next_km || 0), 0);
    const totalTime = stops.reduce((s, st) => s + (st.time_to_next_minutes || 0), 0);

    return {
      id: `route_${String(idx + 1).padStart(3, '0')}`,
      name: name,
      date: new Date(Date.now() + (idx - 1) * 86400000).toISOString().split('T')[0],
      is_optimized: idx === 0,
      total_stops: stops.length,
      total_distance_km: parseFloat(totalDist.toFixed(1)),
      total_time_minutes: totalTime,
      visited_stops: stops.filter(s => s.visited).length,
      stops: stops,
      created_at: new Date(Date.now() - idx * 86400000).toISOString(),
      updated_at: new Date(Date.now() - idx * 43200000).toISOString(),
    };
  });
}

// --- Write Output ---
const businesses = generateBusinesses();
const routes = generateRoutes(businesses);

// Write individual data files
fs.writeFileSync(
  path.join(__dirname, 'data', 'businesses.json'),
  JSON.stringify(businesses, null, 2)
);

fs.writeFileSync(
  path.join(__dirname, 'data', 'categories.json'),
  JSON.stringify({ groups: CATEGORY_GROUPS }, null, 2)
);

fs.writeFileSync(
  path.join(__dirname, 'data', 'routes.json'),
  JSON.stringify(routes, null, 2)
);

// Write combined db.json for the server
fs.writeFileSync(
  path.join(__dirname, 'db.json'),
  JSON.stringify({ businesses, routes, categories: { groups: CATEGORY_GROUPS } }, null, 2)
);

console.log(`Generated ${businesses.length} businesses`);
console.log(`Generated ${routes.length} sample routes`);
console.log(`Category groups: ${CATEGORY_GROUPS.length} with ${CATEGORY_GROUPS.reduce((s, g) => s + g.categories.length, 0)} categories`);
console.log('\nFiles written:');
console.log('  data/businesses.json');
console.log('  data/categories.json');
console.log('  data/routes.json');
console.log('  db.json');
console.log('\nRun `npm start` to start the mock API server.');
