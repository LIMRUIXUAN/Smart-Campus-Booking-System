---
name: Academic Precision
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#444651'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#757682'
  outline-variant: '#c5c5d3'
  surface-tint: '#4059aa'
  primary: '#00236f'
  on-primary: '#ffffff'
  primary-container: '#1e3a8a'
  on-primary-container: '#90a8ff'
  inverse-primary: '#b6c4ff'
  secondary: '#00687a'
  on-secondary: '#ffffff'
  secondary-container: '#57dffe'
  on-secondary-container: '#006172'
  tertiary: '#00311f'
  on-tertiary: '#ffffff'
  tertiary-container: '#004a31'
  on-tertiary-container: '#27c38a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b6c4ff'
  on-primary-fixed: '#00164e'
  on-primary-fixed-variant: '#264191'
  secondary-fixed: '#acedff'
  secondary-fixed-dim: '#4cd7f6'
  on-secondary-fixed: '#001f26'
  on-secondary-fixed-variant: '#004e5c'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 12px
  md: 16px
  lg: 24px
  xl: 32px
  2xl: 48px
  container-max: 1440px
  gutter: 24px
---

## Brand & Style

The design system is engineered for high-utility campus management, blending the reliability of academic institutions with the efficiency of modern SaaS productivity tools. The brand personality is authoritative yet accessible, focusing on clarity, trust, and frictionless navigation.

The design style follows a **Modern Corporate** approach with leanings toward **Minimalism**. It prioritizes heavy whitespace to reduce cognitive load in data-dense environments. Visual interest is generated through purposeful color accents rather than decorative elements. The emotional response should be one of "controlled efficiency"—users should feel that the system is powerful enough to handle complex logistics while remaining simple enough for daily student and faculty use.

## Colors

The palette is anchored by a deep Indigo primary, signaling stability and academic tradition. The Cyan secondary is used for interactive elements and primary actions to provide a modern, energetic contrast. 

- **Primary (#1E3A8A):** Used for navigation sidebars, primary buttons, and headers.
- **Secondary (#06B6D4):** Used for focus states, active indicators, and secondary actions.
- **Tertiary (#10B981):** Reserved for "Success" states and "Completed" booking statuses.
- **Background (#F8FAFC):** A cool off-white that provides a crisp canvas for white cards to pop.
- **Semantic Colors:** Error (#EF4444) for cancellations and Warning (#F59E0B) for no-shows or pending actions.

## Typography

This design system utilizes **Inter** exclusively to leverage its exceptional legibility in dashboard environments. The typographic scale is built on a tight 4px baseline grid. 

Headlines use a tighter letter-spacing and heavier weights to establish clear hierarchy against data tables. Body text is optimized for readability with a generous line height. Use `label-caps` for table headers and small metadata categories to differentiate secondary information from primary content.

## Layout & Spacing

The system employs a **Fixed Grid** model for desktop to ensure data visualizations remain centered and readable, transitioning to a **Fluid Grid** for tablet and mobile.

- **Desktop (1440px+):** 12-column grid, 24px gutters, 48px side margins.
- **Tablet (768px - 1439px):** 8-column grid, 16px gutters, 24px side margins.
- **Mobile (< 767px):** 4-column grid, 16px gutters, 16px side margins.

Horizontal spacing between related elements (like an icon and its label) should use `xs` (8px). Vertical spacing between distinct card sections should use `lg` (24px) to maintain an airy, professional feel.

## Elevation & Depth

Hierarchy is established through **Tonal Layering** and **Ambient Shadows**. 

The background sits at the lowest level (Level 0). Main content areas and cards sit at Level 1, utilizing a soft, diffused shadow (0px 4px 20px rgba(30, 58, 138, 0.05)) to separate them from the off-white background. 

Popovers, tooltips, and modals sit at Level 2, with a more pronounced shadow and a subtle 1px border (#E2E8F0) to ensure they are perceived as being "closer" to the user. Avoid heavy blacks in shadows; use the primary indigo at very low opacity to maintain a clean, "academic tech" aesthetic.

## Shapes

The design system uses a **Rounded** shape language to soften the "institutional" feel of the campus system, making it more approachable. 

The standard radius for cards and larger containers is `1.5rem` (24px) to match the "2xl" requirement, creating a distinct, high-end SaaS appearance. Smaller interactive components like buttons and input fields follow a standard `0.5rem` (8px) radius for functional precision. Status badges use a full pill-shape (999px) to distinguish them from interactive buttons.

## Components

### Buttons & Inputs
- **Primary Button:** Solid Indigo (#1E3A8A) with white text. High-contrast, 0.5rem roundedness.
- **Input Fields:** White background with a 1px border (#CBD5E1). On focus, the border shifts to Cyan (#06B6D4) with a subtle outer glow.

### Status Badges
Badges use a "soft-fill" style: 10% opacity of the status color for the background and 100% opacity for the text.
- **Confirmed:** Primary Blue background/text.
- **Completed:** Tertiary Green background/text.
- **Cancelled:** Neutral Gray or Red background/text.
- **No-show:** Warning Orange background/text.

### Cards
Cards are the primary container for booking information. They must have a white background, `1.5rem` corner radius, and the defined Level 1 ambient shadow. Internal padding should be a consistent `24px` (lg).

### Specialized Components
- **Booking Calendar:** Use a clean, borderless grid with "Active" slots highlighted in light Cyan.
- **Resource Chips:** Small, 8px rounded labels used for room features (e.g., "Projector", "AC", "10 Seats"). Use a neutral gray background.