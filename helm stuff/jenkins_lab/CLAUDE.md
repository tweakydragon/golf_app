# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Jenkins Helm chart for Kubernetes deployment with Configuration as Code (JCasC) and a custom job execution system. The chart is designed for learning and local development with Minikube.

## Common Commands

### Helm Operations
```bash
# Install Jenkins
helm install jenkins-lab-release jenkins_lab/

# Upgrade deployment
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml

# Delete deployment
helm delete jenkins-lab-release

# Run job with specific path
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml --set job.path=path_a
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml --set job.path=path_b

# Override scripts at runtime
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml \
  --set job.path=path_a \
  --set-string job.scripts.path_a="#!/bin/bash\necho \"Custom Hello World\""
```

### Kubernetes Operations
```bash
# Setup namespaces
kubectl create namespace dev
kubectl create namespace testing

# Enable local access (Minikube)
minikube tunnel

# Monitor jobs
kubectl get jobs
kubectl logs job/jenkins-lab-job-path_a
kubectl logs job/jenkins-lab-job-path_b

# View configurations
kubectl get configmap jenkins-lab-job-scripts -o yaml
kubectl describe pod [jenkins-pod-name]
```

## Architecture

### Core Components

**Jenkins Deployment**: Main Jenkins server using LTS image with:
- Web UI on port 8081 (external) → 8080 (internal)
- Jenkins agent port 50001 for distributed builds
- PersistentVolume for data storage (10Gi)
- Init container for plugin installation

**Configuration as Code**: Jenkins configured declaratively via:
- `jenkins-casc-configmap.yaml` - JCasC configuration
- `jenkins-plugins-configmap.yaml` - Plugin definitions
- Pre-configured admin user: lab_admin/lab_admin

**Job Execution System**: Custom Kubernetes job runner that:
- Executes shell scripts from ConfigMaps
- Supports multiple script paths (path_a, path_b)
- Uses pre-install hooks for cleanup
- Requires RBAC permissions for job management

### Key Files

- `values.yaml` - Main configuration file with all customizable settings
- `templates/deployment.yaml` - Core Jenkins deployment template
- `templates/job.yaml` - Kubernetes job template for script execution
- `templates/job-cleanup.yaml` - Pre-install hook for job cleanup
- `templates/*-configmap.yaml` - Configuration and script storage

### Storage Configuration

- **Jenkins Data**: PersistentVolume using hostPath at `/Users/christopherballard/Desktop/projects/helm stuff/data`
- **Storage Class**: Uses `standard` storage class
- **Additional Storage**: 1Gi PV for extra storage needs

### Script Management

Scripts are defined in `values.yaml` under `job.scripts` and mounted as ConfigMaps:
- Scripts can be overridden at deployment time using `--set-string`
- Default scripts: path_a (Hello World), path_b (Goodbye World)
- Scripts in `scripts/` directory are for reference only

### RBAC and Security

- ServiceAccount for job cleanup operations
- Role/RoleBinding for managing jobs in the namespace
- Jenkins CasC handles internal security configuration
- No external authentication configured (lab environment)

## Development Notes

- Chart designed for local Minikube development
- Uses LoadBalancer service type (requires `minikube tunnel`)
- hostPath storage requires specific local directory
- Job cleanup hooks prevent deployment conflicts
- All Jenkins configuration is declarative via JCasC