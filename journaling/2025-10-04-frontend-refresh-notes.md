# Frontend Refresh Notes - 2025-10-04

## Snapshot
- Session overview visual remains monolithic (~700 lines) with custom canvas math and no actual animation support.
- Charts duplicate Chart.js setup and use mismatched color palettes, making iteration slow.
- Global styles still mirror the Vite starter (centered flex body, dark defaults) conflicting with Bootstrap layout.
- Navigation toggle relies on timers and placeholder glyphs, so the slide-out menu feels unfinished.

## Plan of Attack
1. Normalize global styling to play nicely with Bootstrap and introduce shared color tokens.
2. Replace placeholder navigation controls with real icons and smooth transitions.
3. Extract a reusable Chart.js shell so individual chart components only supply data/options.
4. Begin carving geometry helpers out of the session overview visual to prep for real animations.

## Progress Log
- [x] Reworked `style.css` to establish theme tokens and body layout.
- [x] Updated `App.vue` nav toggle to use icons and accessible interactions.
- [x] Added shared chart wrapper and applied it to initial charts.
- [ ] Extracted shot geometry helpers from `SessionOverviewVisual.vue`.

## Open Questions
- Which animation library (GSAP, d3 transitions, native canvas easing) best fits our deployment constraints?
- Do we want to support theming (light/dark) or lock into a single palette for now?
