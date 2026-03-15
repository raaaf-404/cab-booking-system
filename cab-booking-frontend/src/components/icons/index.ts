// ============================================================
// components/icons/index.ts
//
// WHAT THIS IS:
//   A "barrel file" — it re-exports everything from this
//   folder through a single entry point.
//
// WHY THIS MATTERS:
//   Without this file, every consumer must know the exact
//   path to each icon:
//
//     import TaraLogo from '@/components/icons/TaraLogo';
//     import IcoArrow from '@/components/icons/IcoArrow';
//
//   With this barrel file, you write ONE clean import:
//
//     import { TaraLogo, IcoArrow } from '@/components/icons';
//
//   Benefits:
//     → Shorter import lines in every file that uses icons
//     → If you rename or move an icon file, only THIS barrel
//       needs updating — not every consumer across the app
//     → Makes the icons folder feel like a proper "package"
//
// HOW TO ADD A NEW ICON:
//   1. Create components/icons/IcoMyIcon.tsx
//   2. Add one line here: export { default as IcoMyIcon } from './IcoMyIcon';
//   That's it — it's immediately importable everywhere.
//
// NOTE: This file is .ts not .tsx because it contains
//   zero JSX — only re-export statements.
// ============================================================

export { default as TaraLogo } from './TaraLogo';
export { default as IcoArrow } from './IcoArrow';