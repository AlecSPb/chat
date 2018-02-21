package jp.gr.java_conf.cody.util

import android.content.Context
import android.net.ConnectivityManager



/**
 * Created by daigo on 2018/02/21.
 */
class NetUtils(val context: Context) {
    fun isOnline(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
}