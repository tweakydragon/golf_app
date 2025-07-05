import { config } from '@vue/test-utils'
import { vi } from 'vitest'

// Mock Bootstrap to avoid issues with modal/tooltip initialization
global.bootstrap = {
  Modal: vi.fn().mockImplementation(() => ({
    show: vi.fn(),
    hide: vi.fn(),
    dispose: vi.fn()
  })),
  Tooltip: vi.fn().mockImplementation(() => ({
    show: vi.fn(),
    hide: vi.fn(),
    dispose: vi.fn()
  })),
  Tab: vi.fn().mockImplementation(() => ({
    show: vi.fn(),
    hide: vi.fn(),
    dispose: vi.fn()
  }))
}

// Mock window.bootstrap
global.window = global.window || {}
global.window.bootstrap = global.bootstrap

// Mock ResizeObserver
global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn()
}))

// Mock IntersectionObserver
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn()
}))

// Mock canvas getContext for chart testing
HTMLCanvasElement.prototype.getContext = vi.fn().mockReturnValue({
  fillRect: vi.fn(),
  clearRect: vi.fn(),
  getImageData: vi.fn(() => ({ data: new Array(4) })),
  putImageData: vi.fn(),
  createImageData: vi.fn(() => ({ data: new Array(4) })),
  setTransform: vi.fn(),
  drawImage: vi.fn(),
  save: vi.fn(),
  restore: vi.fn(),
  beginPath: vi.fn(),
  moveTo: vi.fn(),
  lineTo: vi.fn(),
  closePath: vi.fn(),
  stroke: vi.fn(),
  fill: vi.fn(),
  measureText: vi.fn(() => ({ width: 0 })),
  arc: vi.fn(),
  scale: vi.fn(),
  rotate: vi.fn(),
  translate: vi.fn(),
  clip: vi.fn(),
  quadraticCurveTo: vi.fn(),
  bezierCurveTo: vi.fn(),
  createLinearGradient: vi.fn(() => ({
    addColorStop: vi.fn()
  })),
  createRadialGradient: vi.fn(() => ({
    addColorStop: vi.fn()
  })),
  setLineDash: vi.fn()
})

// Mock Chart.js
vi.mock('chart.js', () => ({
  Chart: vi.fn().mockImplementation(() => ({
    destroy: vi.fn(),
    update: vi.fn(),
    render: vi.fn()
  })),
  CategoryScale: vi.fn(),
  LinearScale: vi.fn(),
  BarElement: vi.fn(),
  Title: vi.fn(),
  Tooltip: vi.fn(),
  Legend: vi.fn(),
  ArcElement: vi.fn(),
  PointElement: vi.fn(),
  LineElement: vi.fn(),
  register: vi.fn()
}))

// Mock axios
vi.mock('axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

// Global test configuration for Vue Test Utils
config.global.mocks = {
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
}

// Mock vue-router
vi.mock('vue-router', () => ({
  useRoute: () => ({
    params: { id: '1' },
    query: {},
    path: '/session/1',
    name: 'session'
  }),
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn(),
    forward: vi.fn()
  })
}))

// Mock router
config.global.stubs = {
  'router-link': {
    template: '<a><slot /></a>'
  },
  'router-view': {
    template: '<div><slot /></div>'
  }
}

// Set up global plugins that might be used in components
config.global.plugins = []