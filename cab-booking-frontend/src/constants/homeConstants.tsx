// ============================================================
// constants/homeConstants.tsx
//
// WHY .tsx AND NOT .ts:
//   This file contains inline SVG JSX inside the PILLS array.
//   Any file with JSX syntax must use .tsx (TypeScript + JSX).
//   Files with zero JSX use .ts — like home.types.ts.
//
// WHY TYPES ARE IMPORTED, NOT DEFINED HERE:
//   Types live in types/home.types.ts because components like
//   IconPills.tsx and HeroSection.tsx also need these same types.
//   Defining them here would force components to import types
//   FROM a constants file — which is backwards and messy.
//
//   Flow: types/home.types.ts → here → components/
// ============================================================

// `import type` tells TypeScript these are type-only imports.
// They are completely erased at build time — zero runtime cost.

import type {
    NavLink,
    HeroContent,
    PillItem,
    DemoDriver,
    FareRow,
    DemoRoute,
    DemoEta,
    DecorativeDot,
} from '@/types/home.types';

// ─────────────────────────────────────────────
// NAVBAR
// ─────────────────────────────────────────────

/**
 * Navigation links shown in the top navbar.
 * To add/remove a link, only edit this array —
 * the Navbar component never needs to change.
 */
export const NAV_LINKS: NavLink[] = [
    "How It Works",
    "Pricing",
    "For Drivers",
    "About",
];

// ─────────────────────────────────────────────
// HERO SECTION — Left Column Text
// ─────────────────────────────────────────────

/**
 * All copy for the hero left column.
 * Centralising text here makes A/B testing and
 * future localisation straightforward — zero JSX changes needed.
 */
export const HERO_CONTENT: HeroContent = {
    badge:    "Now live in Metro Manila",
    headline: {
        line1:   "Your Ride,",
        accent1: "Anytime",     // rendered in blue by HeroSection
        accent2: "Anywhere",    // rendered in blue by HeroSection
        line2:   "You Go.",
    },
    subtext:
        "Book a trusted cab in seconds. Safe drivers, transparent pricing, and reliable service — every single ride.",
    ctaLabel: "Book a Ride",
};

// ─────────────────────────────────────────────
// ICON PILLS — Trust signals below the CTA
// ─────────────────────────────────────────────

/**
 * The 3 trust pills rendered below the CTA button.
 * icon is ReactNode — typed in PillItem inside home.types.ts.
 *
 * BEGINNER TIP: SVG inside a data array is perfectly valid JSX.
 * The array is defined once at module level — React does not
 * re-create these elements on every render.
 */
export const PILLS: PillItem[] = [
    {
        icon: (
            // Shield — represents safety
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                <path
                    d="M12 2L4 6v6c0 5.55 3.84 10.74 8 12 4.16-1.26 8-6.45 8-12V6L12 2z"
                    fill="#DBEAFE"
                    stroke="#2563EB"
                    strokeWidth="1.8"
                />
                <path
                    d="M9 12l2 2 4-4"
                    stroke="#2563EB"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                />
            </svg>
        ),
        label:  "Safe",
        sub:    "Verified drivers",
        accent: { bg: "#EFF6FF", border: "#BFDBFE", text: "#1D4ED8" },
    },
    {
        icon: (
            // Lightning bolt — represents speed
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                <path
                    d="M13 2L4.5 13.5H11L10 22L19.5 10.5H13L13 2Z"
                    fill="#FEF9C3"
                    stroke="#CA8A04"
                    strokeWidth="1.8"
                    strokeLinejoin="round"
                />
            </svg>
        ),
        label:  "Fast",
        sub:    "Avg. 4 min pickup",
        accent: { bg: "#FEFCE8", border: "#FDE68A", text: "#92400E" },
    },
    {
        icon: (
            // Peso circle — represents affordability
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
                <circle
                    cx="12" cy="12" r="9"
                    fill="#DCFCE7"
                    stroke="#16A34A"
                    strokeWidth="1.8"
                />
                <text
                    x="12" y="16.5"
                    textAnchor="middle"
                    fontFamily="'Plus Jakarta Sans', sans-serif"
                    fontSize="10"
                    fontWeight="800"
                    fill="#16A34A"
                >
                    ₱
                </text>
            </svg>
        ),
        label:  "Affordable",
        sub:    "Transparent fares",
        accent: { bg: "#F0FDF4", border: "#BBF7D0", text: "#15803D" },
    },
];

// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Driver Card
// ─────────────────────────────────────────────

/**
 * Placeholder driver data for the hero illustration.
 * The DEMO_ prefix signals: "replace me with real API data."
 * When the booking API is ready, this gets swapped for
 * a useQuery() call — the component shape stays the same.
 */
export const DEMO_DRIVER: DemoDriver = {
    name:    "Miguel R.",
    rating:  "4.97",
    vehicle: "Toyota Vios · ABC 1234",
    avatar:  "👤",    // → replace with <img src={driver.avatarUrl} /> later
    stars:   5,
};

// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Fare Card
// ─────────────────────────────────────────────

/**
 * Placeholder fare rows for the fare estimate card.
 * value is pre-formatted (₱40) so the component
 * renders it directly — no formatting logic needed there.
 */
export const DEMO_FARE_ROWS: FareRow[] = [
    { label: "Base fare",       value: "₱40" },
    { label: "Distance (8km)",  value: "₱32" },
    { label: "Booking fee",     value: "₱13" },
];

/**
 * Total fare — kept separate from rows because it
 * renders with different styling (larger, bolder, blue).
 */
export const DEMO_FARE_TOTAL = "₱85";


// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — Location Card
// ─────────────────────────────────────────────

/**
 * Placeholder pickup/dropoff for the route card.
 */
export const DEMO_ROUTE: DemoRoute = {
    pickup:  "Quezon City",
    dropoff: "Makati CBD",
};

// ─────────────────────────────────────────────
// FLOATING ILLUSTRATION — ETA Card
// ─────────────────────────────────────────────

/**
 * Placeholder ETA for the arrival countdown card.
 * unit is separate so it can be localised later.
 */
export const DEMO_ETA: DemoEta = {
    value: 4,
    unit:  "minutes",
};

/**
 * Positions and styles for the scattered background dots.
 * right is optional (see DecorativeDot in home.types.ts) —
 * TypeScript will warn if a component forgets to handle
 * the case where right is undefined.
 */
export const DECORATIVE_DOTS: DecorativeDot[] = [
    { top: "28%", left:  "14%",  size: 6, color: "#BFDBFE", delay: "0s"   },
    { top: "14%", left:  "66%",  size: 8, color: "#DBEAFE", delay: "0.4s" },
    { top: "54%", left:  "52%",  size: 5, color: "#93C5FD", delay: "0.8s" },
    { top: "72%", right: "14%",  size: 7, color: "#BFDBFE", delay: "0.2s" },
    { top: "82%", left:  "52%",  size: 5, color: "#DBEAFE", delay: "1s"   },
    { top: "20%", left:  "44%",  size: 4, color: "#93C5FD", delay: "0.6s" },
];