package io.uuz.xposed.nosensor

import android.content.pm.ActivityInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.java.IntType


object ActivityHooker: YukiBaseHooker() {
    override fun onHook() {
        findClass("android.app.Activity").hook {
            injectMember {
                method {
                    name = "setRequestedOrientation"
                    param(IntType)
                }
                beforeHook {
                    val requestedOrientation = args(0).int()
                    val replaced = when(requestedOrientation) {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_USER
                        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_USER
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_USER
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_USER
                        else -> requestedOrientation
                    }
                    args(0).set(replaced)
                }
            }.onAllFailure { loggerE("hook Activity#setRequestedOrientation failure ") }
        }.ignoredHookClassNotFoundFailure()
    }
}