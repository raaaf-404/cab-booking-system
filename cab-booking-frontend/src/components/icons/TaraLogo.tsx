// ============================================================
// components/icons/TaraLogo.tsx
//
// WHAT THIS IS:
//   The official Tara brand logo as an SVG React component.
//
// WHY IT LIVES IN components/icons/:
//   The logo is used in multiple places:
//     → HomeNavbar.tsx      (public marketing page)
//     → layout/HomeNavbar.tsx   (authenticated app navbar)
//     → Any future pages    (email headers, footers, etc.)
//
//   Keeping it in one place means ONE file to update when
//   the brand changes — not hunting across the codebase.
//
// PROPS:
//   height — controls rendered size (default: 44px)
//            width always scales automatically via width: 'auto'
//            so the aspect ratio is always preserved.
//
// ACCESSIBILITY:
//   aria-label + role="img" tells screen readers what this is.
//   Without these, screen readers see a nameless image blob.
// ============================================================

interface TaraLogoProps {
    /** Height in pixels. Width scales automatically. Default: 44 */
    height?: number;
}

const TaraLogo = ({ height = 44 }: TaraLogoProps) => (
    <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 280 72"
        height={height}
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

        {/* Location pin mark — the "T" of Tara */}
        <g transform="translate(0,2) scale(0.52)" filter="url(#pinShadow)">
            <path
                d="M45 0 C20.1 0 0 20.1 0 45 C0 75 45 100 45 100 C45 100 90 75 90 45 C90 20.1 69.9 0 45 0 Z"
                fill="url(#pinGrad)"
            />
            {/* White T-shape cutout inside the pin */}
            <path d="M25 28 H65 V40 H51 V65 H39 V40 H25 Z" fill="#FFFFFF" />
        </g>

        {/* Brand name */}
        <text
            x="60" y="44"
            fontFamily="'Plus Jakarta Sans', sans-serif"
            fontSize="34" fontWeight="800"
            fill="#0F172A" letterSpacing="-1"
        >
            Tara
        </text>

        {/* Tagline */}
        <text
            x="62" y="60"
            fontFamily="'Plus Jakarta Sans', sans-serif"
            fontSize="7.5" fontWeight="600"
            fill="#94A3B8" letterSpacing="3"
        >
            LET'S GO PLACES
        </text>
    </svg>
);

export default TaraLogo;