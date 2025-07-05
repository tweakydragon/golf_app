import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import axios from 'axios'
import SessionView from '../SessionView.vue'
import { mockSession, mockShots, mockStats, mockAxiosGet, waitForAsync, flushPromises } from '../../test/test-utils'

// Mock axios
vi.mock('axios')

// Mock chart components
vi.mock('../charts/ClubUsageChart.vue', () => ({
  default: {
    name: 'ClubUsageChart',
    template: '<div class="club-usage-chart" data-testid="club-usage-chart"></div>',
    props: ['clubCounts']
  }
}))

vi.mock('../charts/ClubDistanceChart.vue', () => ({
  default: {
    name: 'ClubDistanceChart',
    template: '<div class="club-distance-chart" data-testid="club-distance-chart"></div>',
    props: ['clubStats']
  }
}))

vi.mock('../charts/DistanceProgressionChart.vue', () => ({
  default: {
    name: 'DistanceProgressionChart',
    template: '<div class="distance-progression-chart" data-testid="distance-progression-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/CarryVsTotalChart.vue', () => ({
  default: {
    name: 'CarryVsTotalChart',
    template: '<div class="carry-vs-total-chart" data-testid="carry-vs-total-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/DistanceDistributionChart.vue', () => ({
  default: {
    name: 'DistanceDistributionChart',
    template: '<div class="distance-distribution-chart" data-testid="distance-distribution-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/ShotDispersionChart.vue', () => ({
  default: {
    name: 'ShotDispersionChart',
    template: '<div class="shot-dispersion-chart" data-testid="shot-dispersion-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/BallVsClubSpeedChart.vue', () => ({
  default: {
    name: 'BallVsClubSpeedChart',
    template: '<div class="ball-vs-club-speed-chart" data-testid="ball-vs-club-speed-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/LaunchAngleSpinChart.vue', () => ({
  default: {
    name: 'LaunchAngleSpinChart',
    template: '<div class="launch-angle-spin-chart" data-testid="launch-angle-spin-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/SmashFactorChart.vue', () => ({
  default: {
    name: 'SmashFactorChart',
    template: '<div class="smash-factor-chart" data-testid="smash-factor-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/DescentAngleChart.vue', () => ({
  default: {
    name: 'DescentAngleChart',
    template: '<div class="descent-angle-chart" data-testid="descent-angle-chart"></div>',
    props: ['shots']
  }
}))

vi.mock('../charts/AccuracyStatsCard.vue', () => ({
  default: {
    name: 'AccuracyStatsCard',
    template: '<div class="accuracy-stats-card" data-testid="accuracy-stats-card"></div>',
    props: ['shots']
  }
}))

vi.mock('../ShotDetailModal.vue', () => ({
  default: {
    name: 'ShotDetailModal',
    template: '<div class="shot-detail-modal" data-testid="shot-detail-modal"></div>',
    props: ['shot', 'modalId']
  }
}))

vi.mock('../SessionOverviewVisual.vue', () => ({
  default: {
    name: 'SessionOverviewVisual',
    template: '<div class="session-overview-visual" data-testid="session-overview-visual"></div>',
    props: ['shots', 'session', 'highlightedShotIndex', 'isMinimized'],
    emits: ['shot-highlight']
  }
}))

describe('SessionView', () => {
  let wrapper
  let mockAxios

  beforeEach(() => {
    mockAxios = axios
    mockAxios.get = vi.fn()
    vi.clearAllMocks()
  })

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const createWrapper = (routeParams = { id: '1' }) => {
    return mount(SessionView, {
      global: {
        mocks: {
          $route: {
            params: routeParams
          }
        },
        stubs: {
          'router-link': {
            template: '<a><slot /></a>',
            props: ['to']
          }
        },
        provide: {
          [Symbol.for('VueRouterLocationKey')]: {
            params: routeParams,
            query: {},
            path: `/session/${routeParams.id}`,
            name: 'session'
          }
        }
      }
    })
  }

  describe('Component Lifecycle', () => {
    it('should mount successfully', () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })

    it('should show loading state initially', () => {
      mockAxios.get.mockImplementation(() => new Promise(() => {})) // Never resolves
      wrapper = createWrapper()
      expect(wrapper.find('[data-testid="loading-spinner"]').exists() || 
             wrapper.text().includes('Loading session data')).toBe(true)
    })

    it('should fetch session data on mount', async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      
      await flushPromises()
      
      expect(mockAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/sessions/1')
      expect(mockAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/sessions/1/shots')
      expect(mockAxios.get).toHaveBeenCalledWith('http://localhost:8080/api/sessions/1/stats')
    })
  })

  describe('Data Loading', () => {
    it('should display session data when loaded', async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      
      await flushPromises()
      
      expect(wrapper.text()).toContain('Test Session')
      expect(wrapper.text()).toContain('Test Golf Course')
    })

    it('should handle API errors gracefully', async () => {
      mockAxios.get.mockRejectedValue(new Error('API Error'))
      wrapper = createWrapper()
      
      await flushPromises()
      
      expect(wrapper.text()).toContain('Failed to load session data')
    })

    it('should display error state with back button', async () => {
      mockAxios.get.mockRejectedValue(new Error('API Error'))
      wrapper = createWrapper()
      
      await flushPromises()
      
      const backButton = wrapper.find('a[href="/"]')
      expect(backButton.exists()).toBe(true)
      expect(backButton.text()).toContain('Back to Sessions')
    })
  })

  describe('Session Statistics', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should display total shots count', () => {
      expect(wrapper.text()).toContain('50') // Total shots from mockStats
    })

    it('should display average carry distance', () => {
      expect(wrapper.text()).toContain('245.8') // Avg carry from mockStats
    })

    it('should display average total distance', () => {
      expect(wrapper.text()).toContain('268.3') // Avg total from mockStats
    })

    it('should display average ball speed', () => {
      expect(wrapper.text()).toContain('160.5') // Avg ball speed from mockStats
    })

    it('should show session insights', () => {
      expect(wrapper.text()).toContain('Session Insights')
      expect(wrapper.text()).toContain('Best Shot')
      expect(wrapper.text()).toContain('Consistency')
    })
  })

  describe('Chart Components', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should render club usage chart', () => {
      expect(wrapper.find('[data-testid="club-usage-chart"]').exists()).toBe(true)
    })

    it('should render club distance chart', () => {
      expect(wrapper.find('[data-testid="club-distance-chart"]').exists()).toBe(true)
    })

    it('should render distance progression chart', () => {
      expect(wrapper.find('[data-testid="distance-progression-chart"]').exists()).toBe(true)
    })

    it('should render carry vs total chart', () => {
      expect(wrapper.find('[data-testid="carry-vs-total-chart"]').exists()).toBe(true)
    })

    it('should render shot dispersion chart', () => {
      expect(wrapper.find('[data-testid="shot-dispersion-chart"]').exists()).toBe(true)
    })

    it('should render accuracy stats card', () => {
      expect(wrapper.find('[data-testid="accuracy-stats-card"]').exists()).toBe(true)
    })
  })

  describe('Tab Navigation', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should have overview tab active by default', () => {
      const overviewTab = wrapper.find('#overview-tab')
      expect(overviewTab.classes()).toContain('active')
    })

    it('should have all required tabs', () => {
      expect(wrapper.find('#overview-tab').exists()).toBe(true)
      expect(wrapper.find('#distance-tab').exists()).toBe(true)
      expect(wrapper.find('#accuracy-tab').exists()).toBe(true)
      expect(wrapper.find('#performance-tab').exists()).toBe(true)
      expect(wrapper.find('#shots-tab').exists()).toBe(true)
    })

    it('should have correct tab content panels', () => {
      expect(wrapper.find('#overview').exists()).toBe(true)
      expect(wrapper.find('#distance').exists()).toBe(true)
      expect(wrapper.find('#accuracy').exists()).toBe(true)
      expect(wrapper.find('#performance').exists()).toBe(true)
      expect(wrapper.find('#shots').exists()).toBe(true)
    })
  })

  describe('Shot Table', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should display shot table with correct headers', () => {
      const table = wrapper.find('table')
      expect(table.exists()).toBe(true)
      
      expect(table.text()).toContain('Shot #')
      expect(table.text()).toContain('Club')
      expect(table.text()).toContain('Carry')
      expect(table.text()).toContain('Total')
      expect(table.text()).toContain('Ball Speed')
      expect(table.text()).toContain('Launch Angle')
    })

    it('should display shot data in table rows', () => {
      const tableRows = wrapper.findAll('tbody tr')
      expect(tableRows.length).toBe(3) // mockShots has 3 shots
      
      // Check first row data
      expect(tableRows[0].text()).toContain('1') // Shot number
      expect(tableRows[0].text()).toContain('Driver') // Club
      expect(tableRows[0].text()).toContain('250.5') // Carry distance
    })

    it('should have club filter dropdown', () => {
      const clubFilter = wrapper.find('select')
      expect(clubFilter.exists()).toBe(true)
      expect(clubFilter.text()).toContain('All Clubs')
      expect(clubFilter.text()).toContain('Driver')
    })
  })

  describe('Shot Interactions', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should handle shot row click', async () => {
      const shotRow = wrapper.find('tbody tr')
      await shotRow.trigger('click')
      
      // Should open shot detail modal
      expect(wrapper.find('[data-testid="shot-detail-modal"]').exists()).toBe(true)
    })

    it('should handle shot hover', async () => {
      const shotRow = wrapper.find('tbody tr')
      await shotRow.trigger('mouseenter')
      
      // Should highlight the shot
      expect(wrapper.vm.highlightedShotIndex).toBe(0)
    })

    it('should handle shot leave', async () => {
      const shotRow = wrapper.find('tbody tr')
      await shotRow.trigger('mouseleave')
      
      // Should remove highlight
      expect(wrapper.vm.highlightedShotIndex).toBe(null)
    })
  })

  describe('Club Filtering', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should filter shots by club', async () => {
      const clubFilter = wrapper.find('select')
      await clubFilter.setValue('Driver')
      
      // Should only show Driver shots
      const filteredRows = wrapper.findAll('tbody tr')
      expect(filteredRows.length).toBe(1)
      expect(filteredRows[0].text()).toContain('Driver')
    })

    it('should show all shots when no filter is applied', async () => {
      const clubFilter = wrapper.find('select')
      await clubFilter.setValue('')
      
      // Should show all shots
      const filteredRows = wrapper.findAll('tbody tr')
      expect(filteredRows.length).toBe(3)
    })
  })

  describe('Computed Properties', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should calculate hasAwesomeGolfData correctly', () => {
      // Mock session doesn't have AWESOME_GOLF source type
      expect(wrapper.vm.hasAwesomeGolfData).toBe(false)
    })

    it('should calculate bestShot correctly', () => {
      const bestShot = wrapper.vm.bestShot
      expect(bestShot.totalDistance).toBe(275.0) // Driver shot has highest distance
    })

    it('should calculate distanceConsistency', () => {
      const consistency = wrapper.vm.distanceConsistency
      expect(consistency).toBeDefined()
      expect(typeof consistency).toBe('string')
    })

    it('should calculate averageSmashFactor', () => {
      const smashFactor = wrapper.vm.averageSmashFactor
      expect(smashFactor).toBeDefined()
    })

    it('should calculate dataQualityText', () => {
      const dataQuality = wrapper.vm.dataQualityText
      expect(dataQuality).toBeDefined()
      expect(typeof dataQuality).toBe('string')
    })
  })

  describe('Formatting Functions', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should format dates correctly', () => {
      const formatted = wrapper.vm.formatDate('2024-01-01T10:00:00Z')
      expect(formatted).toBeDefined()
      expect(typeof formatted).toBe('string')
      expect(formatted).toContain('Jan') // Should contain month abbreviation
    })

    it('should format numbers correctly', () => {
      const formatted = wrapper.vm.formatNumber(2800)
      expect(formatted).toBe('2,800')
    })

    it('should format source type correctly', () => {
      const formatted = wrapper.vm.formatSourceType('GARMIN_APPROACH')
      expect(formatted).toBe('Garmin Approach')
    })

    it('should handle null/undefined values in formatNumber', () => {
      const formatted = wrapper.vm.formatNumber(null)
      expect(formatted).toBe('')
    })
  })

  describe('Responsive Design', () => {
    beforeEach(async () => {
      mockAxios.get.mockImplementation(mockAxiosGet)
      wrapper = createWrapper()
      await flushPromises()
    })

    it('should have responsive container classes', () => {
      expect(wrapper.find('.session-view-container').exists()).toBe(true)
    })

    it('should have responsive grid classes', () => {
      const gridItems = wrapper.findAll('.col-md-3, .col-md-4, .col-md-6, .col-lg-6, .col-lg-8')
      expect(gridItems.length).toBeGreaterThan(0)
    })

    it('should have responsive table', () => {
      const responsiveTable = wrapper.find('.table-responsive')
      expect(responsiveTable.exists()).toBe(true)
    })
  })

  describe('Error Handling', () => {
    it('should handle network errors', async () => {
      mockAxios.get.mockRejectedValue(new Error('Network Error'))
      wrapper = createWrapper()
      
      await flushPromises()
      
      expect(wrapper.text()).toContain('Failed to load session data')
    })

    it('should handle invalid session ID', async () => {
      mockAxios.get.mockRejectedValue(new Error('Session not found'))
      wrapper = createWrapper({ id: 'invalid' })
      
      await flushPromises()
      
      expect(wrapper.text()).toContain('Failed to load session data')
    })

    it('should handle empty data gracefully', async () => {
      mockAxios.get.mockImplementation((url) => {
        if (url.includes('/sessions/1')) {
          return Promise.resolve({ data: mockSession })
        } else if (url.includes('/shots')) {
          return Promise.resolve({ data: [] })
        } else if (url.includes('/stats')) {
          return Promise.resolve({ data: { totalShots: 0 } })
        }
      })
      
      wrapper = createWrapper()
      await flushPromises()
      
      expect(wrapper.text()).toContain('Test Session')
      expect(wrapper.text()).toContain('0') // Total shots should be 0
    })
  })
})