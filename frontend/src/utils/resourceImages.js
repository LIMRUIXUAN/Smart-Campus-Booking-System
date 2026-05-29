const fallbackPalette = {
  Room: {
    start: '#f4ede3',
    end: '#d5e4f3',
    accent: '#1f4b99',
    label: 'Study room',
  },
  Lab: {
    start: '#e6f6f2',
    end: '#dbeafe',
    accent: '#0f766e',
    label: 'Lab',
  },
  Equipment: {
    start: '#f2ecff',
    end: '#fde68a',
    accent: '#6d28d9',
    label: 'Equipment',
  },
}

const encodeSvg = (svg) => `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`

export const fallbackResourceImage = (type = 'Room') => {
  const palette = fallbackPalette[type] || fallbackPalette.Room
  return encodeSvg(`
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500" role="img" aria-label="${palette.label}">
      <defs>
        <linearGradient id="bg" x1="0%" x2="100%" y1="0%" y2="100%">
          <stop offset="0%" stop-color="${palette.start}" />
          <stop offset="100%" stop-color="${palette.end}" />
        </linearGradient>
      </defs>
      <rect width="800" height="500" rx="40" fill="url(#bg)" />
      <rect x="70" y="60" width="660" height="380" rx="32" fill="white" opacity="0.92" />
      <rect x="110" y="115" width="250" height="170" rx="22" fill="${palette.end}" />
      <rect x="400" y="125" width="220" height="28" rx="14" fill="${palette.accent}" opacity="0.95" />
      <rect x="400" y="182" width="170" height="18" rx="9" fill="${palette.accent}" opacity="0.68" />
      <rect x="110" y="330" width="500" height="24" rx="12" fill="${palette.end}" />
      <circle cx="650" cy="342" r="52" fill="${palette.accent}" opacity="0.92" />
      <text x="650" y="354" text-anchor="middle" font-family="Arial, sans-serif" font-size="52" fill="white">+</text>
    </svg>
  `)
}

export const resolveResourceImage = (resource) => resource?.imageUrl || fallbackResourceImage(resource?.type)
