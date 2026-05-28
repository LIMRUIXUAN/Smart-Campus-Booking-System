export const BLOCKING_STATUSES = new Set(['confirmed'])
const MIN_DURATION_MINUTES = 30
const MAX_DURATION_MINUTES = 2 * 60

export const timeToMinutes = (time) => {
  const [hours, minutes] = time.split(':').map(Number)
  return hours * 60 + minutes
}

export const minutesToTime = (minutes) => {
  const normalized = Math.max(0, Math.min(minutes, 23 * 60 + 59))
  const hours = String(Math.floor(normalized / 60)).padStart(2, '0')
  const mins = String(normalized % 60).padStart(2, '0')
  return `${hours}:${mins}`
}

export const formatTime = (time) => {
  const minutes = timeToMinutes(time)
  const hours24 = Math.floor(minutes / 60)
  const mins = String(minutes % 60).padStart(2, '0')
  const suffix = hours24 >= 12 ? 'PM' : 'AM'
  const hours12 = hours24 % 12 || 12
  return `${hours12}:${mins} ${suffix}`
}

export const getSlotDates = (slot) => {
  const startDate = slot.startDate || slot.date
  const endDate = slot.endDate || slot.date
  const startTime = slot.startTime
  const endTime = slot.endTime

  return {
    startDate,
    endDate,
    startTime,
    endTime,
    startDateTime: slot.startDateTime || (startDate && startTime ? `${startDate}T${startTime}:00` : ''),
    endDateTime: slot.endDateTime || (endDate && endTime ? `${endDate}T${endTime}:00` : ''),
  }
}

export const toDateTimeRange = (slot) => {
  const dates = getSlotDates(slot)
  return {
    ...dates,
    start: dates.startDateTime ? new Date(dates.startDateTime) : null,
    end: dates.endDateTime ? new Date(dates.endDateTime) : null,
  }
}

const isValidDate = (date) => date instanceof Date && !Number.isNaN(date.getTime())

const durationMinutes = (start, end) => (end.getTime() - start.getTime()) / (60 * 1000)

export const overlaps = (candidate, existing) => {
  const candidateRange = toDateTimeRange(candidate)
  const existingRange = toDateTimeRange(existing)

  if (
    !isValidDate(candidateRange.start) ||
    !isValidDate(candidateRange.end) ||
    !isValidDate(existingRange.start) ||
    !isValidDate(existingRange.end)
  ) {
    return false
  }

  return candidateRange.start < existingRange.end && candidateRange.end > existingRange.start
}

export const isPastSlot = (slot, now = new Date()) => {
  const { start } = toDateTimeRange(slot)
  return isValidDate(start) && start.getTime() < now.getTime()
}

export const validateBookingSlot = (slot, bookings, options = {}) => {
  const now = options.now || new Date()
  const resource = options.resource
  const range = toDateTimeRange(slot)
  const capacity = resource?.capacity ?? slot.resourceCapacity ?? slot.capacity
  const pax = Number(slot.pax || 0)
  const activeBookings = bookings.filter((booking) => BLOCKING_STATUSES.has(booking.status))

  if (!range.startDate || !range.startTime || !range.endDate || !range.endTime) {
    return { available: false, reason: 'Please select a start date, start time, end date, and end time.', type: 'missing-fields' }
  }

  if (!isValidDate(range.start) || !isValidDate(range.end)) {
    return { available: false, reason: 'Please enter a valid booking date and time.', type: 'validation-error' }
  }

  const duration = durationMinutes(range.start, range.end)

  if (duration <= 0) {
    return { available: false, reason: 'End date and time must be after the start date and time.', type: 'invalid-time' }
  }

  if (duration < MIN_DURATION_MINUTES) {
    return { available: false, reason: 'Bookings must be at least 30 minutes long.', type: 'duration-too-short' }
  }

  if (duration > MAX_DURATION_MINUTES) {
    return { available: false, reason: 'Bookings cannot be longer than 2 hours.', type: 'duration-too-long' }
  }

  if (isPastSlot(slot, now)) {
    return { available: false, reason: 'Past slots cannot be booked.', type: 'past' }
  }

  if (resource?.status === 'inactive') {
    return {
      available: false,
      reason: 'Inactive resources cannot be booked.',
      type: 'inactive-resource',
    }
  }

  if (capacity && pax > Number(capacity)) {
    return {
      available: false,
      reason: `Requested pax exceeds the resource capacity of ${capacity}.`,
      type: 'capacity',
    }
  }

  const resourceConflict = activeBookings.find(
    (booking) => booking.resourceId === slot.resourceId && overlaps(slot, booking),
  )
  if (resourceConflict) {
    return {
      available: false,
      reason: 'This resource is already booked for the selected time.',
      type: 'resource-conflict',
      conflict: resourceConflict,
    }
  }

  const userConflict = activeBookings.find(
    (booking) => booking.userId === slot.userId && overlaps(slot, booking),
  )
  if (userConflict) {
    return {
      available: false,
      reason: 'You already have another booking during the selected time.',
      type: 'user-conflict',
      conflict: userConflict,
    }
  }

  return { available: true, reason: 'This slot is available.', type: 'available' }
}

const isSlotAvailable = (slot, bookings, now) => validateBookingSlot(slot, bookings, { now }).available

export const generateSuggestions = (slot, resources, bookings, now = new Date()) => {
  const selectedResource = resources.find((resource) => resource.id === slot.resourceId)
  const duration = timeToMinutes(slot.endTime) - timeToMinutes(slot.startTime)
  const suggestions = []

  const pushIfAvailable = (candidate) => {
    if (
      candidate.startTime >= '08:00' &&
      candidate.endTime <= '20:00' &&
      isSlotAvailable(candidate, bookings, now) &&
      !suggestions.some(
        (item) =>
          item.resourceId === candidate.resourceId &&
          item.date === candidate.date &&
          item.startTime === candidate.startTime,
      )
    ) {
      const resource = resources.find((item) => item.id === candidate.resourceId)
      suggestions.push({
        ...candidate,
        resourceName: resource?.name,
        location: resource?.location,
        capacity: resource?.capacity,
      })
    }
  }

  for (const offset of [60, -60, 120]) {
    const start = timeToMinutes(slot.startTime) + offset
    pushIfAvailable({
      ...slot,
      startTime: minutesToTime(start),
      endTime: minutesToTime(start + duration),
      label: offset > 0 ? `+${offset / 60} hr later` : `${Math.abs(offset / 60)} hr earlier`,
      reason: 'Same resource at a nearby available time',
    })
  }

  resources
    .filter(
      (resource) =>
        resource.id !== slot.resourceId &&
        resource.status === 'active' &&
        resource.type === selectedResource?.type &&
        resource.capacity >= (selectedResource?.capacity || 0),
    )
    .slice(0, 2)
    .forEach((resource, index) => {
      pushIfAvailable({
        ...slot,
        resourceId: resource.id,
        label: index === 0 ? 'Same time' : 'Alternative',
        reason: index === 0 ? 'Similar resource available at your selected time' : 'Similar resource nearby',
      })
    })

  return suggestions.slice(0, 3)
}

export const formatDate = (date) =>
  new Intl.DateTimeFormat('en-MY', { month: 'short', day: 'numeric', year: 'numeric' }).format(
    new Date(`${date}T00:00:00`),
  )
