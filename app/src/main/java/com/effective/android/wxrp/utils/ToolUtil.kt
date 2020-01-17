package com.effective.android.wxrp.utils

import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.text.TextUtils
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.StringRes
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.version.Version700
import com.effective.android.wxrp.version.Version7010
import com.effective.android.wxrp.version.Version703
import com.effective.android.wxrp.version.VersionManager
import java.text.SimpleDateFormat
import java.util.*


object ToolUtil {
    private const val TAG = "Tools"
    private val YMD_SDF = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TIME_24_SDF = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun installedWeChat(context: Context): Boolean {
        val packageManager = context.packageManager// 获取packagemanager
        val packageInfo = packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (packageInfo != null) {
            for (i in packageInfo.indices) {
                val pn = packageInfo[i].packageName
                if (pn == Constants.weChatPackageName) {
                    return true
                }
            }
        }
        return false
    }

    fun getWeChatVersion(context: Context): String? {
        val packageManager = context.packageManager
        val packageInfos = packageManager.getInstalledPackages(0)
        for (packageInfo in packageInfos) {
            if (packageManager.getApplicationLabel(packageInfo.applicationInfo).toString() == Constants.weChat) {
                return packageInfo.versionName
            }
        }
        return null
    }

    fun supportWeChatVersion(version: String?): Boolean {
        if (TextUtils.isEmpty(version)) {
            return false
        }
        if (version == Version700.VERSION || version == Version703.VERSION || version == Version7010.VERSION) {
            return true
        }
        return false
    }

    /**
     * 判断是否包含红包内容
     * 如果是判断会话，则需要判断是否包含 [微信红包]
     * 如果是判断聊天详情页里面的 item，则需要判断是否包含 微信红包
     */
    @JvmStatic
    fun hasPacketTipWords(content: String, conversation: Boolean = true): Boolean {
        val flag = if (conversation) Constants.weChatPacketTip else Constants.weChatPacket
        val result = !TextUtils.isEmpty(content) && content.contains(flag)
        Logger.i("isRedPacketNode result = $result text($content)")
        return result
    }

    @JvmStatic
    fun hasPacketKeyWords(content: String, originAppendInfo: Boolean = false): Boolean {
        Logger.i("hasPacketKeyWords  ： originAppendInfo : $originAppendInfo")
        val result = LocalizationHelper.isSupportFilterPacket() && isContainKeyWords(LocalizationHelper.getFilterPacketTag(), content)
        Logger.i("hasPacketKeyWords  ： $result  当前content（$content)")
        return result
    }


    @JvmStatic
    fun hasConversationKeyWords(content: String): Boolean {
        val result = LocalizationHelper.isSupportFilterConversation() && isContainKeyWords(LocalizationHelper.getFilterConversationTag(), content)
        Logger.i("hasConversationKeyWords  ： $result  当前content（$content)")
        return result
    }

    /**
     * 默认不抢：
     * 群聊自己发的
     * 私聊自己发的
     */
    fun hasPassByGettingSetting(messageBySelf: Boolean, isGroupChat: Boolean): Boolean {
        val filterGroup = messageBySelf && isGroupChat && !LocalizationHelper.isSupportGettingSelfPacket()
        val filterSingle = !isGroupChat
        val result = !(filterGroup || filterSingle)
        Logger.i("hasPassByGettingSetting  ： $result  当前messageBySelf（$messageBySelf) isGroupChat（$isGroupChat)")
        return result
    }

    @JvmStatic
    fun isContainKeyWords(keyWords: List<String>, content: String): Boolean {
        var result = false
        keyWords.map {
            if (content.contains(it)) {
                result = true
            }
        }
        Logger.i("isContainKeyWords result = $result")
        return result
    }


    fun isServiceRunning(context: Context, className: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceList = activityManager.getRunningServices(1000)
        if (serviceList.size < 0) {
            return false
        }
        for (i in serviceList.indices) {
            if (serviceList[i].service.className == className) {
                return true
            }
        }
        return false
    }


    fun wakeAndUnlock(context: Context) {
        Logger.i(TAG, "wakeAndUnlock")
        //获取电源管理器对象
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        val wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "bright")
        //点亮屏幕
        wl.acquire(1000)
        //得到键盘锁管理器对象
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val kl = km.newKeyguardLock("unLock")
        //解锁,加上会自动解锁，比较危险
        kl.disableKeyguard()
    }

    /** 是否为锁屏或黑屏状态 */
    fun isLockScreen(context: Context): Boolean {
        val result: Boolean
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isInteractive
        } else {
            isScreenOn = pm.isScreenOn
        }
        result = km.inKeyguardRestrictedInputMode() || !isScreenOn
        Logger.i(TAG, "isLockScreen = $result")
        return result
    }

    fun sleepAndLock(context: Context) {
        Logger.i(TAG, "sleepAndLock")
        //获取电源管理器对象
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        val wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright")
        //点亮屏幕
        wl.acquire(1000)
        //得到键盘锁管理器对象
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val kl = km.newKeyguardLock("unLock")
        //锁屏
        kl.reenableKeyguard()
        //释放wakeLock，关灯
        wl.release()
    }


    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    private fun isSameWeekDates(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        val subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true
        }
        return false
    }

    private fun getWeekOfDate(date: Date): String {
        val weekDaysName = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val calendar = Calendar.getInstance()
        calendar.time = date
        val intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        return weekDaysName[intWeek]
    }

    fun getTimeShowString(milliseconds: Long): String {
        var dataString: String?
        var timeStringBy24: String?

        val currentTime = Date(milliseconds)
        val today = Date()
        val todayStart = Calendar.getInstance()
        todayStart.set(Calendar.HOUR_OF_DAY, 0)
        todayStart.set(Calendar.MINUTE, 0)
        todayStart.set(Calendar.SECOND, 0)
        todayStart.set(Calendar.MILLISECOND, 0)
        val todayBegin = todayStart.time
        val yesterdayBegin = Date(todayBegin.time - 3600 * 24 * 1000)
        val preYesterday = Date(yesterdayBegin.time - 3600 * 24 * 1000)

        var containZH = true
        if (!currentTime.before(todayBegin)) {
            dataString = "今天"
        } else if (!currentTime.before(yesterdayBegin)) {
            dataString = "昨天"
        } else if (!currentTime.before(preYesterday)) {
            dataString = "前天"
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime)
        } else {
            dataString = YMD_SDF.format(currentTime)
            containZH = false
        }

        timeStringBy24 = TIME_24_SDF.format(currentTime)
        return if (containZH) {
            "$dataString $timeStringBy24"
        } else {
            dataString!!
        }
    }

    fun insertSort(a: IntArray, b: IntArray) {
        // nodeInofs的数量一般小于10，插入排序效率较高
        var j: Int
        var insertNoteA: Int
        var insertNoteB: Int             // 要插入的数据
        var i = 1
        while (i < a.size) {                // 从数组的第二个元素开始循环将数组中的元素插入
            insertNoteA = a[i]                         // 设置数组中的第2个元素为第一次循环要插入的数据
            insertNoteB = b[i]
            j = i - 1
            while (j >= 0 && insertNoteA < a[j]) {
                a[j + 1] = a[j]             // 如果要插入的元素小于第j个元素,就将第j个元素向后移动
                b[j + 1] = b[j]
                j--
            }
            a[j + 1] = insertNoteA          // 直到要插入的元素不小于第j个元素,将insertNote插入到数组中
            b[j + 1] = insertNoteB
            i++
        }
    }
}
