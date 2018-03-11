package jp.gr.java_conf.cody.ui.contacts

import android.app.Dialog
import android.os.Bundle
import android.app.DatePickerDialog
import java.util.*
import android.app.DatePickerDialog.OnDateSetListener
import android.support.v4.app.DialogFragment


/**
 * Created by daigo on 2018/03/08.
 */
class DatePickerFragment : DialogFragment() {
    var onDateSet: OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity,
                onDateSet, year, month, day)
    }

    fun setCallBack(dateSetListener: OnDateSetListener) {
        onDateSet = dateSetListener
    }
}