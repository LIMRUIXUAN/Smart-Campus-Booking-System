const today = new Date()

const toDateInput = (offsetDays = 0) => {
  const date = new Date(today)
  date.setDate(today.getDate() + offsetDays)
  return date.toISOString().slice(0, 10)
}

export const demoUsers = [
  {
    id: 'u-student',
    name: 'Alya Tan',
    email: 'student@campus.test',
    password: 'password',
    role: 'student',
  },
  {
    id: 'u-admin',
    name: 'Mr. Kumar',
    email: 'admin@campus.test',
    password: 'password',
    role: 'admin',
  },
]

export const demoResources = [
  {
    id: 'r-study-a',
    name: 'Study Room A',
    type: 'Room',
    location: 'Main Library, Floor 2',
    capacity: 4,
    status: 'active',
    description: 'Quiet enclosed space for small group work with a whiteboard and power outlets.',
    features: ['Wi-Fi', 'Whiteboard', 'Power'],
  },
  {
    id: 'r-study-b',
    name: 'Study Room B',
    type: 'Room',
    location: 'Main Library, Floor 2',
    capacity: 4,
    status: 'active',
    description: 'Adjacent study room with similar capacity and a writable wall.',
    features: ['Wi-Fi', 'Whiteboard'],
  },
  {
    id: 'r-discussion-c',
    name: 'Discussion Room C',
    type: 'Room',
    location: 'Learning Commons',
    capacity: 6,
    status: 'active',
    description: 'Flexible room for tutorials, peer discussion, and presentations.',
    features: ['Display', 'Power', 'Movable chairs'],
  },
  {
    id: 'r-lab-1',
    name: 'Computer Lab 1',
    type: 'Lab',
    location: 'CS Building, Level 3',
    capacity: 24,
    status: 'active',
    description: 'Windows lab with development tools and projector support.',
    features: ['Projector', 'PCs', 'Air conditioning'],
  },
  {
    id: 'r-projector',
    name: '4K Projector Pro',
    type: 'Equipment',
    location: 'IT Helpdesk, Building 4',
    capacity: 1,
    status: 'active',
    description: 'Portable 4K projector kit for presentations and student events.',
    features: ['HDMI', 'Carry case'],
  },
  {
    id: 'r-chem-b',
    name: 'Chemistry Lab B',
    type: 'Lab',
    location: 'Science Block, Floor 1',
    capacity: 20,
    status: 'inactive',
    description: 'Temporarily inactive while maintenance checks are completed.',
    features: ['Benches', 'Ventilation'],
  },
]

export const demoBookings = [
  {
    id: 'b-conflict-resource',
    userId: 'u-other',
    userName: 'Nadia Lee',
    resourceId: 'r-study-a',
    date: toDateInput(1),
    startTime: '14:00',
    endTime: '15:00',
    status: 'confirmed',
  },
  {
    id: 'b-conflict-user',
    userId: 'u-student',
    userName: 'Alya Tan',
    resourceId: 'r-lab-1',
    date: toDateInput(2),
    startTime: '09:00',
    endTime: '10:30',
    status: 'confirmed',
  },
  {
    id: 'b-upcoming',
    userId: 'u-student',
    userName: 'Alya Tan',
    resourceId: 'r-study-b',
    date: toDateInput(3),
    startTime: '11:00',
    endTime: '12:00',
    status: 'confirmed',
  },
  {
    id: 'b-completed',
    userId: 'u-student',
    userName: 'Alya Tan',
    resourceId: 'r-lab-1',
    date: toDateInput(-2),
    startTime: '10:00',
    endTime: '11:30',
    status: 'completed',
  },
  {
    id: 'b-cancelled',
    userId: 'u-student',
    userName: 'Alya Tan',
    resourceId: 'r-discussion-c',
    date: toDateInput(-4),
    startTime: '13:00',
    endTime: '14:00',
    status: 'cancelled',
  },
  {
    id: 'b-no-show',
    userId: 'u-other',
    userName: 'Farid Noor',
    resourceId: 'r-projector',
    date: toDateInput(-1),
    startTime: '15:00',
    endTime: '16:00',
    status: 'no-show',
  },
]

export const defaultBookingDate = toDateInput(1)
