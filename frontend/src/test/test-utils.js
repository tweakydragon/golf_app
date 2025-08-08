import { mount, shallowMount } from '@vue/test-utils'
import { vi } from 'vitest'

// Mock data generators
export const mockSession = {
  id: 1,
  title: 'Test Session',
  location: 'Test Golf Course',
  uploadDate: '2024-01-01T10:00:00Z',
  sourceType: 'GARMIN_APPROACH'
}

export const mockShot = {
  id: 1,
  shotNumber: 1,
  club: 'Driver',
  carryDistance: 250.5,
  totalDistance: 275.0,
  ballSpeed: 165.2,
  clubHeadSpeed: 110.5,
  launchAngle: 12.5,
  spinRate: 2800,
  deviation: 5.2,
  shotTime: '2024-01-01T10:15:00Z'
}

export const mockStats = {
  totalShots: 50,
  avgCarryDistance: 245.8,
  avgTotalDistance: 268.3,
  avgBallSpeed: 160.5,
  clubCounts: {
    'Driver': 15,
    '7 Iron': 20,
    'Pitching Wedge': 15
  },
  clubStats: {
    'Driver': {
      avgCarry: 250.5,
      avgTotal: 275.0,
      avgBallSpeed: 165.2
    },
    '7 Iron': {
      avgCarry: 155.5,
      avgTotal: 162.0,
      avgBallSpeed: 125.8
    },
    'Pitching Wedge': {
      avgCarry: 110.2,
      avgTotal: 115.5,
      avgBallSpeed: 95.3
    }
  }
}

export const mockShots = [
  {
    ...mockShot,
    id: 1,
    shotNumber: 1,
    club: 'Driver'
  },
  {
    ...mockShot,
    id: 2,
    shotNumber: 2,
    club: '7 Iron',
    carryDistance: 155.5,
    totalDistance: 162.0,
    ballSpeed: 125.8,
    clubHeadSpeed: 85.2
  },
  {
    ...mockShot,
    id: 3,
    shotNumber: 3,
    club: 'Pitching Wedge',
    carryDistance: 110.2,
    totalDistance: 115.5,
    ballSpeed: 95.3,
    clubHeadSpeed: 68.5
  }
]

// Mock axios responses
export const mockAxiosGet = (url) => {
  if (url.includes('/sessions/1')) {
    return Promise.resolve({ data: mockSession })
  } else if (url.includes('/shots')) {
    return Promise.resolve({ data: mockShots })
  } else if (url.includes('/stats')) {
    return Promise.resolve({ data: mockStats })
  }
  return Promise.reject(new Error('Unknown API endpoint'))
}

// Helper function to create a component wrapper with common options
export const createWrapper = (component, options = {}) => {
  return mount(component, {
    global: {
      mocks: {
        $router: {
          push: vi.fn(),
          replace: vi.fn(),
          go: vi.fn(),
          back: vi.fn(),
          forward: vi.fn()
        },
        $route: {
          params: { id: '1' },
          query: {},
          path: '/session/1',
          name: 'session'
        }
      },
      stubs: {
        'router-link': {
          template: '<a><slot /></a>',
          props: ['to']
        },
        'router-view': {
          template: '<div><slot /></div>'
        }
      }
    },
    ...options
  })
}

// Helper function to create a shallow wrapper
export const createShallowWrapper = (component, options = {}) => {
  return shallowMount(component, {
    global: {
      mocks: {
        $router: {
          push: vi.fn(),
          replace: vi.fn(),
          go: vi.fn(),
          back: vi.fn(),
          forward: vi.fn()
        },
        $route: {
          params: { id: '1' },
          query: {},
          path: '/session/1',
          name: 'session'
        }
      },
      stubs: {
        'router-link': {
          template: '<a><slot /></a>',
          props: ['to']
        },
        'router-view': {
          template: '<div><slot /></div>'
        }
      }
    },
    ...options
  })
}

// Helper to wait for async operations
export const waitForAsync = (ms = 0) => {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// Helper to flush promises
export const flushPromises = () => {
  return new Promise(resolve => setTimeout(resolve))
}

// Mock chart component
export const mockChartComponent = {
  template: '<div class="mock-chart"></div>',
  props: ['shots', 'clubCounts', 'clubStats']
}

// Mock router for testing
export const mockRouter = {
  push: vi.fn(),
  replace: vi.fn(),
  go: vi.fn(),
  back: vi.fn(),
  forward: vi.fn(),
  currentRoute: {
    value: {
      params: { id: '1' },
      query: {},
      path: '/session/1',
      name: 'session'
    }
  }
}

// Mock route for testing
export const mockRoute = {
  params: { id: '1' },
  query: {},
  path: '/session/1',
  name: 'session'
}