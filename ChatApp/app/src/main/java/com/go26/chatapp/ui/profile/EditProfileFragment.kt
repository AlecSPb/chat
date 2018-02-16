package com.go26.chatapp.ui.profile


import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import com.bumptech.glide.Glide

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import android.support.v7.app.AlertDialog
import android.widget.RelativeLayout
import android.widget.NumberPicker
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel


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
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "プロフィールを編集"
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

        // 名前
        name_text_view.text = currentUser?.name

        // 自己紹介
        if (currentUser?.selfIntroduction != null) {
            self_introduction_text_view.visibility = View.VISIBLE
            self_introduction_text_view.text = currentUser?.selfIntroduction
        }

        // 年齢
        if (currentUser?.age != null) {
            val age = currentUser?.age.toString() + "歳"
            age_edit_button.text = age
        }

        // プログラミング言語
        if (currentUser?.programmingLanguage != null) {
            language_text_view.visibility = View.VISIBLE
            language_text_view.text = currentUser?.programmingLanguage
        }

        // 作ったアプリ
        if (currentUser?.myApps != null) {
            my_apps_text_view.visibility = View.VISIBLE
            my_apps_text_view.text = currentUser?.myApps
        }

        // profile画像
        Glide.with(context)
                .load(currentUser?.imageUrl)
                .into(profile_image_view)

        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        // 名前
        name_edit_button.setOnClickListener {
            val editUserNameFragment = EditUserNameFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editUserNameFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 自己紹介
        self_introduction_edit_button.setOnClickListener {
            val editSelfIntroductionFragment = EditSelfIntroductionFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editSelfIntroductionFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 年齢
        age_edit_button.setOnClickListener {
            val linearLayout = RelativeLayout(context)
            val numberPicker = NumberPicker(context)
            numberPicker.maxValue = 100
            numberPicker.minValue = 0

            val params = RelativeLayout.LayoutParams(50, 50)
            val numPicerParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

            linearLayout.layoutParams = params
            linearLayout.addView(numberPicker, numPicerParams)

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("年齢")
            alertDialogBuilder.setView(linearLayout)
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                             { _, _ ->
                                 val userModel = UserModel(currentUser?.uid)
                                 userModel.age = numberPicker.value

                                 MyChatManager.updateUserAge(object : NotifyMeInterface {
                                     override fun handleData(obj: Any, requestCode: Int?) {

                                         if (currentUser?.age == numberPicker.value) {
                                             val age = numberPicker.value.toString() + "歳"
                                             age_edit_button.text = age

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
                                                     if (currentUser?.age == numberPicker.value) {
                                                         val age = numberPicker.value.toString() + "歳"
                                                         age_edit_button.text = age
                                                     } else {
                                                         handler.postDelayed(this, 100)
                                                     }
                                                 }
                                             }, 100)
                                         }
                                     }
                                 }, userModel, NetworkConstants().UPDATE_INFO)
                             })
                    .setNegativeButton("Cancel",
                             { dialog, _ -> dialog.cancel() })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        // プログラミング言語
        language_edit_button.setOnClickListener {
            val editProgrammingLanguageFragment = EditProgrammingLanguageFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editProgrammingLanguageFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // 作ったアプリ
        my_apps_edit_button.setOnClickListener {
            val editMyAppsFragment = EditMyAppsFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editMyAppsFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
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

            R.id.select_photo -> {
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
