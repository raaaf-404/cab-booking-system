// components/ui/TaraLogo.tsx
import React from 'react';

interface TaraLogoProps extends React.SVGProps<SVGSVGElement> {}

export const TaraLogo: React.FC<TaraLogoProps> = ({ className, ...props }) => {
    return (
        // Note: Converted HTML attributes (stop-color, font-family) to React camelCase (stopColor, fontFamily)
        <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 400 120"
            className={`w-full max-w-[150px] md:max-w-[200px] ${className || ''}`}
            {...props}
        >
            <defs>
                <linearGradient id="gradLight" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stopColor="#2563EB" />
                    <stop offset="100%" stopColor="#3B82F6" />
                </linearGradient>
                <filter id="shadowLight" x="-10%" y="-10%" width="130%" height="130%">
                    <feDropShadow dx="0" dy="4" stdDeviation="4" floodColor="#2563EB" floodOpacity="0.2"/>
                </filter>
            </defs>
            <g transform="translate(20, 15)" filter="url(#shadowLight)">
                <path d="M45 0 C20.1 0 0 20.1 0 45 C0 75 45 100 45 100 C45 100 90 75 90 45 C90 20.1 69.9 0 45 0 Z" fill="url(#gradLight)"/>
                <path d="M25 28 H65 V40 H51 V65 H39 V40 H25 Z" fill="#FFFFFF"/>
            </g>
            {/* Fallback to system fonts for a cleaner, modern look if 'inherit' fails */}
            <text x="135" y="76" fontFamily="system-ui, sans-serif" fontSize="64" fontWeight="800" fill="#0F172A" letterSpacing="-1.5">Tara</text>
            <text x="140" y="98" fontFamily="system-ui, sans-serif" fontSize="12" fontWeight="600" fill="#64748B" letterSpacing="3.5">LET'S GO PLACES</text>
        </svg>
    );
};