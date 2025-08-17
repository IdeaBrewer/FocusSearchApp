@echo off
REM Simple build script for Windows
REM This bypasses the gradle wrapper issues

echo Building Android APK...

REM Check if gradle is available
gradle --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Gradle not found. Installing or using Gradle Wrapper...
    echo Please make sure Gradle is installed and in PATH
    exit /b 1
)

REM Build debug APK
echo Building Debug APK...
gradle assembleDebug

if %errorlevel% neq 0 (
    echo Debug build failed!
    exit /b 1
)

echo Debug APK built successfully!
echo Location: app\build\outputs\apk\debug\app-debug.apk

REM Build release APK
echo Building Release APK...
gradle assembleRelease

if %errorlevel% neq 0 (
    echo Release build failed!
    exit /b 1
)
echo Release APK built successfully!
echo Location: app\build\outputs\apk\release\app-release.apk

echo All builds completed successfully!