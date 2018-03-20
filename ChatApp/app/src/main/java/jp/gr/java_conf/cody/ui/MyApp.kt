package jp.gr.java_conf.cody.ui

import android.app.Application
import android.app.Activity
import android.os.Bundle



/**
 * Created by daigo on 2018/03/20.
 */
class MyApp : Application() {
    private var mAppStatus = AppStatus.FOREGROUND

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

    fun getAppStatus(): AppStatus {
        return mAppStatus
    }

    // check if app is foreground
    fun isForeground(): Boolean {
        return mAppStatus.ordinal > AppStatus.BACKGROUND.ordinal
    }

    enum class AppStatus {
        BACKGROUND, // app is background
        RETURNED_TO_FOREGROUND, // app returned to foreground(or first launch)
        FOREGROUND
        // app is foreground
    }

    inner class MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        // running activity count
        private var running = 0

        override fun onActivityCreated(activity: Activity, bundle: Bundle) {

        }

        override fun onActivityStarted(activity: Activity) {
            if (++running == 1) {
                // running activity is 1,
                // app must be returned from background just now (or first launch)
                mAppStatus = AppStatus.RETURNED_TO_FOREGROUND
            } else if (running > 1) {
                // 2 or more running activities,
                // should be foreground already.
                mAppStatus = AppStatus.FOREGROUND
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            if (--running == 0) {
                // no active activity
                // app goes to background
                mAppStatus = AppStatus.BACKGROUND
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}