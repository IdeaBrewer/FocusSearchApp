package com.focussearchapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat

class FocusMonitorService : Service() {

    private var keyword: String = ""
    private var targetAppPackage: String = ""
    private var targetAppName: String = ""
    private var searchPrefix: String = ""
    private var intervalMinutes: Int = 20
    
    private val handler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null
    private var isMonitoring = false
    
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "focus_monitor_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            keyword = it.getStringExtra("keyword") ?: ""
            targetAppPackage = it.getStringExtra("app_package") ?: ""
            targetAppName = it.getStringExtra("app_name") ?: ""
            searchPrefix = it.getStringExtra("search_prefix") ?: ""
            intervalMinutes = it.getIntExtra("interval_minutes", 20)
        }

        startForeground(NOTIFICATION_ID, createNotification())
        startMonitoring()
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "专注监测",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "监测您的专注状态"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Focus Search Assistant")
            .setContentText("Monitoring your focus status in $targetAppName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        
        // 延迟开始监测，给用户时间跳转到目标应用
        handler.postDelayed({
            if (isMonitoring) {
                scheduleNextCheck()
            }
        }, 30000) // 30秒后开始第一次检查
    }

    private fun scheduleNextCheck() {
        if (!isMonitoring) return
        
        checkRunnable = object : Runnable {
            override fun run() {
                if (!isMonitoring) return
                
                checkUserActivity()
                
                // 安排下一次检查
                handler.postDelayed(this, (intervalMinutes * 60 * 1000).toLong())
            }
        }
        
        handler.post(checkRunnable!!)
    }

    private fun checkUserActivity() {
        try {
            val currentApp = getCurrentForegroundApp()
            
            if (currentApp == targetAppPackage) {
                // 用户还在目标应用中，显示提醒
                showFocusReminder()
            }
        } catch (e: Exception) {
            // 权限或其他问题，停止监测
            stopMonitoring()
        }
    }

    private fun getCurrentForegroundApp(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
                val time = System.currentTimeMillis()
                
                // 获取最近1分钟内使用过的应用
                val stats = usageStatsManager.queryUsageStats(
                    android.app.usage.UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 60,
                    time
                )
                
                if (stats.isNotEmpty()) {
                    var lastUsedApp: android.app.usage.UsageStats? = null
                    for (usageStats in stats) {
                        // 过滤掉系统应用
                        if (!isSystemApp(usageStats.packageName) && 
                            (lastUsedApp == null || usageStats.lastTimeUsed > lastUsedApp.lastTimeUsed)) {
                            lastUsedApp = usageStats
                        }
                    }
                    return lastUsedApp?.packageName
                }
            } catch (e: SecurityException) {
                // 权限被拒绝，停止监测
                stopMonitoring()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return null
    }

    private fun isSystemApp(packageName: String): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            (packageInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            true // 如果找不到包信息，认为是系统应用
        }
    }

    private fun showFocusReminder() {
        if (!Settings.canDrawOverlays(this)) {
            return
        }

        try {
            hideOverlay() // 先隐藏已有的悬浮窗

            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            overlayView = inflater.inflate(R.layout.focus_reminder_overlay, null)

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            // 设置位置
            layoutParams.x = 0
            layoutParams.y = 100

            // 设置按钮点击事件
            overlayView?.let { view ->
                val completedButton = view.findViewById<Button>(R.id.completedButton)
                val continueButton = view.findViewById<Button>(R.id.continueButton)

                completedButton.setOnClickListener {
                    hideOverlay()
                    stopMonitoring()
                    showToast("恭喜完成专注搜索！")
                }

                continueButton.setOnClickListener {
                    hideOverlay()
                    showToast("继续专注，加油！")
                }
            }

            windowManager?.addView(overlayView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                overlayView = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun stopMonitoring() {
        isMonitoring = false
        checkRunnable?.let {
            handler.removeCallbacks(it)
        }
        hideOverlay()
        
        // 更新通知
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Focus Search Assistant")
            .setContentText("Focus monitoring has ended")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(false)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // 延迟停止服务
        handler.postDelayed({
            stopSelf()
        }, 5000)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
    }
}