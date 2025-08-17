# 专注搜索助手 (Focus Search App)

一个帮助用户保持专注的安卓应用，支持直达热门APP搜索结果页面，并提供定时专注提醒功能。

## 主要功能

### 1. 智能搜索跳转
- 输入关键词并选择目标应用
- 自动跳过应用首页，直达搜索结果页面
- 支持百度、淘宝、京东、知乎、抖音等热门应用

### 2. 专注力监测
- 实时监测用户在目标应用中的停留时间
- 可设置10/20/30分钟提醒间隔
- 优雅的悬浮窗提醒，避免打扰专注状态

### 3. 用户友好设计
- 简洁直观的界面设计
- 自动权限申请和管理
- 前台服务保证稳定性

## 技术实现

### 核心技术栈
- **开发语言**: Kotlin
- **最低SDK版本**: 24 (Android 7.0)
- **目标SDK版本**: 34 (Android 14)
- **架构**: MVVM模式

### 关键功能模块

#### 1. 深度链接处理
```kotlin
// 百度搜索示例
Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse("baiduboxapp://search?word=${encodedKeyword}")
    setPackage(app.packageName)
}
```

#### 2. 应用使用监测
```kotlin
// 使用UsageStatsManager获取前台应用
val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
val stats = usageStatsManager.queryUsageStats(interval, startTime, endTime)
```

#### 3. 悬浮窗提醒
```kotlin
// 使用WindowManager显示悬浮窗
val layoutParams = WindowManager.LayoutParams(
    width, height,
    TYPE_APPLICATION_OVERLAY,
    FLAG_NOT_FOCUSABLE,
    PixelFormat.TRANSLUCENT
)
```

## 权限说明

应用需要以下权限：
- **QUERY_ALL_PACKAGES**: 查询已安装应用列表
- **PACKAGE_USAGE_STATS**: 监测应用使用情况
- **SYSTEM_ALERT_WINDOW**: 显示悬浮窗提醒
- **FOREGROUND_SERVICE**: 运行前台服务
- **POST_NOTIFICATIONS**: 显示通知

## 使用说明

### 1. 首次使用
1. 打开应用并授予必要权限
2. 输入搜索关键词
3. 选择目标应用
4. 设置提醒间隔
5. 点击"开始专注搜索"

### 2. 专注监测
- 应用会自动跳转到目标应用的搜索结果页面
- 后台服务会监测您的使用状态
- 按设定时间间隔显示专注提醒

### 3. 结束专注
- 点击"是的，完成了"停止监测
- 或选择"继续专注"继续工作

## 支持的应用

目前支持以下应用的深度链接跳转：
- 百度搜索
- 淘宝
- 京东
- 知乎
- 抖音
- 小红书
- B站
- 微信读书

## 开发环境

使用Android Studio开发，需要：
- Android Studio Giraffe | 2022.3.1+
- Android Gradle Plugin 8.1.0+
- Kotlin 1.9.0+

## 构建和安装

1. 克隆项目到本地
2. 在Android Studio中打开项目
3. 等待Gradle同步完成
4. 连接安卓设备或启动模拟器
5. 点击Run按钮构建并安装

## 注意事项

1. 首次使用需要手动授予特殊权限
2. 部分应用可能不支持深度链接，会跳转到首页
3. 在电池优化设置中将应用设置为"无限制"以确保服务正常运行
4. 不同Android版本的权限管理可能有所不同

## 后续优化

- [ ] 支持更多应用的深度链接
- [ ] 添加专注时间统计功能
- [ ] 优化悬浮窗UI设计
- [ ] 添加专注成就系统
- [ ] 支持自定义提醒内容
- [ ] 添加数据分析和报告功能