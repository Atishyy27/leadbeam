// scripts/pre_submission_check.sh
#!/bin/bash

echo "🔍 FieldFlow Pre-Submission Checklist"
echo "======================================"

# Build check
echo "✓ Running clean build..."
./gradlew clean assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful"

# Test check
echo "✓ Running tests..."
./gradlew test

if [ $? -ne 0 ]; then
    echo "⚠️  Some tests failed"
else
    echo "✅ All tests passed"
fi

# Lint check
echo "✓ Running lint..."
./gradlew lint

# Check for TODOs
echo "✓ Checking for TODO comments..."
TODO_COUNT=$(grep -r "TODO" --include="*.kt" app/ feature/ core/ | wc -l)
if [ $TODO_COUNT -gt 0 ]; then
    echo "⚠️  Found $TODO_COUNT TODO comments"
    grep -r "TODO" --include="*.kt" app/ feature/ core/
fi

# Check for hardcoded strings
echo "✓ Checking for hardcoded strings..."
HARDCODED=$(grep -r "\"[A-Z]" --include="*.kt" */src/main/java | grep -v "contentDescription\|semantics\|TAG" | wc -l)
if [ $HARDCODED -gt 10 ]; then
    echo "⚠️  Found $HARDCODED potential hardcoded strings"
fi

echo ""
echo "======================================"
echo "Pre-submission check complete!"