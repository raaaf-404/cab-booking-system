// ============================================================
// types/home.types.ts
//
// WHY THIS FILE EXISTS:
//   Shared TypeScript types for the Homepage feature.
//   Types live here — NOT inside constants or components —
//   because multiple files need the same type definitions.
//
//   Flow rule: types/ → constants/ → components/
//   A constants file can import from types/.
//   A component can import from types/.
//   But NEITHER should define types the other one needs.
//
// NOTE: This is a .ts file (not .tsx) because it contains
//       zero JSX — only type definitions. Pure TypeScript.
// ============================================================

import type { ReactNode } from 'react';

// ─────────────────────────────────────────────
// NAVBAR
// ─────────────────────────────────────────────

/**
 * A single navigation link label.
 * Simple string type — but named so it's self-documenting
 * and easy to extend later (e.g. add a href field).
 */
export type NavLink = string;


// ─────────────────────────────────────────────
// HERO SECTION
// ─────────────────────────────────────────────

/**
 * All text content for the hero left column.
 * Structured as an object so every piece of copy
 * is named and easy to find.
 */
export interface HeroHeadline {
    line1:   string;  // "Your Ride,"
    accent1: string;  // "Anytime"   — rendered in blue
    accent2: string;  // "Anywhere"  — rendered in blue
    line2:   string;  // "You Go."
}

export interface HeroContent {
    badge:    string;
    headline: HeroHeadline;
    subtext:  string;
    ctaLabel: string;
}


// ─────────────────────────────────────────────
// ICON PILLS
// ─────────────────────────────────────────────

/**
 * Color accent system for a single pill.
 * All three colors are required — the component
 * applies them to background, border, and text.
 */
export interface PillAccent {
    bg:     string;   // pill background color
    border: string;   // pill border color
    text:   string;   // pill label text color
}

/**
 * A single trust pill (Safe / Fast / Affordable).
 *
 * icon is typed as ReactNode — the correct type for any
 * renderable JSX element (svg, img, component, etc.).
 * Never type icons as ReactElement — ReactNode is broader
 * and handles null/undefined gracefully too.
 */
export interface PillItem {
    icon:   ReactNode;    // inline SVG or any renderable element
    label:  string;       // bold pill text   e.g. "Safe"
    sub:    string;       // muted subtext    e.g. "Verified drivers"
    accent: PillAccent;
}


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Driver Card
// ─────────────────────────────────────────────

/**
 * Demo driver data for the hero illustration card.
 * When real API data is wired up, this same shape
 * will be returned from the backend — so naming matters.
 */
export interface DemoDriver {
    name:    string;   // "Miguel R."
    rating:  string;   // "4.97" — string so we control formatting
    vehicle: string;   // "Toyota Vios · ABC 1234"
    avatar:  string;   // emoji placeholder → will be image URL later
    stars:   number;   // how many star icons to render (1–5)
}


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Fare Card
// ─────────────────────────────────────────────

/**
 * A single row in the fare breakdown.
 * value is a pre-formatted string (e.g. "₱40")
 * so the component doesn't need any formatting logic.
 */
export interface FareRow {
    label: string;   // "Base fare"
    value: string;   // "₱40"
}


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Route Card
// ─────────────────────────────────────────────

/**
 * Pickup and drop off location names for the route card.
 */
export interface DemoRoute {
    pickup:  string;  // "Quezon City"
    dropoff: string;  // "Makati CBD"
}


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — ETA Card
// ─────────────────────────────────────────────

/**
 * ETA data for the arrival countdown card.
 * value and unit are separated so the unit can
 * be localised independently (e.g. "minuto" in Filipino).
 */
export interface DemoEta {
    value: number;   // 4
    unit:  string;   // "minutes"
}


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Decorative Dots
// ─────────────────────────────────────────────

/**
 * A single decorative dot scattered in the background.
 *
 * Position uses CSS strings (e.g. "28%") because they're
 * passed directly to inline style — no conversion needed.
 *
 * right is optional because most dots use left positioning.
 * TypeScript forces us to handle the undefined case in the component.
 */
export interface DecorativeDot {
    top:    string;            // CSS top value    e.g. "28%"
    left?:  string;            // CSS left value   e.g. "14%"
    right?: string;            // CSS right value  e.g. "14%" (optional)
    size:   number;            // diameter in px
    color:  string;            // hex color string
    delay:  string;            // CSS animation-delay e.g. "0.4s"
}