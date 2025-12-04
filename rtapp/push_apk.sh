#!/bin/bash

set -e

APK_SOURCE="app/build/outputs/apk/debug/app-debug.apk"
APK_TARGET="app-debug-latest.apk"

echo "Copying APK..."
cp "$APK_SOURCE" "$APK_TARGET"

echo "Adding to Git..."
git add "$APK_TARGET"

echo "Commit message:"
COMMIT_MSG="Update APK at $(date '+%Y-%m-%d %H:%M:%S')"
echo "$COMMIT_MSG"
git commit -m "$COMMIT_MSG"

echo "Pushing to GitHub..."
git push

echo "Done. APK available at:"
echo "https://raw.githubusercontent.com/<USERNAME>/<REPO>/main/$APK_TARGET"
