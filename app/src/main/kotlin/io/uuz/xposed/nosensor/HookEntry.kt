package io.uuz.xposed.nosensor

import com.highcapable.yukihookapi.YukiHookAPI.Configs.debugLog
import com.highcapable.yukihookapi.YukiHookAPI.Configs.isDebug
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() {
        debugLog {
            tag = BuildConfig.APPLICATION_ID
        }
        isDebug = BuildConfig.DEBUG
    }

    override fun onHook() = encase {
        loadApp("com.tencent.qqlive", ScreenOrientationHooker(true))
        loadApp("com.qiyi.video", ScreenOrientationHooker(true))
        loadApp(true, ScreenOrientationHooker())
    }
}