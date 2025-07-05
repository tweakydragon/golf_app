import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ClubUsageChart from '../ClubUsageChart.vue'
import { waitForAsync } from '../../../test/test-utils'

// Mock Chart.js
const mockChart = {
  destroy: vi.fn(),
  update: vi.fn(),
  render: vi.fn()
}

vi.mock('chart.js', () => ({
  Chart: vi.fn().mockImplementation(() => mockChart),
  CategoryScale: vi.fn(),
  LinearScale: vi.fn(),
  BarElement: vi.fn(),
  Title: vi.fn(),
  Tooltip: vi.fn(),
  Legend: vi.fn(),
  ArcElement: vi.fn(),
  register: vi.fn()
}))

describe('ClubUsageChart', () => {
  let wrapper

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
    vi.clearAllMocks()
  })

  const mockClubCounts = {
    'Driver': 15,
    '7 Iron': 20,
    'Pitching Wedge': 15,
    'Sand Wedge': 10
  }

  const createWrapper = (props = {}) => {
    return mount(ClubUsageChart, {
      props: {
        clubCounts: mockClubCounts,
        ...props
      }
    })
  }

  describe('Component Rendering', () => {
    it('should mount successfully', () => {
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })

    it('should render chart container', () => {
      wrapper = createWrapper()
      expect(wrapper.find('.chart-container').exists()).toBe(true)
    })

    it('should render canvas element', () => {
      wrapper = createWrapper()
      expect(wrapper.find('canvas').exists()).toBe(true)
    })

    it('should have proper container styling', () => {
      wrapper = createWrapper()
      const container = wrapper.find('.chart-container')
      expect(container.element.style.position).toBe('relative')
    })
  })

  describe('Chart Creation', () => {
    it('should create chart with correct data', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      expect(Chart).toHaveBeenCalledWith(
        expect.any(Object), // canvas context
        expect.objectContaining({
          type: 'doughnut',
          data: expect.objectContaining({
            labels: ['Driver', '7 Iron', 'Pitching Wedge', 'Sand Wedge'],
            datasets: expect.arrayContaining([
              expect.objectContaining({
                data: [15, 20, 15, 10]
              })
            ])
          })
        })
      )
    })

    it('should create chart with correct options', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      expect(Chart).toHaveBeenCalledWith(
        expect.any(Object),
        expect.objectContaining({
          options: expect.objectContaining({
            responsive: true,
            maintainAspectRatio: false,
            plugins: expect.objectContaining({
              legend: expect.objectContaining({
                position: 'bottom'
              })
            })
          })
        })
      )
    })

    it('should not create chart with empty data', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper({ clubCounts: {} })
      
      await waitForAsync()
      
      expect(Chart).not.toHaveBeenCalled()
    })

    it('should destroy existing chart before creating new one', async () => {
      wrapper = createWrapper()
      await waitForAsync()
      
      // Trigger a prop change to recreate chart
      await wrapper.setProps({ clubCounts: { 'Driver': 25 } })
      
      expect(mockChart.destroy).toHaveBeenCalled()
    })
  })

  describe('Props Handling', () => {
    it('should accept clubCounts prop', () => {
      wrapper = createWrapper()
      expect(wrapper.props('clubCounts')).toEqual(mockClubCounts)
    })

    it('should use default empty object when clubCounts not provided', () => {
      wrapper = createWrapper({ clubCounts: undefined })
      expect(wrapper.props('clubCounts')).toEqual({})
    })

    it('should handle clubCounts prop changes', async () => {
      wrapper = createWrapper()
      await waitForAsync()
      
      const newClubCounts = { 'Driver': 25, '9 Iron': 15 }
      await wrapper.setProps({ clubCounts: newClubCounts })
      
      expect(wrapper.props('clubCounts')).toEqual(newClubCounts)
    })
  })

  describe('Chart Configuration', () => {
    it('should use doughnut chart type', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      expect(chartCall[1].type).toBe('doughnut')
    })

    it('should have proper color scheme', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const dataset = chartCall[1].data.datasets[0]
      expect(dataset.backgroundColor).toBeDefined()
      expect(Array.isArray(dataset.backgroundColor)).toBe(true)
      expect(dataset.backgroundColor.length).toBeGreaterThan(0)
    })

    it('should have border configuration', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const dataset = chartCall[1].data.datasets[0]
      expect(dataset.borderWidth).toBe(2)
      expect(dataset.borderColor).toBe('#fff')
    })

    it('should have custom tooltip configuration', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const tooltipConfig = chartCall[1].options.plugins.tooltip
      expect(tooltipConfig.callbacks.label).toBeDefined()
      expect(typeof tooltipConfig.callbacks.label).toBe('function')
    })
  })

  describe('Tooltip Functionality', () => {
    it('should format tooltip labels correctly', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const tooltipCallback = chartCall[1].options.plugins.tooltip.callbacks.label
      
      const mockContext = {
        label: 'Driver',
        parsed: 15,
        dataset: {
          data: [15, 20, 15, 10]
        }
      }
      
      const result = tooltipCallback(mockContext)
      expect(result).toBe('Driver: 15 shots (25.0%)')
    })

    it('should calculate percentages correctly', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const tooltipCallback = chartCall[1].options.plugins.tooltip.callbacks.label
      
      const mockContext = {
        label: '7 Iron',
        parsed: 20,
        dataset: {
          data: [15, 20, 15, 10]
        }
      }
      
      const result = tooltipCallback(mockContext)
      expect(result).toBe('7 Iron: 20 shots (33.3%)')
    })
  })

  describe('Legend Configuration', () => {
    it('should position legend at bottom', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const legendConfig = chartCall[1].options.plugins.legend
      expect(legendConfig.position).toBe('bottom')
    })

    it('should use point style for legend', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const legendConfig = chartCall[1].options.plugins.legend
      expect(legendConfig.labels.usePointStyle).toBe(true)
    })

    it('should have proper legend padding', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const legendConfig = chartCall[1].options.plugins.legend
      expect(legendConfig.labels.padding).toBe(20)
    })
  })

  describe('Responsive Design', () => {
    it('should be responsive', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const options = chartCall[1].options
      expect(options.responsive).toBe(true)
    })

    it('should not maintain aspect ratio', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const options = chartCall[1].options
      expect(options.maintainAspectRatio).toBe(false)
    })
  })

  describe('Watchers', () => {
    it('should recreate chart when clubCounts changes', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      expect(Chart).toHaveBeenCalledTimes(1)
      
      await wrapper.setProps({ clubCounts: { 'Driver': 30 } })
      
      expect(Chart).toHaveBeenCalledTimes(2)
    })

    it('should destroy old chart when props change', async () => {
      wrapper = createWrapper()
      await waitForAsync()
      
      await wrapper.setProps({ clubCounts: { 'Driver': 30 } })
      
      expect(mockChart.destroy).toHaveBeenCalled()
    })
  })

  describe('Lifecycle Hooks', () => {
    it('should create chart on mount', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      expect(Chart).toHaveBeenCalled()
    })

    it('should handle canvas not available gracefully', async () => {
      const { Chart } = await import('chart.js')
      
      // Mock a component without canvas
      wrapper = mount(ClubUsageChart, {
        props: { clubCounts: mockClubCounts }
      })
      
      // Remove canvas to simulate unavailable state
      const canvas = wrapper.find('canvas')
      canvas.element.remove()
      
      await waitForAsync()
      
      // Should not throw error
      expect(Chart).not.toHaveBeenCalled()
    })
  })

  describe('Error Handling', () => {
    it('should handle null clubCounts gracefully', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper({ clubCounts: null })
      
      await waitForAsync()
      
      expect(Chart).not.toHaveBeenCalled()
    })

    it('should handle undefined clubCounts gracefully', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper({ clubCounts: undefined })
      
      await waitForAsync()
      
      expect(Chart).not.toHaveBeenCalled()
    })

    it('should handle empty clubCounts', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper({ clubCounts: {} })
      
      await waitForAsync()
      
      expect(Chart).not.toHaveBeenCalled()
    })
  })

  describe('Data Processing', () => {
    it('should extract club names correctly', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const labels = chartCall[1].data.labels
      expect(labels).toEqual(['Driver', '7 Iron', 'Pitching Wedge', 'Sand Wedge'])
    })

    it('should extract club counts correctly', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper()
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      const data = chartCall[1].data.datasets[0].data
      expect(data).toEqual([15, 20, 15, 10])
    })

    it('should handle single club data', async () => {
      const { Chart } = await import('chart.js')
      wrapper = createWrapper({ clubCounts: { 'Driver': 25 } })
      
      await waitForAsync()
      
      const chartCall = Chart.mock.calls[0]
      expect(chartCall[1].data.labels).toEqual(['Driver'])
      expect(chartCall[1].data.datasets[0].data).toEqual([25])
    })
  })
})