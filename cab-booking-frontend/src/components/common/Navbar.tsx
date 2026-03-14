// ============================================================
// components/home/HomeNavbar.tsx
//
// WHAT THIS COMPONENT DOES:
//   The glassmorphism marketing navbar for the Homepage.
//   Handles its own scroll state and shows Login/Sign Up
//   links for unauthenticated users.
//
// WHY THIS IS SEPARATE FROM components/layout/Navbar.tsx:
//   The app has TWO navbars that serve different purposes:
//
//   components/layout/Navbar.tsx   → App navbar (authenticated users,
//                                    shows user info + logout button,
//                                    used inside MainLayout)
//
//   components/home/HomeNavbar.tsx → Marketing navbar (public landing
//                                    page, glassmorphism design,
//                                    shows Login + Sign Up buttons)
//
//   Having two navbars for two contexts is correct and normal.
//   Do NOT merge them — they will diverge further as the app grows.
//
// SCROLL BEHAVIOUR:
//   Manages its own scroll state with useEffect.
//   The parent (HomePage) does NOT need to know about scroll —
//   this is an internal concern of the navbar only.
//
// STEP 4 NOTE:
//   TaraLogo is defined inline here for now.
//   It will be extracted to components/icons/ in Step 4.
// ============================================================

import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { NAV_LINKS } from '@/constants/homeConstants';

// ─────────────────────────────────────────────
// TARA LOGO
// ─────────────────────────────────────────────
// TODO: Step 4 — move this to components/icons/TaraLogo.tsx
// and import it from there.

const TaraLogo = () => (
    <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 280 72"
        height="44"
        style={{ width: 'auto', display: 'block' }}
        aria-label="Tara — Let's Go Places"
        role="img"
    >
        <defs>
            <linearGradient id="pinGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%"   stopColor="#2563EB" />
                <stop offset="100%" stopColor="#3B82F6" />
            </linearGradient>
            <filter id="pinShadow" x="-20%" y="-20%" width="140%" height="140%">
                <feDropShadow
                    dx="0" dy="2" stdDeviation="2.5"
                    floodColor="#2563EB" floodOpacity="0.2"
                />
            </filter>
        </defs>
        <g transform="translate(0,2) scale(0.52)" filter="url(#pinShadow)">
            <path
                d="M45 0 C20.1 0 0 20.1 0 45 C0 75 45 100 45 100 C45 100 90 75 90 45 C90 20.1 69.9 0 45 0 Z"
                fill="url(#pinGrad)"
            />
            <path d="M25 28 H65 V40 H51 V65 H39 V40 H25 Z" fill="#FFFFFF" />
        </g>
        <text
            x="60" y="44"
            fontFamily="'Plus Jakarta Sans', sans-serif"
            fontSize="34" fontWeight="800" fill="#0F172A" letterSpacing="-1"
        >
            Tara
        </text>
        <text
            x="62" y="60"
            fontFamily="'Plus Jakarta Sans', sans-serif"
            fontSize="7.5" fontWeight="600" fill="#94A3B8" letterSpacing="3"
        >
            LET'S GO PLACES
        </text>
    </svg>
);

// ─────────────────────────────────────────────
// SUB-COMPONENTS: Buttons
// ─────────────────────────────────────────────

/**
 * Outlined Login button.
 * Uses React Router <Link> — more semantic than a button
 * for navigation. The `to` prop makes it easy to change
 * the destination later without touching the style.
 */
const LoginButton = () => (
    <Link
        to="/login"
        style={{
            background:   'none',
            border:       '1.5px solid var(--border)',
            borderRadius: 10,
            padding:      '8px 20px',
            fontFamily:   'var(--font)',
            fontWeight:   600,
            fontSize:     14,
            color:        'var(--text)',
            cursor:       'pointer',
            transition:   'border-color 0.2s, background 0.2s',
            textDecoration: 'none',
            display:      'inline-block',
        }}
        onMouseEnter={e => {
            (e.currentTarget as HTMLAnchorElement).style.borderColor = '#2563EB';
            (e.currentTarget as HTMLAnchorElement).style.background  = 'var(--blue-light)';
        }}
        onMouseLeave={e => {
            (e.currentTarget as HTMLAnchorElement).style.borderColor = 'var(--border)';
            (e.currentTarget as HTMLAnchorElement).style.background  = 'none';
        }}
    >
        Login
    </Link>
);

/**
 * Filled Sign Up button.
 * Also a <Link> — navigates to /signup.
 * Heavier visual weight than Login to guide the user's eye.
 */
const SignUpButton = () => (
    <Link
        to="/signup"
        style={{
            background:     'var(--blue)',
            border:         'none',
            borderRadius:   10,
            padding:        '8px 22px',
            fontFamily:     'var(--font)',
            fontWeight:     700,
            fontSize:       14,
            color:          'white',
            cursor:         'pointer',
            boxShadow:      '0 3px 12px rgba(37,99,235,0.22)',
            transition:     'background 0.18s, transform 0.15s',
            textDecoration: 'none',
            display:        'inline-block',
        }}
        onMouseEnter={e => {
            (e.currentTarget as HTMLAnchorElement).style.background  = 'var(--blue-dark)';
            (e.currentTarget as HTMLAnchorElement).style.transform   = 'translateY(-1px)';
        }}
        onMouseLeave={e => {
            (e.currentTarget as HTMLAnchorElement).style.background  = 'var(--blue)';
            (e.currentTarget as HTMLAnchorElement).style.transform   = 'translateY(0)';
        }}
    >
        Sign Up
    </Link>
);

// ─────────────────────────────────────────────
// MAIN COMPONENT
// ─────────────────────────────────────────────

/**
 * HomeNavbar takes no props — it manages everything internally.
 *
 * Internal state:
 *   scrolled — true when window.scrollY > 8px, adds a
 *              subtle shadow to signal the page has scrolled.
 *
 * Auth state comes from useAuthStore — if the user is already
 * logged in (e.g. they navigated back to homepage), we show
 * a welcome message instead of Login/Sign Up.
 */
const HomeNavbar = () => {
    // ── Scroll state ──────────────────────────────────────────
    // Owned by the navbar — the parent never needs to know about this.
    const [scrolled, setScrolled] = useState<boolean>(false);

    useEffect(() => {
        const handleScroll = () => setScrolled(window.scrollY > 8);

        window.addEventListener('scroll', handleScroll);

        // IMPORTANT: Always clean up event listeners on unmount.
        // Without this, the listener keeps running even after the
        // component is gone — causing memory leaks and stale updates.
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);
    // The empty [] dependency array means this effect runs
    // once on mount and cleans up on unmount — never re-runs.

    // ── Auth state ────────────────────────────────────────────
    // We read from the global store — no prop drilling needed.
    // If user is logged in, show their name. If not, show Login/Signup.
    const user = useAuthStore((state) => state.user);

    return (
        <nav
            // scrolled-nav is a CSS class defined in global.css
            // that adds a subtle bottom border + shadow on scroll
            className={scrolled ? 'scrolled-nav' : ''}
            style={{
                position:       'fixed',
                top:            0,
                left:           0,
                right:          0,
                zIndex:         100,
                height:         68,
                padding:        '0 6%',
                display:        'flex',
                alignItems:     'center',
                justifyContent: 'space-between',
                // Glassmorphism: semi-transparent white + blur
                background:     'rgba(255,255,255,0.94)',
                backdropFilter: 'blur(14px)',
                transition:     'box-shadow 0.3s',
            }}
            // Accessibility: label the nav landmark for screen readers
            aria-label="Main navigation"
        >

            {/* ── Logo ── */}
            {/* af + d1 = fadeIn animation with 0.06s delay */}
            <div className="af d1">
                {/* Clicking the logo always goes back to homepage */}
                <Link to="/" style={{ display: 'block' }}>
                    <TaraLogo />
                </Link>
            </div>

            {/* ── Nav Links ── */}
            {/* af + d2 = fadeIn animation with 0.18s delay */}
            <div className="af d2" style={{ display: 'flex', gap: 30 }}>
                {NAV_LINKS.map((link) => (
                    // Using the link text as key is safe here because
                    // NAV_LINKS never has duplicate values
                    <a key={link} className="nav-a" href={`#${link.toLowerCase().replace(/\s+/g, '-')}`}>
                        {link}
                    </a>
                ))}
            </div>

            {/* ── Auth Buttons ── */}
            {/* af + d3 = fadeIn animation with 0.30s delay */}
            <div className="af d3" style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                {user ? (
                    // User is already logged in — show their name
                    // and a link to their dashboard instead of Login/Signup
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <span style={{
                fontSize:   14,
                fontWeight: 500,
                color:      'var(--text-sub)',
            }}>
              Welcome, {user.name ?? user.email}
            </span>
                        <Link
                            to={user.role === 'driver' ? '/driver/dashboard' : '/passenger/dashboard'}
                            style={{
                                background:     'var(--blue)',
                                borderRadius:   10,
                                padding:        '8px 20px',
                                fontFamily:     'var(--font)',
                                fontWeight:     700,
                                fontSize:       14,
                                color:          'white',
                                textDecoration: 'none',
                                display:        'inline-block',
                            }}
                        >
                            Dashboard →
                        </Link>
                    </div>
                ) : (
                    // User is not logged in — show Login + Sign Up
                    <>
                        <LoginButton />
                        <SignUpButton />
                    </>
                )}
            </div>

        </nav>
    );
};

export default HomeNavbar;