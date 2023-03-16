package io.uuz.xposed.nosensor

import android.content.pm.ActivityInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.java.IntType
import android.content.res.Configuration
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.type.defined.VagueType

class ScreenOrientationHooker(private val hookLandscape: Boolean = false) : YukiBaseHooker() {
    private fun replaceScreenOrientation(screenOrientation: Int): Int {
        return when (screenOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            ActivityInfo.SCREEN_ORIENTATION_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_USER
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> ActivityInfo.SCREEN_ORIENTATION_FULL_USER
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            else -> screenOrientation
        }
    }

    override fun onHook() {
        "android.app.ActivityClient".hook {
            injectMember {
                method {
                    name = "setRequestedOrientation"
                    param(VagueType, IntType)
                }
                beforeHook {
                    val origin = args(1).int()
                    val replaced = replaceScreenOrientation(origin)
                    loggerD("info", "replace setRequestedOrientation from $origin to $replaced")
                    args(1).set(replaced)
                }
            }.onHookingFailure {
                loggerE(
                    "hook",
                    "hook ActivityClient#setRequestedOrientation failure",
                    it
                )
            }
        }.onHookClassNotFoundFailure {
            loggerE("hook", "hook ActivityClient failure ", it)
        }

        "android.app.Activity".hook {
            injectMember {
                method {
                    name = "attach"
                    paramCount = 19
                }
                beforeHook {
                    val activityInfo = args(7).cast<ActivityInfo>()!!
                    val replaced = replaceScreenOrientation(activityInfo.screenOrientation)
                    loggerD(
                        "info",
                        "replace attach from ${activityInfo.screenOrientation} to $replaced"
                    )
                    activityInfo.screenOrientation = replaced
                }
            }.onHookingFailure { loggerE("hook", "hook Activity#attach failure", it) }

            injectMember {
                method {
                    name = "onConfigurationChanged"
                    paramCount = 1
                }

                beforeHook {
                    if (!hookLandscape) return@beforeHook
                    val change = args(0).cast<Configuration>()!!
                    loggerD(
                        "info",
                        "replace attach from ${change.orientation} to ${Configuration.ORIENTATION_UNDEFINED}"
                    )
                    change.orientation = Configuration.ORIENTATION_UNDEFINED
                }
            }.onHookingFailure {
                loggerE(
                    "hook",
                    "hook Activity#onConfigurationChanged failure",
                    it
                )
            }
        }.onHookClassNotFoundFailure {
            loggerE("hook", "hook Activity failure ", it)
        }
    }
}