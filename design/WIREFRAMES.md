# FieldFlow - Screen Reference

These are rough descriptions, not prescriptive layouts. Use your own judgment on UI design.

---

## Screens

### Login
Email + password form. Validation, loading state, error feedback. Standard stuff.

### Map (Home)
Full-screen Google Map. This is where users spend most of their time.
- Business markers on the map
- A way to filter what's shown
- A way to search by name
- Location button
- Tap a marker → see business info without leaving the map

### Business Detail
Full profile of a business. The API returns a lot of data — you decide what matters and how to organize it. A field sales rep cares about: "Can I contact them? Are they open? Should I visit?"

### Business List
Alternative to the map for browsing. Sortable, scrollable, paginated.

### Route List
Saved routes grouped by date. Create, view, delete.

### Route Builder
Pick businesses, order them, name the route, save.

### Route Execution
The "in the car" screen. Show the route on a map, list the stops, let the user navigate to each and mark them visited.

### Settings
User profile, preferences, manage starred/hidden businesses, logout.

---

## Design Notes

- Use Material 3 as your foundation
- Support dark mode
- Category groups have colors (from the API) — use them consistently across map markers, badges, and filters
- Touch targets: 48dp minimum (especially for the route execution screen — users will be driving)
- Empty states should be helpful, not just empty

---

## Not Specified (Your Call)

- Navigation pattern (bottom nav, drawer, tabs — whatever fits)
- How the filter UI works (bottom sheet, chips, sidebar, etc.)
- Whether business list is a separate tab or a toggle on the map screen
- How "add to route" works from different contexts
- How to display the optimization before/after comparison
- Map info window design
- Loading indicators (shimmer, skeleton, spinner, progress bar)
- Color palette beyond category colors
