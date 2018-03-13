package jp.gr.java_conf.cody.ui.contacts


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMap
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.CommunityActivityModel
import jp.gr.java_conf.cody.util.NetUtils
import kotlinx.android.synthetic.main.fragment_community_activity_post.*
import java.util.*


class CommunityActivityPostFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    var communityId: String? = null
    private var timestamp: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        communityId = arguments.getString("id")
        return inflater!!.inflate(R.layout.fragment_community_activity_post, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = getString(R.string.activity_post)
        setHasOptionsMenu(true)

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        // focus
        community_activity_layout.setOnTouchListener{ _, _ ->
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(community_activity_layout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            community_activity_layout.requestFocus()
            return@setOnTouchListener true
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        activity_date_edit_text.hint = String.format("%d / %02d / %02d", year, month+1, day)
        activity_date_edit_text.setOnClickListener {
            showDatePicker()
        }

        post_button.setOnClickListener {
            if (NetUtils(context).isOnline()) {
                postActivity()
            } else {
                CDialog(context)
                        .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                        .setDuration(2000)
                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                        .show()
            }
        }
    }

    private fun postActivity() {
        var isValid = true
        var errorMessage = "Validation Error"

        var date: String = activity_date_edit_text.text.toString()
        if (date.isBlank()) {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            cal.clear()
            cal.set(year, month, day, 0, 0)
            date = cal.timeInMillis.toString()
        } else {
            date = timestamp!!
        }

        val location: String = activity_location_edit_text.text.toString()
        if (location.isBlank()) {
            isValid = false
            errorMessage = getString(R.string.activity_location_hint)
        }

        val content: String = activity_content_edit_text.text.toString()
        if (content.isBlank()) {
            isValid = false
            errorMessage = getString(R.string.activity_content_hint)
        }

        MyChatManager.setmContext(context)

        if (isValid) {
            val communityActivityModel = CommunityActivityModel(date = date, location = location, activityContents = content)

            MyChatManager.postCommunityActivity(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    if (communityMap!![communityId!!]?.lastActivity?.activityContents == content) {
                        Toast.makeText(context, "投稿しました", Toast.LENGTH_SHORT).show()
                        fragmentManager.beginTransaction().remove(this@CommunityActivityPostFragment).commit()
                        fragmentManager.popBackStack()
                    } else {
                        var count = 0
                        val handler = Handler()

                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                count++
                                if (count > 30) {

                                    Toast.makeText(context, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                                    return
                                }
                                if (communityMap!![communityId!!]?.lastActivity?.activityContents == content) {
                                    Toast.makeText(context, "投稿しました", Toast.LENGTH_SHORT).show()
                                    fragmentManager.beginTransaction().remove(this@CommunityActivityPostFragment).commit()
                                    fragmentManager.popBackStack()
                                } else {
                                    handler.postDelayed(this, 100)
                                }
                            }
                        }, 100)
                    }
                }
            }, NetworkConstants().POST_COMMUNITY_ACTIVITY, communityId, communityActivityModel)
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerFragment()
        datePicker.setCallBack(this)
        datePicker.show(fragmentManager, "")
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        activity_date_edit_text.setText(String.format("%d / %02d / %02d", year, month+1, day))

        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month, day, 0, 0)
        timestamp = cal.timeInMillis.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                true
            }
            else -> {
                false
            }
        }
    }

    companion object {

        fun newInstance(id: String?): CommunityActivityPostFragment {
            val fragment = CommunityActivityPostFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
