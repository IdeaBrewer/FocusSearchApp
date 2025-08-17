#!/bin/bash

echo "创建专注搜索助手项目包..."
echo

# 创建打包目录
PACKAGE_DIR="FocusSearchApp_Package"
mkdir -p "$PACKAGE_DIR"

# 复制所有项目文件
echo "复制项目文件..."
cp -r app "$PACKAGE_DIR/"
cp build.gradle "$PACKAGE_DIR/"
cp settings.gradle "$PACKAGE_DIR/"
cp gradle.properties "$PACKAGE_DIR/"
cp gradlew "$PACKAGE_DIR/"
cp gradlew.bat "$PACKAGE_DIR/"
cp -r gradle "$PACKAGE_DIR/"
cp build.bat "$PACKAGE_DIR/"
cp build.sh "$PACKAGE_DIR/"
cp README.md "$PACKAGE_DIR/"
cp INSTALL.md "$PACKAGE_DIR/"

# 创建说明文件
cat > "$PACKAGE_DIR/使用说明.txt" << EOF
专注搜索助手 - Android专注力工具

=== 快速开始 ===
1. 上传整个文件夹到在线构建服务：
   - 推荐：https://appcircle.io/
   - 或：https://www.bitrise.io/

2. 或者如果你有Android Studio：
   - 直接打开此文件夹
   - 点击绿色播放按钮构建APK

3. 构建完成后安装到手机：
   - 传输APK到安卓设备
   - 开启"未知来源应用"权限
   - 点击安装即可使用

=== 支持的应用 ===
百度、淘宝、京东、知乎、抖音、小红书、B站、微信读书

=== 功能说明 ===
1. 智能搜索跳转 - 跳过首页直达搜索结果
2. 专注力监测 - 定时提醒避免分心
3. 优雅悬浮窗 - 温馨专注提醒

=== 注意事项 ===
首次使用需要手动授予：
- 使用情况访问权限
- 悬浮窗显示权限

构建时间：$(date)
EOF

# 创建zip压缩包
echo "创建压缩包..."
zip -r "FocusSearchApp.zip" "$PACKAGE_DIR/"

# 清理临时目录
rm -rf "$PACKAGE_DIR"

echo
echo "========================================"
echo "项目包已创建完成！"
echo "文件名: FocusSearchApp.zip"
echo "大小: $(ls -lh FocusSearchApp.zip | awk '{print $5}')"
echo
echo "接下来你可以："
echo "1. 将 FocusSearchApp.zip 上传到在线构建服务"
echo "2. 或发送给有Android Studio的朋友帮忙构建"
echo "3. 或参考INSTALL.md中的其他方法"
echo "========================================"