package com.go26.chatapp.ui.profile


import android.content.Context
import android.os.Bundle
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
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.fragment_edit_profile.*


class EditProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_profile, container, false)
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
        setHasOptionsMenu(true)

        // focus
        edit_profile_layout.setOnTouchListener{ _, _ ->
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(edit_profile_layout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            edit_profile_layout.requestFocus()
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

        name_edit_text.setText(currentUser?.name)
        if (currentUser?.programmingLanguage != null) {
            language_edit_text.setText(currentUser?.programmingLanguage)
        }
        if (currentUser?.whatMade != null) {
            made_edit_text.setText(currentUser?.whatMade)
        }
        loadRoundImage(profile_image_view, currentUser?.imageUrl!!)


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_toolbar_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.finish_edit -> {
                var isValid = true
                var errorMessage = ""
                val userModel = UserModel(uid = currentUser?.uid, imageUrl = currentUser?.imageUrl!!)

                val name = name_edit_text.text.toString()
                if (name.isBlank()) {
                    isValid = false
                    errorMessage = "name is blank"
                } else {
                    userModel.name = name
                }

                val language = language_edit_text.text.toString()
                if (!language.isBlank()) {
                    userModel.programmingLanguage = language
                } else {
                    userModel.programmingLanguage = null
                }

                val made = made_edit_text.text.toString()
                if (!made.isBlank()) {
                    userModel.whatMade = made
                } else {
                    userModel.whatMade = null
                }

                if (isValid) {
                    MyChatManager.setmContext(context)
                    MyChatManager.updateUserInfo(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            Toast.makeText(context, "編集しました", Toast.LENGTH_LONG).show()
                            fragmentManager.popBackStack()
                            fragmentManager.beginTransaction().remove(this@EditProfileFragment).commit()
                        }
                    }, userModel, NetworkConstants().UPDATE_INFO)
                } else {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
