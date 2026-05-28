import { describe, expect, it } from 'vitest'
import { generateSuggestions, overlaps, validateBookingSlot } from './booking'

const futureNow = new Date('2026-01-10T08:00:00')
const bookings = [
  {
    id: 'b1',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-11',
    startTime: '14:00',
    endTime: '15:00',
    status: 'confirmed',
  },
  {
    id: 'b2',
    userId: 'u-student',
    resourceId: 'lab-a',
    date: '2026-01-11',
    startTime: '16:00',
    endTime: '17:00',
    status: 'confirmed',
  },
  {
    id: 'b3',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-11',
    startTime: '10:00',
    endTime: '11:00',
    status: 'cancelled',
  },
]

describe('booking utilities', () => {
  it('detects overlapping ranges with the documented rule', () => {
    expect(
      overlaps(
        { date: '2026-01-11', startTime: '13:30', endTime: '14:30' },
        { date: '2026-01-11', startTime: '14:00', endTime: '15:00' },
      ),
    ).toBe(true)
    expect(
      overlaps(
        { date: '2026-01-11', startTime: '15:00', endTime: '16:00' },
        { date: '2026-01-11', startTime: '14:00', endTime: '15:00' },
      ),
    ).toBe(false)
  })

  it('blocks same-resource overlaps', () => {
    const result = validateBookingSlot(
      {
        userId: 'u-student',
        resourceId: 'room-a',
        date: '2026-01-11',
        startTime: '14:30',
        endTime: '15:30',
      },
      bookings,
      { now: futureNow },
    )
    expect(result.type).toBe('resource-conflict')
  })

  it('blocks same-user overlaps across resources', () => {
    const result = validateBookingSlot(
      {
        userId: 'u-student',
        resourceId: 'room-b',
        date: '2026-01-11',
        startTime: '16:30',
        endTime: '17:30',
      },
      bookings,
      { now: futureNow },
    )
    expect(result.type).toBe('user-conflict')
  })

  it('rejects bookings longer than two hours', () => {
    const result = validateBookingSlot(
      {
        userId: 'u-student',
        resourceId: 'room-b',
        date: '2026-01-11',
        startTime: '09:00',
        endTime: '11:30',
      },
      bookings,
      { now: futureNow },
    )
    expect(result.type).toBe('duration')
  })

  it('rejects past slots', () => {
    const result = validateBookingSlot(
      {
        userId: 'u-student',
        resourceId: 'room-b',
        date: '2026-01-09',
        startTime: '09:00',
        endTime: '10:00',
      },
      bookings,
      { now: futureNow },
    )
    expect(result.type).toBe('past')
  })

  it('ignores cancelled bookings', () => {
    const result = validateBookingSlot(
      {
        userId: 'u-student',
        resourceId: 'room-a',
        date: '2026-01-11',
        startTime: '10:00',
        endTime: '11:00',
      },
      bookings,
      { now: futureNow },
    )
    expect(result.available).toBe(true)
  })

  it('generates smart suggestions around conflicts', () => {
    const resources = [
      { id: 'room-a', name: 'Study Room A', type: 'Room', capacity: 4, status: 'active' },
      { id: 'room-b', name: 'Study Room B', type: 'Room', capacity: 4, status: 'active' },
    ]
    const suggestions = generateSuggestions(
      {
        userId: 'u-student',
        resourceId: 'room-a',
        date: '2026-01-11',
        startTime: '14:00',
        endTime: '15:00',
      },
      resources,
      bookings,
      futureNow,
    )
    expect(suggestions.length).toBeGreaterThan(0)
    expect(suggestions.some((item) => item.resourceId === 'room-b' || item.startTime !== '14:00')).toBe(true)
  })
})
