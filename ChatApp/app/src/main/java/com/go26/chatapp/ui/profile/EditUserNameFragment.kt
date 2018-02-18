package com.go26.chatapp.ui.profile


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
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
        activity.supportActionBar?.title = getString(R.string.edit_name)
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
                var name: String? = null
                if (firstName.isBlank() || lastName.isBlank()) {
                    isValid = false
                    errorMessage = getString(R.string.blank)
                } else {
                    name = firstName + " " + lastName
                    userModel.name = name
                }

                if (isValid) {
                    MyChatManager.setmContext(context)
                    MyChatManager.updateUserName(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            if (currentUser?.name == name) {
                                fragmentManager.popBackStack()
                                fragmentManager.beginTransaction().remove(this@EditUserNameFragment).commit()
                            } else {
                                var count = 0
                                val handler = Handler()

                                handler.postDelayed(object : Runnable {
                                    override fun run() {
                                        count ++
                                        if (count > 30) {
                                            Toast.makeText(context, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                                            return
                                        }
                                        if (currentUser?.name == name) {
                                            fragmentManager.popBackStack()
                                            fragmentManager.beginTransaction().remove(this@EditUserNameFragment).commit()
                                        } else {
                                            handler.postDelayed(this, 100)
                                        }
                                    }
                                }, 100)
                            }
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
