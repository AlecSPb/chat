package jp.gr.java_conf.cody.util

import android.text.InputFilter
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by daigo on 2018/01/14.
 */
class MyTextUtil {
    fun getTimestamp(milliseconds: Long): String =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(milliseconds)) + " " +
                    SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date(milliseconds)).replace("", "").toLowerCase()

    fun getTime(milliseconds: Long): String = DateFormat.format("yyyy/MM/dd, E, kk:mm", milliseconds).toString()

    fun getEmojiFIlter(): Array<InputFilter> {
        return arrayOf(InputFilter { src, _, _, _, _, _ ->
            if (src == "") { // for backspace
                return@InputFilter src
            }
            if (src.toString().matches("[\\x00-\\x7F]+".toRegex())) {
                src
            } else ""
        })
    }

    fun isValidPhoneNumber(phoneNumber: CharSequence): Boolean {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches()
        }
        return false
    }

    fun getHash(a: String, b: String): String {
        var result: Long = 17
        result = 37 * result + a.hashCode().toLong() + b.hashCode().toLong()
        return result.toString()
    }

}