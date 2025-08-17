#!/bin/bash

# Gradle Wrapper Download Script
# This script downloads the complete Gradle Wrapper

echo "Downloading Gradle Wrapper..."

# Create wrapper directory
mkdir -p gradle/wrapper

# Download gradle-wrapper.jar
echo "Downloading gradle-wrapper.jar..."
curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar

# Download gradle-wrapper.properties
echo "Downloading gradle-wrapper.properties..."
curl -L -o gradle/wrapper/gradle-wrapper.properties https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.properties

# Download gradlew script
echo "Downloading gradlew..."
curl -L -o gradlew https://github.com/gradle/gradle/raw/v8.0.0/gradlew
chmod +x gradlew

# Download gradlew.bat
echo "Downloading gradlew.bat..."
curl -L -o gradlew.bat https://github.com/gradle/gradle/raw/v8.0.0/gradlew.bat

echo "Gradle Wrapper download completed!"
echo "Files downloaded:"
echo "- gradle/wrapper/gradle-wrapper.jar"
echo "- gradle/wrapper/gradle-wrapper.properties"
echo "- gradlew"
echo "- gradlew.bat"