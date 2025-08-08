#!/bin/bash

# Script to disable GitHub Actions CI/CD Pipeline
# Usage: ./scripts/disable-github-actions.sh

set -e

echo "⏸️  Disabling GitHub Actions CI/CD Pipeline..."

# Check if the workflow file exists
WORKFLOW_FILE=".github/workflows/ci-cd.yml"

if [[ ! -f "$WORKFLOW_FILE" ]]; then
    echo "❌ Error: Workflow file not found at $WORKFLOW_FILE"
    exit 1
fi

# Create a backup of the current enabled workflow
cp "$WORKFLOW_FILE" "$WORKFLOW_FILE.enabled.backup"
echo "📋 Created backup: $WORKFLOW_FILE.enabled.backup"

# Add disabled header comment block
cat > temp_header.txt << 'EOF'
# DISABLED: GitHub Actions CI/CD Pipeline
# 
# This workflow is currently disabled to avoid GitHub Actions billing charges.
# To re-enable:
# 1. Run: ./scripts/enable-github-actions.sh
# 2. Commit and push the changes
#
# The workflow provides:
# - Backend and frontend testing
# - Security scanning with Trivy
# - Docker image building and pushing to GHCR
# - Automated deployment to staging and production
#
# Estimated monthly cost: $5-20 depending on usage
# Free tier: 2,000 minutes per month for public repos

EOF

# Add comment prefixes to disable the workflow
sed 's/^/# /' "$WORKFLOW_FILE" > temp_workflow.yml
cat temp_header.txt temp_workflow.yml > "$WORKFLOW_FILE"

# Clean up temp files
rm temp_header.txt temp_workflow.yml

echo "✅ GitHub Actions workflow has been disabled!"
echo ""
echo "📝 The workflow is now commented out and will not run."
echo "💰 This prevents any GitHub Actions billing charges."
echo ""
echo "🔧 To re-enable, run: ./scripts/enable-github-actions.sh"