package com.go26.chatapp.ui.contacts.requests

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import com.bumptech.glide.Glide
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.myFriendsMap
import com.go26.chatapp.model.UserModel
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Created by daigo on 2018/02/16.
 */
class ContactsFriendDetailFragment : Fragment() {
    var id: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        id = arguments.getString("id")

        return inflater!!.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(tool_bar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

        val friend: UserModel? = myFriendsMap[id]

        // 名前
        name_text_view.text = friend?.name

        // 自己紹介
        if (friend?.selfIntroduction != null) {
            self_introduction_text_view.visibility = View.VISIBLE
            self_introduction_text_view.text = friend.selfIntroduction
        }

        // 年齢
        if (friend?.age != null) {
            age_title_line.visibility = View.VISIBLE

            age_title_text_view.visibility = View.VISIBLE
            age_title_text_view.text = "年齢"

            age_text_view.visibility = View.VISIBLE
            val age = friend.age.toString() + "歳"
            age_text_view.text = age

        }
        //　使用言語
        if (friend?.programmingLanguage != null) {
            language_title_line.visibility = View.VISIBLE

            language_title_text_view.visibility = View.VISIBLE
            language_title_text_view.text = "プログラミング言語"

            language_text_view.visibility = View.VISIBLE
            language_text_view.text = friend.programmingLanguage
        }

        // 過去に作ったもの
        if (friend?.myApps != null) {
            my_apps_title_line.visibility = View.VISIBLE

            my_apps_title_text_view.visibility = View.VISIBLE
            my_apps_title_text_view.text = "過去に作ったアプリ"

            my_apps_text_view.visibility = View.VISIBLE
            val made = friend.myApps
            my_apps_text_view.text = made
        }

        // profile画像
        Glide.with(context)
                .load(friend?.imageUrl)
                .into(profile_image_view)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(id: String?): ContactsFriendDetailFragment {
            val fragment = ContactsFriendDetailFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor