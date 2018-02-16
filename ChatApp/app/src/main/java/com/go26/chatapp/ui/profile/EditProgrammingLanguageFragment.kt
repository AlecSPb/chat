package com.go26.chatapp.ui.profile


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import kotlinx.android.synthetic.main.fragment_edit_programming_language.*


class EditProgrammingLanguageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_programming_language, container, false)
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
        activity.supportActionBar?.title = getString(R.string.edit_programming_language)
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

        if (currentUser?.programmingLanguage != null) {
            programming_language_edit_text.setText(currentUser?.programmingLanguage)
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
                val userModel = UserModel(uid = currentUser?.uid)

                val programmingLanguage = programming_language_edit_text.text.toString()
                if (!programmingLanguage.isBlank()) {
                    userModel.programmingLanguage = programmingLanguage
                }

                MyChatManager.setmContext(context)
                MyChatManager.updateUserProgrammingLanguage(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        if (currentUser?.programmingLanguage == programmingLanguage) {
                            fragmentManager.popBackStack()
                            fragmentManager.beginTransaction().remove(this@EditProgrammingLanguageFragment).commit()
                        } else {
                            var count = 0
                            val handler = Handler()

                            handler.postDelayed(object : Runnable {
                                override fun run() {
                                    count ++
                                    if (count > 30) {
                                        Toast.makeText(context, "更新に失敗しました。アプリを再起動してください。", Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    if (currentUser?.programmingLanguage == programmingLanguage) {
                                        fragmentManager.popBackStack()
                                        fragmentManager.beginTransaction().remove(this@EditProgrammingLanguageFragment).commit()
                                    } else {
                                        handler.postDelayed(this, 100)
                                    }
                                }
                            }, 100)
                        }
                    }
                }, userModel, NetworkConstants().UPDATE_INFO)

                return true
            }
            else -> {
                return false
            }
        }
    }


    companion object {

        fun newInstance(): EditProgrammingLanguageFragment {
            val fragment = EditProgrammingLanguageFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
