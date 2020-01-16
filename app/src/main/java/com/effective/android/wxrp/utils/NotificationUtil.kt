package com.effective.android.wxrp.utils

import android.app.Notification
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.effective.android.wxrp.accessibility.WxNotificationListenerService
import java.lang.reflect.Field


object NotificationUtil {

    @JvmStatic
    fun isNotificationListenersEnabled2(context: Context): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":")
            if (names.isNotEmpty()) {
                for (name in names) {
                    val cn = ComponentName.unflattenFromString(name)
                    if (cn != null && TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    @JvmStatic
    fun isNotificationListenersEnabled(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }

    @JvmStatic
    fun gotoNotificationAccessSetting(context: Context): Boolean {
        return try {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) { //普通情况下找不到的时候需要再特殊处理找一次
            try {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val cn = ComponentName("com.android.settings", "com.android.settings.Settings\$NotificationAccessSettingsActivity")
                intent.component = cn
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings")
                context.startActivity(intent)
                return true
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            false
        }
    }


    /**
     *  在退出app后，再次打开，监听不生效，这个时候我们需要做一些处理。在app启动时，我们去重新关闭打开一次监听服务，让它正常工作。
     */
    @JvmStatic
    fun toggleNotificationListenerService(context: Context) {
        val pm = context.packageManager
        pm.setComponentEnabledSetting(ComponentName(context, WxNotificationListenerService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        pm.setComponentEnabledSetting(ComponentName(context, WxNotificationListenerService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }


    fun getNotificationText(notification: Notification?): List<String> {
        val result = mutableListOf<String>()
        if (notification == null) {
            return result
        }
        var views = notification.bigContentView

        if (views == null) {
            views = notification.contentView
        }

        if (views == null) {
            return result
        }
        try {
            val field: Field = views.javaClass.getDeclaredField("mActions")
            field.isAccessible = true
            val actions = field[views] as ArrayList<Parcelable>
            // Find the setText() and setTime() reflection actions
            for (p in actions) {
                val parcel = Parcel.obtain()
                p.writeToParcel(parcel, 0)
                parcel.setDataPosition(0)
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                val tag = parcel.readInt()
                if (tag != 2) continue
                // View ID
                parcel.readInt()
                val methodName = parcel.readString()
                if (null == methodName) {
                    continue
                } else if (methodName == "setText") { // Parameter type (10 = Character Sequence)
                    parcel.readInt()
                    // Store the actual string
                    val t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim { it <= ' ' }
                    result.add(t)
                }
                parcel.recycle()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }

}