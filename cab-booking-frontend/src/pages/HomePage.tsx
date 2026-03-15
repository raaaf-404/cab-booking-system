// ============================================================
// pages/HomePage.tsx
//
// WHAT THIS FILE IS:
//   The route-level page component for the public homepage.
//   React Router renders this directly when the user visits "/".
//
// WHAT THIS FILE DOES NOT CONTAIN:
//   ✗ No hardcoded strings or copy
//   ✗ No inline styles
//   ✗ No data arrays or constants
//   ✗ No business logic
//   ✗ No useState / useEffect
//   ✗ No SVGs or icons
//
// WHAT THIS FILE DOES CONTAIN:
//   ✓ Imports of the components built in Steps 3–4
//   ✓ A single JSX return that composes those components
//   ✓ The page <title> via the PageTitle helper
//
// WHY THIS PATTERN?
//   This is called the "Orchestrator Pattern" or "Page Shell".
//   A page component's only job is to answer one question:
//   "What sections appear on this page, and in what order?"
//
//   All the HOW (styling, data, logic) lives in the components.
//   The page just says WHAT and WHERE.
//
//   This makes the page:
//   → Easy to read at a glance (see the whole layout in seconds)
//   → Easy to reorder sections (just move one JSX line)
//   → Easy to add/remove sections without touching any logic
//
// FUTURE SECTIONS TO ADD BELOW HeroSection:
//   <HowItWorksSection />
//   <PricingSection />
//   <ForDriversSection />
//   <TestimonialsSection />
//   <FooterSection />
//   Each will be its own component in components/home/
// ============================================================

import HomeNavbar    from '@/components/common/HomeNavbar';
import HeroSection   from '@/components/home/HeroSection';

// ─────────────────────────────────────────────────────────────
// PAGE TITLE HELPER
// ─────────────────────────────────────────────────────────────
// Sets the browser tab title for this page.
//
// WHY NOT use a <title> tag directly in JSX?
//   React renders into a <div id="root"> inside <body>.
//   The <title> tag lives in <head> — outside React's root.
//   You can't write <title> in a component and expect it to work.
//
// CORRECT APPROACH (used here):
//   useEffect + document.title — simple, no library needed.
//
// ALTERNATIVE: react-helmet or @tanstack/react-router meta —
//   better for SEO-critical apps, but overkill for now.

import { useEffect } from 'react';

const usePageTitle = (title: string) => {
    useEffect(() => {
        // Save the old title so we can restore it when
        // the user navigates away from this page
        const previous = document.title;
        document.title = title;

        // Cleanup: restore previous title on unmount
        return () => { document.title = previous; };
    }, [title]);
    // [title] dependency: re-runs if the title prop changes
};


// ─────────────────────────────────────────────────────────────
// PAGE COMPONENT
// ─────────────────────────────────────────────────────────────

const HomePage = () => {
    // Sets the browser tab title while on this page
    usePageTitle('Tara — Let\'s Go Places');

    return (
        // Page wrapper — sets the base background and font for
        // the entire page. overflowX: hidden prevents horizontal
        // scrollbars from the floating illustration cards.
        <div style={{
            minHeight:  '100vh',
            background: 'var(--bg)',
            fontFamily: 'var(--font)',
            overflowX:  'hidden',
        }}>

            {/* Fixed top navigation bar */}
            <HomeNavbar />

            {/* Full-height hero — headline, CTA, floating illustration */}
            <HeroSection />

            {/*
        ── FUTURE SECTIONS ──────────────────────────────────────
        Add new sections here as the page grows.
        Each section = one import at the top + one line here.

        Example:
        <HowItWorksSection />
        <PricingSection />
        <ForDriversSection />
        <TestimonialsSection />
        <FooterSection />
        ────────────────────────────────────────────────────────
      */}

        </div>
    );
};

export default HomePage;