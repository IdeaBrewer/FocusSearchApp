# GitHub编译APK详细指南

## 🎯 完整操作流程

### 第一步：注册GitHub账号
1. 访问 [github.com](https://github.com)
2. 点击 "Sign up" 注册账号
3. 完成邮箱验证

### 第二步：创建仓库
1. 登录GitHub后，点击右上角的 "+" 号
2. 选择 "New repository"
3. 填写仓库信息：
   - **Repository name**: `FocusSearchApp` (或其他你喜欢的名字)
   - **Description**: `专注搜索助手安卓应用`
   - 设置为 **Public** (免费用户只能使用Public仓库的Actions)
   - **不要**勾选 "Add a README file" (因为我们本地已经有文件了)
4. 点击 "Create repository"

### 第三步：上传项目文件
#### 方法A：使用GitHub Desktop (推荐新手)
1. 下载安装 [GitHub Desktop](https://desktop.github.com/)
2. 登录你的GitHub账号
3. 点击 "File" → "Add Local Repository"
4. 选择你的 `FocusSearchApp` 文件夹
5. 点击 "Publish repository"
6. 填写仓库名称，选择Public，点击 "Publish"

#### 方法B：使用Git命令行
1. 安装 [Git](https://git-scm.com/)
2. 在项目文件夹右键选择 "Git Bash Here"
3. 依次执行以下命令：

```bash
# 初始化Git仓库
git init

# 添加所有文件
git add .

# 提交代码
git commit -m "Initial commit"

# 添加远程仓库 (替换YOUR_USERNAME为你的GitHub用户名)
git remote add origin https://github.com/YOUR_USERNAME/FocusSearchApp.git

# 推送到GitHub
git branch -M main
git push -u origin main
```

### 第四步：自动编译APK
1. 推送代码后，GitHub会自动开始编译
2. 在GitHub仓库页面点击 "Actions" 标签
3. 你会看到 "Build Android APK" 工作流正在运行
4. 等待大约5-10分钟编译完成

### 第五步：下载APK
1. 编译完成后，进入Actions页面
2. 点击最新的构建记录
3. 在左侧选择 "Artifacts"
4. 你会看到两个APK文件：
   - `debug-apk`: 调试版本 (直接安装)
   - `release-apk`: 发布版本 (推荐使用)

5. 点击APK文件下载到本地

### 第六步：安装到手机
#### 1. 传输APK到手机
- **USB数据线**: 连接手机和电脑，直接复制文件
- **微信/QQ**: 发送文件到手机
- **云存储**: 上传到百度网盘等，在手机下载

#### 2. 开启安装权限
不同安卓手机设置路径略有不同：

**小米手机**:
设置 → 安全与隐私 → 更多安全设置 → 未知来源应用

**华为手机**:
设置 → 安全和隐私 → 更多安全设置 → 安装外部来源应用

**OPPO手机**:
设置 → 安全 → 安装未知应用

**vivo手机**:
设置 → 安全与隐私 → 更多安全设置 → 未知来源应用

**通用方法**:
在设置中搜索 "未知来源" 或 "外部来源应用"

#### 3. 安装应用
1. 找到下载的APK文件
2. 点击安装
3. 按提示完成安装

#### 4. 首次使用设置
1. 打开应用
2. 按提示授予权限：
   - **使用情况访问权限**: 用于监测应用使用
   - **悬浮窗权限**: 用于显示专注提醒
3. 开始使用！

## ⚠️ 常见问题解决

### 问题1：GitHub Actions编译失败
- 检查代码是否完整上传
- 查看Actions日志中的错误信息
- 可能是网络问题，重新触发构建

### 问题2：APK安装失败
- 确保开启了"未知来源应用"权限
- 检查手机系统版本是否支持 (Android 7.0+)
- 尝试下载debug版本安装

### 问题3：权限申请失败
- 在手机设置中手动授予权限
- 不同品牌手机权限设置路径不同
- 搜索 "使用情况访问权限" 找到对应设置

## 📱 支持的应用版本
- **最低要求**: Android 7.0 (API 24)
- **推荐版本**: Android 10+ 以获得最佳体验

## 🎉 恭喜！
完成以上步骤后，你就可以使用这个专注搜索助手了！有问题随时问我。