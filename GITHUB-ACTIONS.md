# GitHub Actions CI/CD Pipeline

## Overview

This project includes a comprehensive GitHub Actions CI/CD pipeline that provides automated testing, security scanning, and deployment capabilities. **The pipeline is currently disabled to avoid billing charges.**

## Pipeline Features

### 🧪 **Automated Testing**
- **Backend Tests**: JUnit tests with PostgreSQL test database
- **Frontend Tests**: Vitest unit tests with coverage reporting
- **Test Reporting**: Detailed test results and coverage uploads

### 🔐 **Security Scanning**
- **Vulnerability Scanning**: Trivy security scanner for dependencies and containers
- **SARIF Reporting**: Results uploaded to GitHub Security tab
- **Code Quality**: Automated security checks

### 🐳 **Container Management**
- **Multi-architecture Builds**: Docker images for backend and frontend
- **Container Registry**: Automated publishing to GitHub Container Registry (GHCR)
- **Image Optimization**: Multi-stage builds with caching

### 🚀 **Deployment Automation**
- **Staging Deployment**: Automatic deployment from `develop` branch
- **Production Deployment**: Automatic deployment from `main` branch
- **Deployment Notifications**: Success/failure status reporting

## Current Status: DISABLED

The GitHub Actions workflow is currently **disabled** to prevent billing charges. The workflow file exists but is commented out.

### Why Disabled?
- **Billing Concerns**: GitHub Actions charges for compute time
- **Free Tier Limits**: 2,000 minutes/month for public repos
- **Cost Management**: Avoiding unexpected charges during development

## Billing Information

### 📊 **Free Tier Limits**
- **Public Repositories**: 2,000 minutes/month (free)
- **Private Repositories**: 2,000 minutes/month for free accounts
- **Runner Multipliers**:
  - Ubuntu: 1x (cheapest)
  - Windows: 2x
  - macOS: 10x (most expensive)

### 💰 **Estimated Costs**
Based on this pipeline's complexity:
- **Light Usage** (few commits): $0-5/month
- **Regular Development** (daily commits): $5-15/month
- **Heavy Usage** (multiple daily builds): $15-50/month

### ⚡ **Cost Optimization Tips**
1. Use Ubuntu runners (1x cost multiplier)
2. Optimize workflows to reduce run time
3. Use workflow conditions to skip unnecessary runs
4. Cache dependencies to speed up builds
5. Run tests locally before pushing

## Enabling/Disabling the Pipeline

### 🔄 **To Enable GitHub Actions**

#### Option 1: Using Scripts (Recommended)
```bash
# Enable the pipeline
make enable-actions
# or
./scripts/enable-github-actions.sh

# Commit and push changes
git add .github/workflows/ci-cd.yml
git commit -m "Enable GitHub Actions CI/CD pipeline"
git push
```

#### Option 2: Manual Editing
1. Open `.github/workflows/ci-cd.yml`
2. Remove all `#` comment prefixes
3. Remove the header comment block
4. Commit and push changes

### ⏸️ **To Disable GitHub Actions**

```bash
# Disable the pipeline
make disable-actions
# or
./scripts/disable-github-actions.sh

# Commit and push changes
git add .github/workflows/ci-cd.yml
git commit -m "Disable GitHub Actions to avoid billing"
git push
```

## Workflow Triggers

When enabled, the pipeline runs on:
- **Push** to `main` or `develop` branches
- **Pull Requests** to `main` branch
- **Manual triggers** via GitHub UI

## Pipeline Jobs

### 1. **Backend Tests** (`backend-tests`)
- Sets up PostgreSQL test database
- Runs Spring Boot tests with Maven
- Generates JUnit test reports
- **Duration**: ~3-5 minutes

### 2. **Frontend Tests** (`frontend-tests`)
- Sets up Node.js environment
- Runs Vitest unit tests
- Generates coverage reports
- Uploads coverage to Codecov
- **Duration**: ~2-4 minutes

### 3. **Security Scan** (`security-scan`)
- Runs Trivy vulnerability scanner
- Scans dependencies and configuration
- Uploads results to GitHub Security tab
- **Duration**: ~1-2 minutes

### 4. **Build and Push** (`build-and-push`)
- Builds Docker images for backend and frontend
- Pushes to GitHub Container Registry
- Uses BuildKit caching for efficiency
- **Duration**: ~5-10 minutes

### 5. **Deploy Staging** (`deploy-staging`)
- Deploys to staging environment (develop branch)
- Conditional job (only on develop branch)
- **Duration**: ~2-5 minutes

### 6. **Deploy Production** (`deploy-production`)
- Deploys to production environment (main branch)
- Conditional job (only on main branch)
- **Duration**: ~2-5 minutes

### 7. **Notifications** (`notify`)
- Sends deployment status notifications
- Runs after production deployment
- **Duration**: ~30 seconds

## Configuration

### Required Secrets
When enabling, ensure these GitHub secrets are configured:
- `GITHUB_TOKEN` (automatically provided)
- Additional secrets for deployment targets (if using external services)

### Environment Variables
The workflow uses environment variables for:
- Container registry configuration
- Database connection strings
- Deployment targets

## Monitoring Usage

### 📊 **Tracking Minutes**
1. Go to GitHub Repository Settings
2. Navigate to "Billing and plans"
3. View "Actions & Packages" usage
4. Monitor monthly minutes consumption

### 🚨 **Setting Alerts**
1. Set up billing alerts in GitHub account settings
2. Configure spending limits
3. Monitor usage patterns

## Alternative Testing Approaches

While GitHub Actions is disabled, you can still run tests locally:

### Backend Testing
```bash
cd backend
./mvnw clean test
```

### Frontend Testing
```bash
cd frontend
npm run test:run
npm run test:coverage
```

### Full Local Testing
```bash
make test
```

### Security Scanning
```bash
# Install Trivy locally
./scripts/security-scan.sh
```

## Re-enabling Considerations

Before re-enabling GitHub Actions:

1. **Budget Planning**: Determine acceptable monthly cost
2. **Usage Monitoring**: Plan to monitor usage regularly
3. **Optimization**: Consider workflow optimizations to reduce runtime
4. **Alternatives**: Evaluate other CI/CD options (Jenkins, GitLab CI, etc.)

## Support

For questions about GitHub Actions billing or setup:
- [GitHub Actions Pricing](https://github.com/pricing)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Managing Billing](https://docs.github.com/en/billing/managing-billing-for-github-actions)

---

**Current Status**: ⏸️ **DISABLED** - Use `make enable-actions` to activate when ready.