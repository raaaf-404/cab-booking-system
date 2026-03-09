// ============================================================
// components/home/IconPills.tsx
//
// WHAT THIS COMPONENT DOES:
//   Renders the 3 trust signal pills below the CTA button.
//   (Safe · Fast · Affordable)
//
// WHAT THIS COMPONENT DOES NOT DO:
//   - It does NOT define what the pills contain (that's in
//     homeConstants.tsx)
//   - It does NOT know about routing or auth state
//   - It only knows HOW to render a pill given the data
//
// PATTERN USED: "Dumb component" / Presentational component
//   This component receives data via props and renders it.
//   Zero business logic lives here — only rendering logic.
//
// WHY A SEPARATE COMPONENT FOR JUST 3 PILLS?
//   1. HeroSection.tsx stays clean and readable
//   2. Pills can be reused on other pages (e.g. /pricing)
//   3. Easy to test in isolation
//   4. If the pill design changes, you only edit ONE file
// ============================================================

import type { PillItem } from '@/types/home.types';
import { PILLS } from '@/constants/homeConstants';
import React from 'react';

// ─────────────────────────────────────────────
// PROP TYPES
// ─────────────────────────────────────────────

interface IconPillsProps {
    /**
     * The array of pills to render.
     * Defaults to the PILLS constant from homeConstants.tsx
     * if not provided — so the component works out of the box,
     * but can also be overridden with custom pills if needed.
     *
     * BEGINNER TIP: Providing a default value for props is
     * called a "default prop pattern". It makes components
     * flexible without requiring the parent to always pass data.
     */
    pills?: PillItem[];
}

// ─────────────────────────────────────────────
// SUB-COMPONENT: Single Pill
// ─────────────────────────────────────────────

/**
 * Renders a single pill item.
 *
 * WHY A SUB-COMPONENT?
 * We split the individual pill into its own function so that:
 * - The hover handler logic lives in one place
 * - IconPills stays a clean flat map() call
 * - Each pill is independently readable and testable
 *
 * It is NOT exported because nothing outside this file
 * needs to render a single pill — only the full set.
 */
interface SinglePillProps {
    pill: PillItem;
}

const SinglePill = ({ pill }: SinglePillProps) => {

    // Hover handlers — we use onMouseEnter/Leave instead of
    // CSS :hover because our styles are inline objects.
    // In a Tailwind project you would use hover: classes instead.
    const handleMouseEnter = (e: React.MouseEvent<HTMLDivElement>) => {
        e.currentTarget.style.transform  = "translateY(-2px)";
        e.currentTarget.style.boxShadow  = "0 4px 14px rgba(0,0,0,0.07)";
    };

    const handleMouseLeave = (e: React.MouseEvent<HTMLDivElement>) => {
        e.currentTarget.style.transform  = "translateY(0)";
        e.currentTarget.style.boxShadow  = "none";
    };

    return (
        <div
            style={{
                display:        "flex",
                alignItems:     "center",
                gap:            9,
                // Each pill uses its own accent color system
                // All 3 colors come from pill.accent — never hardcoded
                background:     pill.accent.bg,
                border:         `1.5px solid ${pill.accent.border }`,
                borderRadius:   100,                   // full pill shape
                padding:        "8px 16px 8px 10px",
                transition:     "transform 0.18s, box-shadow 0.18s",
                cursor:         "default",
            }}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >

            {/* ── Icon circle ──
          White circle behind the icon gives it lift
          against the colored pill background           */}
            <div style={{
                width:           26,
                height:          26,
                borderRadius:    "50%",
                background:      "white",
                display:         "flex",
                alignItems:      "center",
                justifyContent:  "center",
                flexShrink:      0,                    // never squish the icon
                boxShadow:       "0 1px 3px rgba(0,0,0,0.06)",
            }}>
                {pill.icon}
            </div>

            {/* ── Label + sub ──
          Two lines stacked: bold label on top,
          muted subtext below                           */}
            <div>
                <div style={{
                    fontSize:    13,
                    fontWeight:  700,
                    // Text color comes from the pill's accent system
                    color:       pill.accent.text,
                    lineHeight:  1.2,
                }}>
                    {pill.label}
                </div>
                <div style={{
                    fontSize:    11,
                    fontWeight:  500,
                    // Subtext always uses the global muted color —
                    // it doesn't change per pill
                    color:       "var(--text-muted)",
                    lineHeight:  1.2,
                }}>
                    {pill.sub}
                </div>
            </div>

        </div>
    );
};

// ─────────────────────────────────────────────
// MAIN COMPONENT
// ─────────────────────────────────────────────

/**
 * Renders the row of 3 trust signal pills.
 * Uses the PILLS constant by default — can be overridden via props.
 *
 * Usage:
 *   <IconPills />                     // uses default PILLS data
 *   <IconPills pills={customPills} /> // override with custom data
 */
const IconPills = ({ pills = PILLS }: IconPillsProps) => {
    return (
        // au + d5 = CSS animation classes defined in global.css
        // au  → fadeUp entrance animation
        // d5  → 0.58s delay (5th element to animate in)
        <div
            className="au d5"
            style={{
                display:   "flex",
                gap:       10,
                flexWrap:  "wrap",   // wraps to next line on small screens
            }}
        >
            {pills.map((pill, index) => (
                // BEGINNER TIP: Always use a stable key in .map().
                // Here we use index because the pills array never
                // reorders at runtime. If your list could reorder,
                // use a unique id from the data instead.
                <SinglePill key={index} pill={pill} />
            ))}
        </div>
    );
};

export default IconPills;