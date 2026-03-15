// ============================================================
// components/icons/IcoArrow.tsx
//
// WHAT THIS IS:
//   A simple right-pointing arrow icon used inside
//   the hero CTA button ("Book a Ride →").
//
// WHY EXTRACT A SIMPLE ICON?
//   Even a 6-line SVG benefits from extraction because:
//     1. If the icon changes, one file to update.
//     2. The component that uses it (HeroSection) stays
//        clean — no inline SVG cluttering the JSX.
//     3. It's now searchable and reusable across the app.
//
// PROPS:
//   size  — width and height in pixels (default: 16)
//   color — stroke color (default: white for CTA buttons)
//
//   WHY make color a prop?
//   The same arrow could be used on a light background
//   someday (e.g. an outlined button) — hardcoding "white"
//   would force you to create a second component.
//   Props make the component flexible from day one.
// ============================================================

interface IcoArrowProps {
    /** Width and height in pixels. Default: 16 */
    size?:  number;
    /** Stroke color. Default: 'white' */
    color?: string;
}

const IcoArrow = ({ size = 16, color = 'white' }: IcoArrowProps) => (
    <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        aria-hidden="true" // decorative — the button label conveys meaning
    >
        <path
            // Horizontal line + arrowhead chevron
            d="M5 12h14M13 6l6 6-6 6"
            stroke={color}
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
        />
    </svg>
);

export default IcoArrow;