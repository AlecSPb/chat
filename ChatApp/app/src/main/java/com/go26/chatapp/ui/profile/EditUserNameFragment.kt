package com.go26.chatapp.ui.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import kotlinx.android.synthetic.main.fragment_edit_user_name.*


class EditUserNameFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_user_name, container, false)
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
        activity.supportActionBar?.title = "名前を編集"
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

        val firstName = currentUser?.name?.split(Regex("\\s+"))!![0]
        val lastName = currentUser?.name?.split(Regex("\\s+"))!![1]
        first_name_edit_text.setText(firstName)
        last_name_edit_text.setText(lastName)
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
                var isValid = true
                var errorMessage = ""

                val userModel = UserModel(uid = currentUser?.uid)

                val firstName = first_name_edit_text.text.toString()
                val lastName = last_name_edit_text.text.toString()
                if (firstName.isBlank() || lastName.isBlank()) {
                    isValid = false
                    errorMessage = getString(R.string.blank)
                } else {
                    val name = firstName + " " + lastName
                    userModel.name = name
                }

                if (isValid) {
                    MyChatManager.setmContext(context)
                    MyChatManager.updateUserName(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            fragmentManager.popBackStack()
                            fragmentManager.beginTransaction().remove(this@EditUserNameFragment).commit()
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

        fun newInstance(): EditUserNameFragment {
            val fragment = EditUserNameFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
