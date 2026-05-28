import { describe, expect, it } from 'vitest'
import { generateSuggestions, overlaps, validateBookingSlot } from './booking'

const futureNow = new Date('2026-01-10T08:00:00')
const resource = { id: 'room-a', name: 'Study Room A', type: 'Room', capacity: 4, status: 'active' }
const bookings = [
  {
    id: 'b-confirmed',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-14',
    startTime: '14:00',
    endTime: '15:00',
    status: 'confirmed',
  },
  {
    id: 'b-approved',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-15',
    startTime: '09:00',
    endTime: '10:00',
    status: 'approved',
  },
  {
    id: 'b-pending',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-14',
    startTime: '10:00',
    endTime: '11:00',
    status: 'pending',
  },
  {
    id: 'b-user',
    userId: 'u-student',
    resourceId: 'lab-a',
    date: '2026-01-14',
    startTime: '16:00',
    endTime: '17:00',
    status: 'booked',
  },
  {
    id: 'b-cancelled',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-14',
    startTime: '11:00',
    endTime: '12:00',
    status: 'cancelled',
  },
  {
    id: 'b-rejected',
    userId: 'u-other',
    resourceId: 'room-a',
    date: '2026-01-14',
    startTime: '12:00',
    endTime: '13:00',
    status: 'rejected',
  },
]

const baseSlot = {
  userId: 'u-student',
  resourceId: 'room-a',
  startDate: '2026-01-14',
  date: '2026-01-14',
  startTime: '09:00',
  endDate: '2026-01-14',
  endTime: '10:00',
  pax: 2,
}

const check = (slot, options = {}) =>
  validateBookingSlot({ ...baseSlot, ...slot }, bookings, { now: futureNow, resource, ...options })

describe('booking utilities', () => {
  it('detects overlapping date-time ranges with the documented rule', () => {
    expect(
      overlaps(
        { startDate: '2026-01-14', startTime: '13:30', endDate: '2026-01-14', endTime: '14:30' },
        { date: '2026-01-14', startTime: '14:00', endTime: '15:00' },
      ),
    ).toBe(true)
    expect(
      overlaps(
        { date: '2026-01-14', startTime: '15:00', endTime: '16:00' },
        { date: '2026-01-14', startTime: '14:00', endTime: '15:00' },
      ),
    ).toBe(false)
  })

  it('allows an available slot', () => {
    const result = check({ startTime: '13:00', endTime: '14:00' })
    expect(result.available).toBe(true)
  })

  it('blocks overlapping confirmed bookings', () => {
    expect(check({ startTime: '14:30', endTime: '15:30' }).type).toBe('resource-conflict')
  })

  it('allows non-confirmed overlapping bookings', () => {
    const result = check({ startTime: '10:30', endTime: '11:30' })
    expect(result.available).toBe(true)
  })

  it('blocks same-user overlaps across resources', () => {
    const result = validateBookingSlot(
      { ...baseSlot, resourceId: 'room-b', startTime: '16:30', endTime: '17:30' },
      [{ ...bookings[3], status: 'confirmed' }],
      { now: futureNow, resource },
    )
    expect(result.type).toBe('user-conflict')
  })

  it('ignores cancelled and rejected bookings', () => {
    expect(check({ startTime: '11:00', endTime: '12:00' }).available).toBe(true)
    expect(check({ startTime: '12:00', endTime: '13:00' }).available).toBe(true)
  })

  it('rejects pax above resource capacity', () => {
    const result = check({ pax: 5 })
    expect(result.type).toBe('capacity')
  })

  it('rejects missing date or time fields', () => {
    const result = check({ startDate: '', date: '', startTime: '' })
    expect(result.type).toBe('missing-fields')
  })

  it('rejects end date-time before start date-time', () => {
    const result = check({ startTime: '11:00', endTime: '10:00' })
    expect(result.type).toBe('invalid-time')
  })

  it('rejects bookings shorter than 30 minutes', () => {
    const result = check({ startTime: '09:00', endTime: '09:20' })
    expect(result.type).toBe('duration-too-short')
  })

  it('rejects bookings longer than 2 hours', () => {
    const result = check({
      startDate: '2026-01-14',
      date: '2026-01-14',
      startTime: '09:00',
      endDate: '2026-01-14',
      endTime: '11:30',
    })
    expect(result.type).toBe('duration-too-long')
  })

  it('rejects inactive resources', () => {
    const result = check({ startTime: '09:00', endTime: '10:00' }, { resource: { ...resource, status: 'inactive' } })
    expect(result.type).toBe('inactive-resource')
  })

  it('generates smart suggestions around conflicts', () => {
    const resources = [
      resource,
      { id: 'room-b', name: 'Study Room B', type: 'Room', capacity: 4, status: 'active' },
    ]
    const suggestions = generateSuggestions(
      {
        userId: 'u-student',
        resourceId: 'room-a',
        date: '2026-01-14',
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
