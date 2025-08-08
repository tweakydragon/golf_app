# Golf App Frontend Testing Guide

This document provides comprehensive guidance for testing the Vue.js golf application frontend.

## Testing Framework Overview

The application uses **Vitest** as the primary testing framework, chosen for its excellent integration with Vite and Vue 3. The testing stack includes:

- **Vitest**: Fast unit testing framework with native ES modules support
- **Vue Test Utils**: Official testing utilities for Vue components
- **jsdom**: Browser environment simulation for DOM testing
- **@vitest/coverage-v8**: Code coverage reporting using V8 engine
- **@vitest/ui**: Web-based UI for running and monitoring tests

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── __tests__/               # Component tests
│   │   │   ├── SessionView.test.js
│   │   │   ├── ShotDetailModal.test.js
│   │   │   └── basic.test.js
│   │   └── charts/
│   │       └── __tests__/           # Chart component tests
│   │           └── ClubUsageChart.test.js
│   └── test/
│       ├── setup.js                 # Global test configuration
│       └── test-utils.js            # Shared test utilities
├── vitest.config.js                 # Vitest configuration
└── TESTING.md                       # This documentation
```

## Configuration Files

### vitest.config.js
Main configuration file that sets up:
- jsdom environment for DOM simulation
- Global test setup
- Coverage thresholds and reporting
- File inclusion/exclusion patterns
- Path aliases

### src/test/setup.js
Global setup file that provides:
- Mocks for Bootstrap components
- Canvas context mocking for Chart.js
- Axios mocking
- Vue Router mocking
- Global utilities and observers

### src/test/test-utils.js
Shared utilities including:
- Mock data generators
- Component wrapper helpers
- Async operation utilities
- Mock axios responses

## Available Test Scripts

```bash
# Run tests in watch mode (recommended for development)
npm run test

# Run tests once and exit
npm run test:run

# Run tests with coverage report
npm run test:coverage

# Open Vitest UI in browser
npm run test:ui

# Run tests in watch mode
npm run test:watch

# Run tests related to changed files
npm run test:related

# Run tests for changed files only
npm run test:changed
```

## Writing Tests

### Basic Test Structure

```javascript
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import YourComponent from '../YourComponent.vue'

describe('YourComponent', () => {
  let wrapper

  afterEach(() => {
    if (wrapper) {
      wrapper.unmount()
    }
  })

  const createWrapper = (props = {}) => {
    return mount(YourComponent, {
      props: {
        // Default props
        ...props
      }
    })
  }

  describe('Component Behavior', () => {
    it('should render correctly', () => {
      wrapper = createWrapper()
      expect(wrapper.exists()).toBe(true)
    })
  })
})
```

### Testing Vue Components

#### Props Testing
```javascript
it('should accept and display props correctly', () => {
  wrapper = createWrapper({ title: 'Test Title' })
  expect(wrapper.text()).toContain('Test Title')
})
```

#### Event Testing
```javascript
it('should emit events on user interaction', async () => {
  wrapper = createWrapper()
  const button = wrapper.find('button')
  await button.trigger('click')
  expect(wrapper.emitted()).toHaveProperty('buttonClicked')
})
```

#### Computed Properties Testing
```javascript
it('should calculate computed properties correctly', () => {
  wrapper = createWrapper({ shots: mockShots })
  expect(wrapper.vm.averageDistance).toBe(250.5)
})
```

### Testing Chart Components

Chart components require special mocking since they use Chart.js:

```javascript
// Mock Chart.js
vi.mock('chart.js', () => ({
  Chart: vi.fn().mockImplementation(() => ({
    destroy: vi.fn(),
    update: vi.fn(),
    render: vi.fn()
  })),
  CategoryScale: vi.fn(),
  LinearScale: vi.fn(),
  // ... other Chart.js components
  register: vi.fn()
}))

describe('ChartComponent', () => {
  it('should create chart with correct data', async () => {
    const { Chart } = await import('chart.js')
    wrapper = createWrapper({ data: mockData })
    
    await waitForAsync()
    
    expect(Chart).toHaveBeenCalledWith(
      expect.any(Object),
      expect.objectContaining({
        type: 'doughnut',
        data: expect.objectContaining({
          labels: expect.arrayContaining(['Driver', '7 Iron'])
        })
      })
    )
  })
})
```

### Testing API Interactions

```javascript
import axios from 'axios'

vi.mock('axios')

describe('API Integration', () => {
  beforeEach(() => {
    const mockAxios = axios
    mockAxios.get = vi.fn()
  })

  it('should fetch data on mount', async () => {
    axios.get.mockResolvedValue({ data: mockData })
    wrapper = createWrapper()
    
    await flushPromises()
    
    expect(axios.get).toHaveBeenCalledWith('http://localhost:8080/api/sessions/1')
  })
})
```

### Testing Router Integration

Components using Vue Router need special setup:

```javascript
const createWrapper = (routeParams = { id: '1' }) => {
  return mount(Component, {
    global: {
      mocks: {
        $route: { params: routeParams }
      },
      stubs: {
        'router-link': {
          template: '<a><slot /></a>',
          props: ['to']
        }
      }
    }
  })
}
```

## Testing Best Practices

### 1. Test Organization
- Group related tests using `describe` blocks
- Use descriptive test names that explain what is being tested
- Follow the AAA pattern: Arrange, Act, Assert

### 2. Component Testing Strategy
- **Unit Tests**: Test individual component logic in isolation
- **Integration Tests**: Test component interactions with props/events
- **Behavioral Tests**: Test user interactions and workflows

### 3. Mock Strategy
- Mock external dependencies (APIs, Chart.js, etc.)
- Use real implementations for Vue utilities when possible
- Keep mocks simple and focused

### 4. Data Management
- Use factory functions for creating test data
- Keep mock data realistic and representative
- Reuse common test data across multiple tests

### 5. Async Testing
```javascript
// Wait for promises to resolve
await flushPromises()

// Wait for DOM updates
await wrapper.vm.$nextTick()

// Wait for specific time
await waitForAsync(100)
```

## Coverage Goals

The project has the following coverage thresholds:
- **Branches**: 70%
- **Functions**: 70%
- **Lines**: 70%
- **Statements**: 70%

### Generating Coverage Reports

```bash
# Generate coverage report
npm run test:coverage

# Coverage files are generated in:
# - coverage/ (HTML report)
# - coverage/coverage.json (JSON report)
```

### Coverage Exclusions

The following are excluded from coverage:
- `node_modules/`
- `src/test/` (test utilities)
- `src/**/*.d.ts` (TypeScript definitions)
- `src/main.js` (application entry point)
- `src/router/` (router configuration)
- `dist/` and `public/` (build artifacts)

## Component-Specific Testing Notes

### SessionView Component
- Tests data loading from multiple API endpoints
- Validates chart component rendering
- Tests tab navigation and filtering
- Verifies computed property calculations

### ShotDetailModal Component  
- Tests modal rendering and accessibility
- Validates data formatting and visualization
- Tests zoom and pan controls
- Verifies canvas rendering

### Chart Components
- Mock Chart.js library
- Test data processing and formatting
- Validate chart configuration options
- Test responsive behavior

## Debugging Tests

### Using Vitest UI
```bash
npm run test:ui
```
This opens a web interface showing:
- Test results and status
- Code coverage visualization
- Test execution timeline
- Interactive debugging tools

### Console Debugging
```javascript
// Add debug output in tests
console.log('Component state:', wrapper.vm.$data)
console.log('Rendered HTML:', wrapper.html())

// Use Vitest debugging
import { debug } from 'vitest'
debug(wrapper.vm.computedProperty)
```

### Common Issues and Solutions

#### Router Injection Errors
Ensure Vue Router is properly mocked in test setup:
```javascript
vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  useRouter: () => ({ push: vi.fn() })
}))
```

#### Chart.js Errors
Mock Chart.js and canvas context:
```javascript
HTMLCanvasElement.prototype.getContext = vi.fn().mockReturnValue({
  fillRect: vi.fn(),
  // ... other canvas methods
})
```

#### Bootstrap Component Errors
Mock Bootstrap components used in components:
```javascript
global.bootstrap = {
  Modal: vi.fn().mockImplementation(() => ({
    show: vi.fn(),
    hide: vi.fn()
  }))
}
```

## Continuous Integration

For CI/CD pipelines, use:
```bash
# Run tests with coverage and exit
npm run test:coverage

# Check if coverage meets thresholds
# (Vitest will exit with error code if thresholds not met)
```

## Future Enhancements

### Potential Additions
1. **E2E Testing**: Consider adding Playwright or Cypress for end-to-end testing
2. **Visual Regression Testing**: Add screenshot testing for chart components
3. **Performance Testing**: Add tests to monitor component rendering performance
4. **Accessibility Testing**: Integrate automated accessibility testing tools

### Test Expansion Areas
1. More comprehensive router testing
2. Error boundary testing
3. Responsive design testing
4. Cross-browser compatibility testing

## Resources

- [Vitest Documentation](https://vitest.dev/)
- [Vue Test Utils Documentation](https://test-utils.vuejs.org/)
- [Vue 3 Testing Cookbook](https://vuejs.org/guide/scaling-up/testing.html)
- [Testing Chart.js Components](https://www.chartjs.org/docs/latest/developers/api.html)

---

This testing framework provides a solid foundation for maintaining code quality and reliability in the golf application frontend.