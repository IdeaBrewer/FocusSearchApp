@echo off
echo 创建专注搜索助手项目包...
echo.

:: 创建打包目录
set PACKAGE_DIR=FocusSearchApp_Package
if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
mkdir "%PACKAGE_DIR%"

:: 复制所有项目文件
echo 复制项目文件...
xcopy app "%PACKAGE_DIR%\app\" /E /I /H /Y
copy build.gradle "%PACKAGE_DIR\" >nul
copy settings.gradle "%PACKAGE_DIR\" >nul
copy gradle.properties "%PACKAGE_DIR\" >nul
copy gradlew "%PACKAGE_DIR\" >nul
copy gradlew.bat "%PACKAGE_DIR\" >nul
xcopy gradle "%PACKAGE_DIR%\gradle\" /E /I /H /Y
copy build.bat "%PACKAGE_DIR\" >nul
copy build.sh "%PACKAGE_DIR\" >nul
copy README.md "%PACKAGE_DIR\" >nul
copy INSTALL.md "%PACKAGE_DIR\" >nul

:: 创建说明文件
echo 创建说明文件...
(
echo 专注搜索助手 - Android专注力工具
echo.
echo === 快速开始 ===
echo 1. 上传整个文件夹到在线构建服务：
echo    - 推荐：https://appcircle.io/
echo    - 或：https://www.bitrise.io/
echo.
echo 2. 或者如果你有Android Studio：
echo    - 直接打开此文件夹
echo    - 点击绿色播放按钮构建APK
echo.
echo 3. 构建完成后安装到手机：
echo    - 传输APK到安卓设备
echo    - 开启"未知来源应用"权限
echo    - 点击安装即可使用
echo.
echo === 支持的应用 ===
echo 百度、淘宝、京东、知乎、抖音、小红书、B站、微信读书
echo.
echo === 功能说明 ===
echo 1. 智能搜索跳转 - 跳过首页直达搜索结果
echo 2. 专注力监测 - 定时提醒避免分心
echo 3. 优雅悬浮窗 - 温馨专注提醒
echo.
echo === 注意事项 ===
echo 首次使用需要手动授予：
echo - 使用情况访问权限
echo - 悬浮窗显示权限
echo.
echo 构建时间：%date% %time%
) > "%PACKAGE_DIR%\使用说明.txt"

:: 创建zip压缩包
echo 创建压缩包...
if exist "FocusSearchApp.zip" del "FocusSearchApp.zip"
powershell -Command "Compress-Archive -Path '%PACKAGE_DIR%' -DestinationPath 'FocusSearchApp.zip' -Force"

:: 清理临时目录
rmdir /s /q "%PACKAGE_DIR%"

echo.
echo ========================================
echo 项目包已创建完成！
echo 文件名: FocusSearchApp.zip
echo.

if exist "FocusSearchApp.zip" (
    echo 大小: 
    dir "FocusSearchApp.zip" | find "FocusSearchApp.zip"
    echo.
    echo 接下来你可以：
    echo 1. 将 FocusSearchApp.zip 上传到在线构建服务
    echo 2. 或发送给有Android Studio的朋友帮忙构建
    echo 3. 或参考INSTALL.md中的其他方法
) else (
    echo 错误: 压缩包创建失败
)

echo ========================================
pause