#!/bin/bash

# Script to re-enable GitHub Actions CI/CD Pipeline
# Usage: ./scripts/enable-github-actions.sh

set -e

echo "🔄 Re-enabling GitHub Actions CI/CD Pipeline..."

# Check if the workflow file exists
WORKFLOW_FILE=".github/workflows/ci-cd.yml"

if [[ ! -f "$WORKFLOW_FILE" ]]; then
    echo "❌ Error: Workflow file not found at $WORKFLOW_FILE"
    exit 1
fi

# Create a backup of the current disabled workflow
cp "$WORKFLOW_FILE" "$WORKFLOW_FILE.disabled.backup"
echo "📋 Created backup: $WORKFLOW_FILE.disabled.backup"

# Remove comment prefixes to re-enable the workflow
sed -i '' 's/^# //' "$WORKFLOW_FILE"
sed -i '' 's/^#   /  /' "$WORKFLOW_FILE"
sed -i '' 's/^#     /    /' "$WORKFLOW_FILE"
sed -i '' 's/^#       /      /' "$WORKFLOW_FILE"
sed -i '' 's/^#         /        /' "$WORKFLOW_FILE"
sed -i '' 's/^#           /          /' "$WORKFLOW_FILE"

# Remove the disabled header comment block
sed -i '' '1,16d' "$WORKFLOW_FILE"

echo "✅ GitHub Actions workflow has been re-enabled!"
echo ""
echo "📝 Next steps:"
echo "1. Review the workflow file: $WORKFLOW_FILE"
echo "2. Commit and push the changes to activate the pipeline"
echo "3. Monitor your GitHub Actions usage to avoid unexpected billing"
echo ""
echo "💰 Billing reminder:"
echo "- GitHub Actions has 2,000 free minutes per month for public repos"
echo "- Private repos: 2,000 free minutes for free accounts, more for paid plans"
echo "- Each job run consumes minutes based on runner type (Ubuntu: 1x, Windows: 2x, macOS: 10x)"
echo ""
echo "🔧 To disable again, run: ./scripts/disable-github-actions.sh"