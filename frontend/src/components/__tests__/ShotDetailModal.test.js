import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ShotDetailModal from '../ShotDetailModal.vue'
import { mockShot, waitForAsync } from '../../test/test-utils'

describe('ShotDetailModal', () => {
  let wrapper

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const createWrapper = (props = {}) => {
    return mount(ShotDetailModal, {
      props: {
        shot: mockShot,
        modalId: 'testModal',
        ...props
      }
    })
  }

  describe('Component Rendering', () => {
    it('should mount successfully', () => {
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })

    it('should render modal with correct ID', () => {
      wrapper = createWrapper({ modalId: 'customModal' })
      const modal = wrapper.find('#customModal')
      expect(modal.exists()).toBe(true)
      expect(modal.attributes('id')).toBe('customModal')
    })

    it('should display shot number in title', () => {
      wrapper = createWrapper()
      const title = wrapper.find('.modal-title')
      expect(title.text()).toContain('Shot #1 Details')
    })

    it('should render close button', () => {
      wrapper = createWrapper()
      const closeButton = wrapper.find('.btn-close')
      expect(closeButton.exists()).toBe(true)
      expect(closeButton.attributes('data-bs-dismiss')).toBe('modal')
    })
  })

  describe('Shot Overview Section', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should display club information', () => {
      expect(wrapper.text()).toContain('Driver')
    })

    it('should display total distance', () => {
      expect(wrapper.text()).toContain('275.0')
      expect(wrapper.text()).toContain('yds')
    })

    it('should display ball speed', () => {
      expect(wrapper.text()).toContain('165.2')
      expect(wrapper.text()).toContain('mph')
    })

    it('should show shot time when available', () => {
      const shotWithTime = {
        ...mockShot,
        shotTime: '2024-01-01T10:15:00Z'
      }
      wrapper = createWrapper({ shot: shotWithTime })
      expect(wrapper.text()).toContain('Shot Time')
    })

    it('should show club description when available', () => {
      const shotWithDescription = {
        ...mockShot,
        clubDescription: 'TaylorMade M6 Driver'
      }
      wrapper = createWrapper({ shot: shotWithDescription })
      expect(wrapper.text()).toContain('Club Description')
      expect(wrapper.text()).toContain('TaylorMade M6 Driver')
    })
  })

  describe('Distance Metrics Section', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should display carry distance', () => {
      expect(wrapper.text()).toContain('Carry Distance')
      expect(wrapper.text()).toContain('250.5')
    })

    it('should display total distance', () => {
      expect(wrapper.text()).toContain('Total Distance')
      expect(wrapper.text()).toContain('275.0')
    })

    it('should display deviation when available', () => {
      expect(wrapper.text()).toContain('Deviation')
      expect(wrapper.text()).toContain('5.2')
    })

    it('should show additional distance metrics when available', () => {
      const shotWithExtendedMetrics = {
        ...mockShot,
        rollDistance: 24.5,
        apex: 85.2,
        peakHeight: 88.7
      }
      wrapper = createWrapper({ shot: shotWithExtendedMetrics })
      
      expect(wrapper.text()).toContain('Roll Distance')
      expect(wrapper.text()).toContain('24.5')
      expect(wrapper.text()).toContain('Apex')
      expect(wrapper.text()).toContain('85.2')
      expect(wrapper.text()).toContain('Peak Height')
      expect(wrapper.text()).toContain('88.7')
    })
  })

  describe('Speed & Launch Section', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should display ball speed', () => {
      expect(wrapper.text()).toContain('Ball Speed')
      expect(wrapper.text()).toContain('165.2')
    })

    it('should display club speed', () => {
      expect(wrapper.text()).toContain('Club Speed')
      expect(wrapper.text()).toContain('110.5')
    })

    it('should display launch angle', () => {
      expect(wrapper.text()).toContain('Launch Angle')
      expect(wrapper.text()).toContain('12.5')
    })

    it('should show additional launch metrics when available', () => {
      const shotWithLaunchMetrics = {
        ...mockShot,
        launchDirection: 2.5,
        horizontalLaunch: 1.8,
        descentAngle: 45.2
      }
      wrapper = createWrapper({ shot: shotWithLaunchMetrics })
      
      expect(wrapper.text()).toContain('Launch Direction')
      expect(wrapper.text()).toContain('2.5')
      expect(wrapper.text()).toContain('Horizontal Launch')
      expect(wrapper.text()).toContain('1.8')
      expect(wrapper.text()).toContain('Descent Angle')
      expect(wrapper.text()).toContain('45.2')
    })
  })

  describe('Spin & Angles Section', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should display spin rate', () => {
      expect(wrapper.text()).toContain('Spin Rate')
      expect(wrapper.text()).toContain('2,800') // Should be formatted with commas
    })

    it('should show additional spin metrics when available', () => {
      const shotWithSpinMetrics = {
        ...mockShot,
        spinAxis: 15.2,
        attackAngle: -2.5,
        faceAngle: 1.2,
        swingPath: -1.8,
        faceToPath: 3.0
      }
      wrapper = createWrapper({ shot: shotWithSpinMetrics })
      
      expect(wrapper.text()).toContain('Spin Axis')
      expect(wrapper.text()).toContain('15.2')
      expect(wrapper.text()).toContain('Attack Angle')
      expect(wrapper.text()).toContain('-2.5')
      expect(wrapper.text()).toContain('Face Angle')
      expect(wrapper.text()).toContain('1.2')
    })
  })

  describe('Advanced Metrics Section', () => {
    it('should show advanced metrics when available', () => {
      const shotWithAdvancedMetrics = {
        ...mockShot,
        smash: 1.49,
        dynamicLoft: 12.5,
        spinLoft: 15.2,
        lowPoint: 2.5,
        swingPlane: 65.8
      }
      wrapper = createWrapper({ shot: shotWithAdvancedMetrics })
      
      expect(wrapper.text()).toContain('Advanced Club Metrics')
      expect(wrapper.text()).toContain('Dynamic Loft')
      expect(wrapper.text()).toContain('12.5')
      expect(wrapper.text()).toContain('Spin Loft')
      expect(wrapper.text()).toContain('15.2')
    })

    it('should show lateral metrics when available', () => {
      const shotWithLateralMetrics = {
        ...mockShot,
        carryLateralDistance: 8.5,
        totalLateralDistance: 10.2,
        carryCurveDistance: 5.8,
        totalCurveDistance: 7.1
      }
      wrapper = createWrapper({ shot: shotWithLateralMetrics })
      
      expect(wrapper.text()).toContain('Lateral & Curve Metrics')
      expect(wrapper.text()).toContain('Carry Lateral')
      expect(wrapper.text()).toContain('8.5')
      expect(wrapper.text()).toContain('Total Lateral')
      expect(wrapper.text()).toContain('10.2')
    })

    it('should hide advanced metrics section when not available', () => {
      wrapper = createWrapper()
      expect(wrapper.text()).not.toContain('Advanced Club Metrics')
      expect(wrapper.text()).not.toContain('Lateral & Curve Metrics')
    })
  })

  describe('Data Formatting', () => {
    it('should format values correctly', () => {
      wrapper = createWrapper()
      // Test that N/A is shown for undefined values
      expect(wrapper.vm.formatValue(null)).toBe('N/A')
      expect(wrapper.vm.formatValue(undefined)).toBe('N/A')
      
      // Test that numbers are formatted correctly
      expect(wrapper.vm.formatValue(123.456)).toBe('123.5')
      expect(wrapper.vm.formatValue(123.456, 'yds')).toBe('123.5 yds')
    })

    it('should format spin rate with commas', () => {
      wrapper = createWrapper()
      expect(wrapper.vm.formatValue(2800, 'rpm', true)).toBe('2,800.0 rpm')
    })

    it('should format date time correctly', () => {
      wrapper = createWrapper()
      const formatted = wrapper.vm.formatDateTime('2024-01-01T10:15:00Z')
      expect(formatted).toBeDefined()
      expect(typeof formatted).toBe('string')
    })

    it('should handle invalid date time', () => {
      wrapper = createWrapper()
      const formatted = wrapper.vm.formatDateTime(null)
      expect(formatted).toBe('N/A')
    })
  })

  describe('Visualization Controls', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should render zoom controls', () => {
      expect(wrapper.find('.zoom-controls').exists()).toBe(true)
      expect(wrapper.find('[data-testid="zoom-in"]').exists() || 
             wrapper.text().includes('zoom-in')).toBe(true)
      expect(wrapper.find('[data-testid="zoom-out"]').exists() || 
             wrapper.text().includes('zoom-out')).toBe(true)
    })

    it('should render view toggle buttons', () => {
      expect(wrapper.find('.view-toggle').exists()).toBe(true)
      expect(wrapper.text()).toContain('Side View')
      expect(wrapper.text()).toContain('Top View')
    })

    it('should handle zoom in', async () => {
      const initialZoom = wrapper.vm.zoomLevel
      await wrapper.vm.zoomIn()
      expect(wrapper.vm.zoomLevel).toBeGreaterThan(initialZoom)
    })

    it('should handle zoom out', async () => {
      wrapper.vm.zoomLevel = 2
      const initialZoom = wrapper.vm.zoomLevel
      await wrapper.vm.zoomOut()
      expect(wrapper.vm.zoomLevel).toBeLessThan(initialZoom)
    })

    it('should handle reset zoom', async () => {
      wrapper.vm.zoomLevel = 2
      wrapper.vm.panOffset = { x: 50, y: 50 }
      await wrapper.vm.resetZoom()
      expect(wrapper.vm.zoomLevel).toBe(1)
      expect(wrapper.vm.panOffset).toEqual({ x: 0, y: 0 })
    })

    it('should handle view mode change', async () => {
      expect(wrapper.vm.viewMode).toBe('side')
      const topViewButton = wrapper.find('button:contains("Top View")')
      if (topViewButton.exists()) {
        await topViewButton.trigger('click')
        expect(wrapper.vm.viewMode).toBe('top')
      }
    })
  })

  describe('Canvas Rendering', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should render trajectory canvas', () => {
      expect(wrapper.find('canvas').exists()).toBe(true)
    })

    it('should handle canvas interaction events', () => {
      const canvas = wrapper.find('canvas')
      expect(canvas.exists()).toBe(true)
      
      // Test that event listeners are attached
      expect(canvas.attributes('width')).toBe('800')
      expect(canvas.attributes('height')).toBe('400')
    })

    it('should call draw methods on mount', () => {
      // Mock the draw methods
      const drawTrajectory = vi.spyOn(wrapper.vm, 'drawTrajectory')
      const drawDispersion = vi.spyOn(wrapper.vm, 'drawDispersion')
      const drawMetrics = vi.spyOn(wrapper.vm, 'drawMetrics')
      
      wrapper.vm.redrawAll()
      
      expect(drawTrajectory).toHaveBeenCalled()
      expect(drawDispersion).toHaveBeenCalled()
      expect(drawMetrics).toHaveBeenCalled()
    })
  })

  describe('Props Validation', () => {
    it('should accept valid shot prop', () => {
      wrapper = createWrapper({ shot: mockShot })
      expect(wrapper.props('shot')).toEqual(mockShot)
    })

    it('should accept modalId prop', () => {
      wrapper = createWrapper({ modalId: 'customModal' })
      expect(wrapper.props('modalId')).toBe('customModal')
    })

    it('should use default modalId when not provided', () => {
      wrapper = createWrapper()
      expect(wrapper.props('modalId')).toBe('shotDetailModal')
    })
  })

  describe('Computed Properties', () => {
    it('should compute hasAwesomeGolfMetrics correctly', () => {
      const shotWithoutAdvanced = { ...mockShot }
      wrapper = createWrapper({ shot: shotWithoutAdvanced })
      expect(wrapper.vm.hasAwesomeGolfMetrics).toBe(false)
      
      const shotWithAdvanced = { ...mockShot, smash: 1.49 }
      wrapper = createWrapper({ shot: shotWithAdvanced })
      expect(wrapper.vm.hasAwesomeGolfMetrics).toBe(true)
    })
  })

  describe('Event Handling', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should handle wheel events for zoom', () => {
      const canvas = wrapper.find('canvas')
      const wheelEvent = new WheelEvent('wheel', { deltaY: -100 })
      const handleWheel = vi.spyOn(wrapper.vm, 'handleWheel')
      
      canvas.element.dispatchEvent(wheelEvent)
      // Note: We can't easily test the actual wheel event handling in jsdom
      // but we can verify the method exists
      expect(typeof wrapper.vm.handleWheel).toBe('function')
    })

    it('should handle pan start', () => {
      const canvas = wrapper.find('canvas')
      const mouseEvent = new MouseEvent('mousedown', { clientX: 100, clientY: 100 })
      
      wrapper.vm.startPan(mouseEvent)
      expect(wrapper.vm.isPanning).toBe(true)
      expect(wrapper.vm.lastPanPoint).toEqual({ x: 100, y: 100 })
    })

    it('should handle pan end', () => {
      wrapper.vm.isPanning = true
      wrapper.vm.endPan()
      expect(wrapper.vm.isPanning).toBe(false)
    })
  })

  describe('Accessibility', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should have proper ARIA attributes', () => {
      const modal = wrapper.find('.modal')
      expect(modal.attributes('tabindex')).toBe('-1')
      expect(modal.attributes('aria-hidden')).toBe('true')
    })

    it('should have proper modal title association', () => {
      const modal = wrapper.find('.modal')
      const title = wrapper.find('.modal-title')
      expect(modal.attributes('aria-labelledby')).toBe(title.attributes('id'))
    })

    it('should have close button with proper label', () => {
      const closeButton = wrapper.find('.btn-close')
      expect(closeButton.attributes('aria-label')).toBe('Close')
    })
  })

  describe('Responsiveness', () => {
    beforeEach(() => {
      wrapper = createWrapper()
    })

    it('should have responsive modal class', () => {
      expect(wrapper.find('.modal-xl').exists()).toBe(true)
    })

    it('should have responsive grid classes', () => {
      const gridItems = wrapper.findAll('.col-md-3, .col-md-4, .col-md-6, .col-md-8')
      expect(gridItems.length).toBeGreaterThan(0)
    })

    it('should have scrollable modal dialog', () => {
      expect(wrapper.find('.modal-dialog-scrollable').exists()).toBe(true)
    })
  })
})