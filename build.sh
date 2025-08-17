#!/bin/bash

echo "========================================"
echo "专注搜索助手 APK 构建脚本"
echo "========================================"
echo

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请先安装Java JDK"
    echo "下载地址: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# 检查Gradle是否可用
if ! command -v gradle &> /dev/null; then
    echo "提示: Gradle未在系统PATH中，将使用项目自带的Gradle Wrapper"
    echo
fi

echo "开始构建APK..."
echo

# 清理之前的构建
./gradlew clean
if [ $? -ne 0 ]; then
    echo "错误: 清理失败"
    exit 1
fi

# 构建Release版本APK
./gradlew assembleRelease
if [ $? -ne 0 ]; then
    echo "错误: 构建失败"
    exit 1
fi

echo
echo "========================================"
echo "构建完成！"
echo "========================================"
echo "APK文件位置: app/build/outputs/apk/release/app-release.apk"
echo

# 检查APK文件是否存在
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo "文件大小:"
    ls -lh "app/build/outputs/apk/release/app-release.apk"
    echo
    echo "你可以直接将此APK文件传输到安卓设备并安装！"
    echo
    echo "安装说明:"
    echo "1. 将APK文件传输到手机"
    echo "2. 在手机设置中开启'未知来源应用'安装权限"
    echo "3. 点击APK文件进行安装"
    echo "4. 首次运行时按提示授予权限"
else
    echo "错误: 未找到生成的APK文件"
fi