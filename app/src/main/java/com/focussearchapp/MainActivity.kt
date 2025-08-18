package com.focussearchapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.focussearchapp.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.ApplicationInfo
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_USAGE_STATS = 1001
    private val REQUEST_CODE_OVERLAY = 1002
    private val REQUEST_CODE_NOTIFICATIONS = 1003

    // 支持的应用列表
    private val supportedApps = listOf(
        AppInfo("百度", "com.baidu.searchbox", "baidu"),
        AppInfo("淘宝", "com.taobao.taobao", "taobao"),
        AppInfo("京东", "com.jingdong.app.mall", "jd"),
        AppInfo("知乎", "com.zhihu.vip.android", "zhihu"),
        AppInfo("抖音", "com.ss.android.ugc.aweme", "douyin"),
        AppInfo("小红书", "com.xingin.xhs", "xiaohongshu"),
        AppInfo("B站", "tv.danmaku.bili", "bilibili"),
        AppInfo("微信读书", "com.tencent.weread", "weread"),
        AppInfo("微博", "com.sina.weibo", "weibo"),
        AppInfo("豆瓣", "com.douban.frodo", "douban")
    )

    data class AppInfo(val name: String, val packageName: String, val searchPrefix: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkAndRequestPermissions()
    }

    private fun setupUI() {
        // 设置应用选择器
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            supportedApps.map { it.name }
        )
        binding.appSpinner.adapter = adapter

        binding.startButton.setOnClickListener {
            startFocusSearch()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 检查通知权限 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 检查使用情况访问权限
        if (!hasUsageStatsPermission()) {
            showPermissionDialog("使用情况访问权限", "需要此权限来监测您的应用使用情况") {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            return
        }

        // 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            showPermissionDialog("悬浮窗权限", "需要此权限来显示专注提醒") {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivityForResult(intent, REQUEST_CODE_OVERLAY)
            }
            return
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_NOTIFICATIONS
            )
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showPermissionDialog(title: String, message: String, onGrant: () -> Unit) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("授予权限") { _, _ -> onGrant() }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun startFocusSearch() {
        val keyword = binding.keywordEditText.text.toString().trim()
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedAppIndex = binding.appSpinner.selectedItemPosition
        if (selectedAppIndex == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "请选择目标应用", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedApp = supportedApps[selectedAppIndex]
        val intervalMinutes = getSelectedInterval()

        // 启动专注监测服务
        val serviceIntent = Intent(this, FocusMonitorService::class.java).apply {
            putExtra("keyword", keyword)
            putExtra("app_package", selectedApp.packageName)
            putExtra("app_name", selectedApp.name)
            putExtra("search_prefix", selectedApp.searchPrefix)
            putExtra("interval_minutes", intervalMinutes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // 跳转到目标应用
        launchTargetApp(selectedApp, keyword)

        // 显示状态
        binding.statusTextView.text = "正在监测您在${selectedApp.name}的专注状态..."
        binding.statusTextView.visibility = View.VISIBLE
    }

    private fun getSelectedInterval(): Int {
        return when (binding.intervalRadioGroup.checkedRadioButtonId) {
            R.id.radio10Minutes -> 10
            R.id.radio20Minutes -> 20
            R.id.radio30Minutes -> 30
            else -> 20
        }
    }

    private fun launchTargetApp(app: AppInfo, keyword: String) {
        try {
            val packageManager = packageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            
            if (launchIntent != null) {
                // 尝试构建深度链接来直接跳转到搜索页面
                val searchIntent = createSearchIntent(app, keyword)
                if (searchIntent != null) {
                    startActivity(searchIntent)
                } else {
                    startActivity(launchIntent)
                }
            } else {
                Toast.makeText(this, "未找到${app.name}应用", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "启动${app.name}失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSearchIntent(app: AppInfo, keyword: String): Intent? {
        return try {
            val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
            when (app.searchPrefix) {
                "baidu" -> {
                    // 百度搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("baiduboxapp://search?word=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "taobao" -> {
                    // 淘宝搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("taobao://s.taobao.com?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "jd" -> {
                    // 京东搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("openapp.jdmobile://virtual?params={\"category\":\"jump\",\"des\":\"search\",\"keyword\":\"$encodedKeyword\"}")
                        setPackage(app.packageName)
                    }
                }
                "zhihu" -> {
                    // 知乎搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("zhihu://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "douyin" -> {
                    // 抖音搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("snssdk1128://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "xiaohongshu" -> {
                    // 小红书搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("xhsdiscover://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "bilibili" -> {
                    // B站搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("bilibili://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "weread" -> {
                    // 微信读书搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("weread://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "weibo" -> {
                    // 微博搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("sinaweibo://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                "douban" -> {
                    // 豆瓣搜索深度链接
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = android.net.Uri.parse("douban://search?q=$encodedKeyword")
                        setPackage(app.packageName)
                    }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_OVERLAY -> {
                if (Settings.canDrawOverlays(this)) {
                    // 悬浮窗权限已授予
                } else {
                    Toast.makeText(this, "需要悬浮窗权限才能显示提醒", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 停止服务
        stopService(Intent(this, FocusMonitorService::class.java))
    }
}