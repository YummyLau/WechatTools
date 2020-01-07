package com.effective.android.wxrp.data.sp

import android.text.TextUtils
import com.effective.android.wxrp.RpApplication
import java.lang.StringBuilder
import java.util.*

class Config private constructor() {

    companion object {

        private const val SPLIT_POINT = "_&_"

        private const val KEY_USER_WX_NAME = "key_user_wx_nick"
        private var userWxName = ""

        //是否打开自己红包
        private const val KEY_OPEN_GET_SELF_PACKET = "key_open_get_self_packet"
        private var openGetSelfPacket: Boolean = true

        //是否支持过滤
        private const val KEY_OPEN_FILTER_TAG = "key_open_filter_tag"
        private const val KEY_FILTER_TAG_DATA = "filterTagData"           //"测_&_挂_&_专属_&_生日_&_踢"
        private var openFilterTag: Boolean = false
        val filterTags = ArrayList<String>()
        private var defaultTagsString: String = "测_&_挂_&_专属_&_生日_&_踢"

        //是否支持延迟：分为 无延迟，固定延迟和随机延迟
        private const val KEY_OPEN_DELAY_OPTION = "key_delay_option"
        private const val KEY_IS_FIXATION_DELAY = "key_is_fixation_delay"
        private const val KEY_DELAY_TIME_DATA = "delayTimeNum"           //随机最大数 {$fixationDelayTime}_&_{randomDelayTime}
        private var defaultTimesString: String = "100_&_100"
        private var openDelay = false
        private var isFixationDelay = true
        private var fixationDelayTime: Int = 100
        private var randomDelayTime: Int = 100

        /**
         * 应用启动时需要初始化
         */
        fun init() {
            userWxName = RpApplication.sp().getString(KEY_USER_WX_NAME, userWxName)!!

            openGetSelfPacket = RpApplication.sp().getBoolean(KEY_OPEN_GET_SELF_PACKET, openGetSelfPacket)

            val list = RpApplication.sp().getString(KEY_FILTER_TAG_DATA, defaultTagsString)!!.split(SPLIT_POINT).toList()
            filterTags.addAll(list)
            openFilterTag = RpApplication.sp().getBoolean(KEY_OPEN_FILTER_TAG, openFilterTag)

            openDelay = RpApplication.sp().getBoolean(KEY_OPEN_DELAY_OPTION, openDelay)
            isFixationDelay = RpApplication.sp().getBoolean(KEY_IS_FIXATION_DELAY, isFixationDelay)
            val times = RpApplication.sp().getString(KEY_DELAY_TIME_DATA, defaultTimesString)!!.split(SPLIT_POINT).toList()
            if (!times.isEmpty() && times.size == 2) {
                fixationDelayTime = times[0].toInt()
                randomDelayTime = times[1].toInt()
            }
        }

        fun onSave() {
            var tagsString = StringBuilder()
            var isFirst = true
            filterTags.map {
                if (isFirst) {
                    tagsString.append(it)
                    isFirst = false
                } else {
                    tagsString.append(SPLIT_POINT + it)
                }
            }

            var timesString = StringBuilder()
            timesString.append(fixationDelayTime)
            timesString.append(SPLIT_POINT)
            timesString.append(randomDelayTime)

            RpApplication.sp().edit()
                    .putString(KEY_USER_WX_NAME, userWxName)
                    .putBoolean(KEY_OPEN_GET_SELF_PACKET, openGetSelfPacket)
                    .putBoolean(KEY_OPEN_FILTER_TAG, openFilterTag).putString(KEY_FILTER_TAG_DATA, tagsString.toString())
                    .putBoolean(KEY_OPEN_DELAY_OPTION, openDelay).putBoolean(KEY_IS_FIXATION_DELAY, isFixationDelay).putString(KEY_DELAY_TIME_DATA, timesString.toString())
                    .apply()
        }

        fun getUserWxName(): String = userWxName

        fun setUserWxName(userName: String): Boolean {
            if (!TextUtils.isEmpty(userName) && userWxName != userName) {
                userWxName = userName
                return true
            }
            return false
        }

        fun openGetSelfPacket(b: Boolean) {
            openGetSelfPacket = b
        }

        fun isOpenGetSelfPacket(): Boolean = openGetSelfPacket

        fun openFilterTag(b: Boolean) {
            openFilterTag = b
        }

        fun isOpenFilterTag(): Boolean = openFilterTag

        fun openDelay(b: Boolean) {
            openDelay = b
        }

        fun isOpenDelay(): Boolean = openDelay

        fun openFixationDelay(b: Boolean) {
            isFixationDelay = b
        }

        fun isFixationDelay(): Boolean = isFixationDelay


        fun setDelayTime(time: Int) {
            if (isFixationDelay) {
                fixationDelayTime = time
            } else {
                randomDelayTime = time
            }
        }


        fun getDelayTime(b: Boolean): Int {
            return if (openDelay) {
                0
            } else if (isFixationDelay) {
                fixationDelayTime
            } else {
                if (b) {
                    randomDelayTime
                } else {
                    generateRandomNum(randomDelayTime)
                }
            }
        }


        private fun generateRandomNum(n: Int): Int {
            val rand = Random()
            return rand.nextInt(n + 1)
        }
    }
}