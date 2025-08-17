@echo off
echo Building APK...
echo.

java -version
if %errorlevel% neq 0 (
    echo Please install Java JDK first
    pause
    exit /b 1
)

call gradlew clean
call gradlew assembleRelease

echo.
echo Build completed!
echo APK location: app\build\outputs\apk\release\app-release.apk
pause