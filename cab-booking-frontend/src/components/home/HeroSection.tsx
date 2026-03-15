// ============================================================
// components/home/HeroSection.tsx
//
// WHAT THIS COMPONENT DOES:
//   The full-height hero section of the homepage.
//   Renders the left column (badge, headline, subtext, CTA,
//   icon pills) and the right column (floating illustration).
//
// ARCHITECTURE NOTE — why HeroSection uses sub-components:
//   Rather than writing everything inline, we delegate:
//     → <IconPills />           — the 3 trust pills row
//     → <FloatingIllustration /> — the right-side card stack
//   HeroSection only owns layout and the left column copy.
//   This keeps each file focused on one responsibility.
//
// CTA BUTTON:
//   Uses React Router useNavigate() to redirect to /login.
//   The old Homepage.jsx used alert() — that was placeholder.
//   useNavigate() is the correct production approach.
//
// HERO CONTENT DATA:
//   Badge text, headline, subtext, and CTA label all come
//   from HERO_CONTENT in homeConstants.tsx — zero hardcoding.
//   To update the copy, only edit the constants file.
//
// CSS CLASSES USED (defined in styles/global.css):
//   .au     = fadeUp entrance animation
//   .d1–d5  = stagger delays (each element enters slightly later)
//   .cta-btn = CTA button with shimmer hover effect
// ============================================================

import { useNavigate } from 'react-router-dom';
import { HERO_CONTENT } from '@/constants/homeConstants';
import { IcoArrow } from '@/components/icons';
import IconPills from '@/components/home/IconsPills';
import FloatingIllustration from '@/components/home/FloatingIllustration';


// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: DotGridBackground
// ─────────────────────────────────────────────────────────────
// Subtle dot-grid texture behind the hero content.
//ly decorative — aria-hidden removes it from// pointerEvents: 'none' ensures it never blocks interactions.
// This is pure
// the accessibility tree.

const DotGridBackground = () => (
    <div
        aria-hidden="true"
        style={{
            position:        'absolute',
            inset:           0,
            pointerEvents:   'none',
            // radial-gradient creates a dot at each background-size tile
            backgroundImage: 'radial-gradient(circle, #CBD5E1 1px, transparent 1px)',
            backgroundSize:  '26px 26px',
            opacity:         0.28,
        }}
    />
);


// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: LiveBadge
// ─────────────────────────────────────────────────────────────
// The green "Now live in Metro Manila" pill at the top.
// Receives the badge text from HERO_CONTENT via props so it
// can be updated from constants without touching JSX.

interface LiveBadgeProps {
    text: string;
}

const LiveBadge = ({ text }: LiveBadgeProps) => (
    <div
        className="au d1"
        style={{
            display:       'inline-flex',
            alignItems:    'center',
            gap:           7,
            background:    '#F0FDF4',
            border:        '1px solid #BBF7D0',
            borderRadius:  100,
            padding:       '5px 14px 5px 9px',
            marginBottom:  28,
            alignSelf:     'flex-start',
        }}
    >
        {/* Pulsing green dot — signals "live / active" */}
        <span style={{
            width:       8,
            height:      8,
            borderRadius: '50%',
            background:  '#22C55E',
            // Soft halo ring around the dot
            boxShadow:   '0 0 0 3px rgba(34, 197, 94, 0.18)',
            display:     'inline-block',
            flexShrink:  0,
        }} />
        <span style={{ fontSize: 13, fontWeight: 600, color: '#15803D' }}>
      {text}
    </span>
    </div>
);


// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: HeroHeadline
// ─────────────────────────────────────────────────────────────
// The large h1 headline.
// clamp() makes font size responsive without media queries:
//   clamp(MIN, PREFERRED, MAX)
//   → min: 38px (small screens)
//   → preferred: 4.8vw (scales with viewport)
//   → max: 62px (large screens — never gets bigger than this)
//
// WHY JSX AND NOT A STRING from constants?
//   The headline has two <span> accent words in the middle.
//   We can't inject JSX color spans from a plain string —
//   so we use line1/accent1/accent2/line2 parts from constants
//   and assemble the JSX structure here.

interface HeroHeadlineProps {
    line1:   string;
    accent1: string;
    accent2: string;
    line2:   string;
}

const HeroHeadline = ({ line1, accent1, accent2, line2 }: HeroHeadlineProps) => (
    <h1
        className="au d2"
        style={{
            // clamp(min, preferred, max) — responsive font size without @media
            fontSize:      'clamp(38px, 4.8vw, 62px)',
            fontWeight:    800,
            lineHeight:    1.08,
            letterSpacing: '-2px',
            color:         'var(--text)',
            marginBottom:  20,
        }}
    >
        {line1}{' '}
        <span style={{ color: 'var(--blue)' }}>{accent1}</span>
        <br />
        <span style={{ color: 'var(--blue)' }}>{accent2}</span>
        {' '}{line2}
    </h1>
);


// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: CtaButton
// ─────────────────────────────────────────────────────────────
// The primary call-to-action button.
// .cta-btn CSS class (in global.css) handles the shimmer
// hover effect — this cannot be done with inline styles.
//
// onClick navigates to /login via React Router useNavigate().
// We pass onClick as a prop so HeroSection owns the navigation
// logic — the button stays a dumb presentational component.

interface CtaButtonProps {
    label:   string;
    onClick: () => void;
}

const CtaButton = ({ label, onClick }: CtaButtonProps) => (
    <button className="cta-btn" onClick={onClick}>
        {label}
        {/* IcoArrow with default white color for the filled blue button */}
        <IcoArrow size={16} color="white" />
    </button>
);


// ─────────────────────────────────────────────────────────────
// MAIN COMPONENT: HeroSection
// ─────────────────────────────────────────────────────────────

const HeroSection = () => {
    // useNavigate gives us a function to programmatically
    // navigate to a route — replaces the old alert() placeholder.
    const navigate = useNavigate();

    // Pull all copy from constants — HeroSection has zero hardcoded strings
    const { badge, headline, subtext, ctaLabel } = HERO_CONTENT;

    const handleCta = () => {
        // TODO: when auth is wired, check if user is logged in here.
        // If logged in → navigate to /passenger/dashboard or /driver/dashboard
        // If not logged in → navigate to /login (current behaviour)
        navigate('/login');
    };

    return (
        <section
            style={{
                minHeight:     '100vh',
                paddingTop:    100,     // clears the fixed navbar (68px height + breathing room)
                paddingBottom: 60,
                paddingLeft:   '6%',
                paddingRight:  '4%',
                display:       'flex',
                alignItems:    'center',
                gap:           20,
                position:      'relative',
                overflow:      'hidden',
            }}
        >
            {/* Decorative dot-grid texture — sits behind everything */}
            <DotGridBackground />

            {/* ── LEFT COLUMN — copy + CTA ── */}
            <div style={{
                flex:          '0 0 46%',
                maxWidth:      500,
                position:      'relative',
                zIndex:        1,
                display:       'flex',
                flexDirection: 'column',
            }}>

                {/* "Now live in Metro Manila" badge */}
                <LiveBadge text={badge} />

                {/* Main headline with blue accent words */}
                <HeroHeadline
                    line1={headline.line1}
                    accent1={headline.accent1}
                    accent2={headline.accent2}
                    line2={headline.line2}
                />

                {/* Supporting paragraph */}
                <p
                    className="au d3"
                    style={{
                        fontSize:     17,
                        lineHeight:   1.75,
                        color:        'var(--text-sub)',
                        maxWidth:     420,
                        marginBottom: 40,
                        fontWeight:   400,
                    }}
                >
                    {subtext}
                </p>

                {/* CTA button row */}
                <div className="au d4" style={{ marginBottom: 44 }}>
                    <CtaButton label={ctaLabel} onClick={handleCta} />
                </div>

                {/* Trust pills row — Safe / Fast / Affordable */}
                {/* IconPills handles its own data from constants */}
                <IconPills />

            </div>

            {/* ── RIGHT COLUMN — floating illustration ── */}
            {/* No border or box — cards float directly on the page */}
            <div style={{
                flex:      1,
                position:  'relative',
                zIndex:    1,
                minHeight: 520,
            }}>
                <FloatingIllustration />
            </div>

        </section>
    );
};

export default HeroSection;