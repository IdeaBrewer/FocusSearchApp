@echo off
chcp 65001 >nul
echo ========================================
echo Focus Search App APK Build Script
echo ========================================
echo.

:: Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found, please install Java JDK first
    echo Download: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

:: Check if Gradle is available
gradle -version >nul 2>&1
if %errorlevel% neq 0 (
    echo INFO: Gradle not in system PATH, using project Gradle Wrapper
    echo.
)

echo Starting APK build...
echo.

:: Clean previous build
call gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

:: Build Release APK
call gradlew assembleRelease
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed!
echo ========================================
echo APK location: app\build\outputs\apk\release\app-release.apk
echo.

:: Check if APK file exists
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo File size: 
    dir "app\build\outputs\apk\release\app-release.apk"
    echo.
    echo You can transfer this APK file to your Android device and install!
    echo.
    echo Installation instructions:
    echo 1. Transfer APK file to your phone
    echo 2. Enable "Unknown sources" installation permission in phone settings
    echo 3. Click APK file to install
    echo 4. Grant permissions when first running the app
) else (
    echo ERROR: APK file not found
)

pause