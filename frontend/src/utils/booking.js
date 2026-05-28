const ACTIVE_STATUSES = new Set(['confirmed'])

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

export const overlaps = (candidate, existing) => {
  if (candidate.date !== existing.date) return false
  return (
    timeToMinutes(candidate.startTime) < timeToMinutes(existing.endTime) &&
    timeToMinutes(candidate.endTime) > timeToMinutes(existing.startTime)
  )
}

export const isPastSlot = (slot, now = new Date()) => {
  const start = new Date(`${slot.date}T${slot.startTime}:00`)
  return start.getTime() < now.getTime()
}

export const validateBookingSlot = (slot, bookings, options = {}) => {
  const now = options.now || new Date()
  const duration = timeToMinutes(slot.endTime) - timeToMinutes(slot.startTime)
  const activeBookings = bookings.filter((booking) => ACTIVE_STATUSES.has(booking.status))

  if (duration <= 0) {
    return { available: false, reason: 'End time must be after start time.', type: 'invalid-time' }
  }

  if (duration > 120) {
    return { available: false, reason: 'Bookings cannot be longer than 2 hours.', type: 'duration' }
  }

  if (isPastSlot(slot, now)) {
    return { available: false, reason: 'Past slots cannot be booked.', type: 'past' }
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
