// ============================================================
// components/home/FloatingIllustration.tsx
//
// WHAT THIS COMPONENT DOES:
//   The animated UI card stack shown on the right side of the
//   hero section. Simulates a live cab booking interface with
//   5 floating cards, ambient blobs, connector lines, and dots.
//
// ARCHITECTURE DECISION — private sub-components:
//   This file is complex (~250 lines). Rather than writing
//   everything inline inside one giant return(), we split it
//   into small, named sub-components defined in THIS file.
//
//   WHY NOT put them in separate files?
//     Sub-components like <AmbientBlobs> and <ConnectorLines>
//     are ONLY ever used inside FloatingIllustration. Putting
//     them in their own files would make the folder cluttered
//     for no benefit. The rule is:
//
//       → Used in 1 place only  → private, same file
//       → Used in 2+ places     → own file, exported
//
// DATA:
//   All demo data comes from constants — zero hardcoding here.
//   DEMO_ prefix = "replace this with an API call later."
//
// ANIMATION CLASSES (defined in global.css / GlobalStyles):
//   .float-card    → base card style (white, rounded, shadow)
//   .f1–f5         → per-card float animation (different speeds)
//   .blob-spin     → slow rotation for the ambient blob
//   .ripple-ring   → expanding ring pulse on the location pin
//   .pulse-dot     → pulsing opacity on SVG connector dots
//   .pop-in        → scale-in animation on the green check badge
//   .fill-bar      → width animation on the progress bar
// ============================================================

import type {
    DemoDriver,
    FareRow,
    DemoRoute,
    DemoEta,
    DecorativeDot,
} from '@/types/home.types';

import {
    DEMO_DRIVER,
    DEMO_FARE_ROWS,
    DEMO_FARE_TOTAL,
    DEMO_ROUTE,
    DEMO_ETA,
    DECORATIVE_DOTS,
} from '@/constants/homeConstants';

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: AmbientBlobs
// ─────────────────────────────────────────────────────────────
// These blurred circles create a soft glowing backdrop.
// They replace a hard-edged box — the cards appear to float
// ON the page rather than inside a container.
// pointerEvents: 'none' ensures they never block click events.

const AmbientBlobs = () => (
    <>
        {/* Primary blob — large, centered, slow rotation */}
        <div
            className="blob-spin"
            style={{
                position:      'absolute',
                top:           '50%',
                left:          '50%',
                transform:     'translate(-50%, -50%)',
                width:         420,
                height:        380,
                // Organic shape via border-radius percentages
                borderRadius:  '60% 40% 55% 45% / 50% 60% 40% 50%',
                background:    'radial-gradient(ellipse at 40% 40%, #DBEAFE 0%, #EFF6FF 50%, transparent 75%)',
                filter:        'blur(48px)',
                opacity:       0.75,
                zIndex:        0,
                pointerEvents: 'none',
            }}
        />

        {/* Secondary blob — smaller, top-right, adds color depth */}
        <div
            style={{
                position:      'absolute',
                top:           '30%',
                right:         '8%',
                width:         180,
                height:        180,
                borderRadius:  '50%',
                background:    'radial-gradient(circle, rgba(196,221,255,0.5) 0%, transparent 70%)',
                filter:        'blur(32px)',
                zIndex:        0,
                pointerEvents: 'none',
            }}
        />
    </>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: ConnectorLines
// ─────────────────────────────────────────────────────────────
// SVG dashed lines that draw visual relationships between cards.
// The pulsing dots suggest live data flowing through the system.
// Using an SVG overlay (position: absolute, inset: 0) lets us
// draw lines across the entire illustration without affecting layout.

const ConnectorLines = () => (
    <svg
        style={{
            position:      'absolute',
            inset:         0,
            width:         '100%',
            height:        '100%',
            zIndex:        1,
            pointerEvents: 'none',
            overflow:      'visible',
        }}
        aria-hidden="true" // decorative — screen readers skip this
    >
        {/* Line: Location pin → Route card */}
        <line
            x1="52%" y1="18%" x2="72%" y2="32%"
            stroke="#BFDBFE" strokeWidth="1.5"
            strokeDasharray="4 5"
        />
        {/* Line: Route card → ETA card */}
        <line
            x1="72%" y1="42%" x2="62%" y2="60%"
            stroke="#BFDBFE" strokeWidth="1.5"
            strokeDasharray="4 5"
        />
        {/* Line: ETA → Driver card */}
        <line
            x1="38%" y1="58%" x2="28%" y2="74%"
            stroke="#BFDBFE" strokeWidth="1.5"
            strokeDasharray="4 5"
        />

        {/* Pulsing dots along the lines — suggests live data flow */}
        <circle className="pulse-dot" cx="62%" cy="25%" r="3" fill="#93C5FD" />
        <circle className="pulse-dot" cx="66%" cy="51%" r="3" fill="#93C5FD"
                style={{ animationDelay: '0.6s' }} />
        <circle className="pulse-dot" cx="33%" cy="66%" r="3" fill="#93C5FD"
                style={{ animationDelay: '1.2s' }} />
    </svg>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: RideConfirmedCard  (Card 1 — top center)
// ─────────────────────────────────────────────────────────────
// Primary card — largest visual anchor.
// Shows a progress bar animating from 0 → 72% on load.
const RideConfirmedCard = () => (
    <div
        className="float-card f1"
        style={{ top: '2%', left: '22%', padding: '18px 20px', width: 230, zIndex: 4 }}
    >
        {/* Header row: icon + title + green check */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 14 }}>
            <div style={{
                width:          36,
                height:         36,
                borderRadius:   10,
                background:     '#F0FDF4',
                display:        'flex',
                alignItems:     'center',
                justifyContent: 'center',
                fontSize:       18,
            }}>
                🚗
            </div>

            <div>
                <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--text)' }}>
                    Ride Confirmed
                </div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 1 }}>
                    Driver on the way
                </div>
            </div>

            {/* pop-in = CSS scale animation from 0 → 1 on load */}
            <div className="pop-in" style={{
                marginLeft:     'auto',
                width:          22,
                height:         22,
                borderRadius:   '50%',
                background:     '#22C55E',
                display:        'flex',
                alignItems:     'center',
                justifyContent: 'center',
                fontSize:       12,
                color:          'white',
                fontWeight:     700,
            }}>
                ✓
            </div>
        </div>

        {/* Progress bar: fill-bar CSS animates width from 0 → 72% */}
        <div style={{
            height:       5,
            background:   '#F1F5F9',
            borderRadius: 10,
            overflow:     'hidden',
            marginBottom: 10,
        }}>
            <div
                className="fill-bar"
                style={{
                    height:     '100%',
                    background: 'linear-gradient(90deg, #2563EB, #60A5FA)',
                    borderRadius: 10,
                    width:      0,   // animation takes over from here
                }}
            />
        </div>

        {/* Progress labels */}
        <div style={{
            display:        'flex',
            justifyContent: 'space-between',
            fontSize:       10,
            fontWeight:     600,
            color:          'var(--text-muted)',
        }}>
            <span>Pickup</span>
            <span style={{ color: 'var(--blue)' }}>72%</span>
            <span>Dropoff</span>
        </div>
    </div>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: LocationPinCard  (Card 2 — top right)
// ─────────────────────────────────────────────────────────────
// Receives route data as props — no hardcoding.
// ripple-ring = expanding circle animation on the pin icon.

interface LocationPinCardProps {
    route: DemoRoute;
}

const LocationPinCard = ({ route }: LocationPinCardProps) => (
    <div
        className="float-card f2"
        style={{ top: '6%', right: '4%', padding: '14px 16px', width: 160, zIndex: 3 }}
    >
        {/* Pin icon row */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
            <div style={{ position: 'relative' }}>
                {/* Expanding ring — draws attention to the pickup point */}
                <div className="ripple-ring" style={{
                    position:  'absolute',
                    top:       '50%',
                    left:      '50%',
                    transform: 'translate(-50%, -50%)',
                    width:     28,
                    height:    28,
                    borderRadius: '50%',
                    border:    '1.5px solid #2563EB',
                }} />
                <div style={{
                    width:          28,
                    height:         28,
                    borderRadius:   '50%',
                    background:     'var(--blue-mid)',
                    display:        'flex',
                    alignItems:     'center',
                    justifyContent: 'center',
                    fontSize:       14,
                    zIndex:         1,
                    position:       'relative',
                }}>
                    📍
                </div>
            </div>

            <div>
                <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--text-muted)' }}>
                    PICKUP
                </div>
                <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--text)', marginTop: 1 }}>
                    {route.pickup}
                </div>
            </div>
        </div>

        {/* Destination row */}
        <div style={{
            borderTop:  '1px solid var(--border)',
            paddingTop: 8,
            display:    'flex',
            alignItems: 'center',
            gap:        8,
        }}>
            <div style={{
                width:        6,
                height:       6,
                borderRadius: '50%',
                background:   'var(--blue)',
                flexShrink:   0,
            }} />
            <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--text-sub)' }}>
                → {route.dropoff}
            </div>
        </div>
    </div>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: DriverCard  (Card 3 — middle left)
// ─────────────────────────────────────────────────────────────
// Renders driver avatar, name, star rating and vehicle info.
// Array(driver.stars).fill(0) generates N star elements without
// needing to hardcode [1,2,3,4,5] — it scales to any rating.

interface DriverCardProps {
    driver: DemoDriver;
}

const DriverCard = ({ driver }: DriverCardProps) => (
    <div
        className="float-card f3"
        style={{ top: '34%', left: '0%', padding: '16px 18px', width: 210, zIndex: 4 }}
    >
        <div style={{
            fontSize:      10,
            fontWeight:    700,
            color:         'var(--text-muted)',
            letterSpacing: '1px',
            marginBottom:  12,
        }}>
            YOUR DRIVER
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            {/* Avatar circle */}
            <div style={{
                width:          44,
                height:         44,
                borderRadius:   '50%',
                background:     'linear-gradient(135deg, #DBEAFE, #93C5FD)',
                display:        'flex',
                alignItems:     'center',
                justifyContent: 'center',
                fontSize:       22,
                flexShrink:     0,
                border:         '2px solid white',
                boxShadow:      '0 2px 8px rgba(37,99,235,0.15)',
            }}>
                {driver.avatar}
            </div>

            <div style={{ flex: 1 }}>
                <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--text)' }}>
                    {driver.name}
                </div>

                {/* Star rating — Array(n).fill(0) creates n-length array */}
                <div style={{ display: 'flex', alignItems: 'center', gap: 4, marginTop: 3 }}>
                    {Array(driver.stars).fill(0).map((_, i) => (
                        <span key={i} style={{ fontSize: 10, color: '#FBBF24' }}>★</span>
                    ))}
                    <span style={{ fontSize: 11, color: 'var(--text-muted)', marginLeft: 2 }}>
            {driver.rating}
          </span>
                </div>

                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>
                    {driver.vehicle}
                </div>
            </div>
        </div>
    </div>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: EtaCard  (Card 4 — middle right)
// ─────────────────────────────────────────────────────────────
// Countdown-style card showing minutes until arrival.
// The mini progress dots use index math to highlight the center
// dot — i === 2 (middle of 5) gets the wider blue style.

interface EtaCardProps {
    eta: DemoEta;
}

const EtaCard = ({ eta }: EtaCardProps) => (
    <div
        className="float-card f4"
        style={{
            top:       '38%',
            right:     '2%',
            padding:   '16px 20px',
            width:     150,
            zIndex:    3,
            textAlign: 'center',
        }}
    >
        <div style={{
            fontSize:      10,
            fontWeight:    700,
            color:         'var(--text-muted)',
            letterSpacing: '1px',
            marginBottom:  8,
        }}>
            ARRIVES IN
        </div>

        {/* Large ETA number */}
        <div style={{
            fontSize:      42,
            fontWeight:    800,
            color:         'var(--blue)',
            lineHeight:    1,
            letterSpacing: '-2px',
        }}>
            {eta.value}
        </div>
        <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-muted)', marginTop: 4 }}>
            {eta.unit}
        </div>

        {/* Mini progress dots — middle dot is wider to show current position */}
        <div style={{
            display:        'flex',
            justifyContent: 'center',
            gap:            4,
            marginTop:      12,
        }}>
            {/* 5 dots: index 2 (center) is highlighted */}
            {[0, 1, 2, 3, 4].map(i => (
                <div key={i} style={{
                    width:        i === 2 ? 16 : 6,  // center dot is wider
                    height:       6,
                    borderRadius: 3,
                    background:   i === 2 ? 'var(--blue)' : 'var(--blue-mid)',
                    transition:   'width 0.2s',
                }} />
            ))}
        </div>
    </div>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: FareCard  (Card 5 — bottom center)
// ─────────────────────────────────────────────────────────────
// Receipt-style breakdown of the fare.
// Uses index to determine whether to show a bottom border
// between rows — last row has no border before the total.

interface FareCardProps {
    rows:  FareRow[];
    total: string;
}

const FareCard = ({ rows, total }: FareCardProps) => (
    <div
        className="float-card f5"
        style={{ bottom: '4%', left: '18%', padding: '16px 20px', width: 220, zIndex: 4 }}
    >
        <div style={{
            fontSize:      10,
            fontWeight:    700,
            color:         'var(--text-muted)',
            letterSpacing: '1px',
            marginBottom:  12,
        }}>
            FARE ESTIMATE
        </div>

        {/* Fare line items */}
        {rows.map((row, i) => {
            const isLast = i === rows.length - 1;
            return (
                <div key={row.label} style={{
                    display:        'flex',
                    justifyContent: 'space-between',
                    alignItems:     'center',
                    marginBottom:   isLast ? 0 : 8,
                    paddingBottom:  isLast ? 0 : 8,
                    // Divider between rows — none after the last row
                    borderBottom:   isLast ? 'none' : '1px solid var(--border)',
                }}>
          <span style={{ fontSize: 12, color: 'var(--text-sub)', fontWeight: 500 }}>
            {row.label}
          </span>
                    <span style={{ fontSize: 12, color: 'var(--text)', fontWeight: 600 }}>
            {row.value}
          </span>
                </div>
            );
        })}

        {/* Total row — heavier border + larger text */}
        <div style={{
            display:        'flex',
            justifyContent: 'space-between',
            alignItems:     'center',
            marginTop:      10,
            paddingTop:     10,
            borderTop:      '2px solid var(--border)',
        }}>
      <span style={{ fontSize: 13, color: 'var(--text)', fontWeight: 700 }}>
        Total
      </span>
            <span style={{
                fontSize:      16,
                fontWeight:    800,
                color:         'var(--blue)',
                letterSpacing: '-0.5px',
            }}>
        {total}
      </span>
        </div>
    </div>
);

// ─────────────────────────────────────────────────────────────
// PRIVATE SUB-COMPONENT: DecorativeDots
// ─────────────────────────────────────────────────────────────
// Scattered dots that add texture to the illustration.
// They float freely — not inside cards — using absolute positioning.
// right is optional on DecorativeDot so we spread the dot object
// and let undefined values be ignored by the style object.

interface DecorativeDotsProps {
    dots: DecorativeDot[];
}

const DecorativeDots = ({ dots }: DecorativeDotsProps) => (
    <>
        {dots.map((dot, i) => (
            <div
                key={i}
                style={{
                    position:     'absolute',
                    top:          dot.top,
                    left:         dot.left,
                    // right is optional — only set if defined in the data
                    ...(dot.right ? { right: dot.right } : {}),
                    width:        dot.size,
                    height:       dot.size,
                    borderRadius: '50%',
                    background:   dot.color,
                    zIndex:       2,
                    // Each dot gets a slightly different float speed + delay
                    // 4 + i * 0.7 staggers the animation so dots don't move in sync
                    animation:    `float1 ${4 + i * 0.7}s ease-in-out ${dot.delay} infinite`,
                }}
            />
        ))}
    </>
);

// ─────────────────────────────────────────────────────────────
// MAIN COMPONENT: FloatingIllustration
// ─────────────────────────────────────────────────────────────
// Orchestrates all sub-components.
// This component takes NO props — it sources all data from
// constants. When the backend is ready, swap constants for
// a useQuery() hook here and pass real data down as props.

const FloatingIllustration = () => (
    <div
        style={{
            position: 'relative',
            width:    '100%',
            height:   520,
            // No border, no background here — pure floating cards.
            // Visual weight comes entirely from the ambient blobs.
        }}
        // Accessibility: mark as decorative — screen readers skip it
        aria-hidden="true"
    >
        {/* Layer 0: ambient background blobs */}
        <AmbientBlobs />

        {/* Layer 1: SVG connector lines + pulse dots */}
        <ConnectorLines />

        {/* Layer 2: decorative scattered dots */}
        <DecorativeDots dots={DECORATIVE_DOTS} />

        {/* Layer 3+: the floating cards (zIndex 3–4) */}
        <RideConfirmedCard />
        <LocationPinCard   route={DEMO_ROUTE}                    />
        <DriverCard        driver={DEMO_DRIVER}                  />
        <EtaCard           eta={DEMO_ETA}                        />
        <FareCard          rows={DEMO_FARE_ROWS} total={DEMO_FARE_TOTAL} />
    </div>
);

export default FloatingIllustration;
