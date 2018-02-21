package jp.gr.java_conf.cody.ui.profile


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.NetUtils
import kotlinx.android.synthetic.main.fragment_edit_self_introduction.*


class EditSelfIntroductionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_self_introduction, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = getString(R.string.edit_self_introduction)
        setHasOptionsMenu(true)

        // focus
        parent_layout.setOnTouchListener{ _, _ ->
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(parent_layout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            parent_layout.requestFocus()
            return@setOnTouchListener true
        }

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        if (currentUser?.selfIntroduction != null) {
            self_introduction_edit_text.setText(currentUser?.selfIntroduction)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_finish_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.edit_finish -> {
                if (NetUtils(context).isOnline()) {
                    val userModel = UserModel(uid = currentUser?.uid)

                    val selfIntroduction = self_introduction_edit_text.text.toString()
                    if (!selfIntroduction.isBlank()) {
                        userModel.selfIntroduction = selfIntroduction
                    }

                    MyChatManager.setmContext(context)
                    MyChatManager.updateSelfIntroduction(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            if (currentUser?.selfIntroduction == selfIntroduction) {
                                fragmentManager.popBackStack()
                                fragmentManager.beginTransaction().remove(this@EditSelfIntroductionFragment).commit()
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
                                        if (currentUser?.selfIntroduction == selfIntroduction) {
                                            fragmentManager.popBackStack()
                                            fragmentManager.beginTransaction().remove(this@EditSelfIntroductionFragment).commit()
                                        } else {
                                            handler.postDelayed(this, 100)
                                        }
                                    }
                                }, 100)
                            }
                        }
                    }, userModel, NetworkConstants().UPDATE_INFO)
                } else {
                    CDialog(context)
                            .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                            .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show()
                }
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(): EditSelfIntroductionFragment {
            val fragment = EditSelfIntroductionFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
