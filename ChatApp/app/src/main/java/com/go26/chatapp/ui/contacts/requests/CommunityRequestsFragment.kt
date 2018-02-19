package com.go26.chatapp.ui.contacts.requests


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.communityRequestsList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.fragment_community_requests.*


class CommunityRequestsFragment : Fragment() {
    var user: UserModel? = null
    var community: CommunityModel? = null
    var communityId: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val position = arguments.getInt("position")
        user = communityRequestsList[position].second
        communityId = communityRequestsList[position].first
        community = communityMap!![communityId!!]

        return inflater!!.inflate(R.layout.fragment_community_requests, container, false)
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
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = getString(R.string.community_request_title)
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
        name_text_view.text = user?.name

        // 自己紹介
        if (user?.selfIntroduction != null) {
            self_introduction_text_view.visibility = View.VISIBLE
            self_introduction_text_view.text = user?.selfIntroduction
        }

        // 年齢
        if (user?.age != null) {
            age_title_line.visibility = View.VISIBLE

            age_title_text_view.visibility = View.VISIBLE

            age_text_view.visibility = View.VISIBLE
            val age = user?.age.toString() + getString(R.string.age_content)
            age_text_view.text = age
        }

        // 開発経験
        if (user?.developmentExperience != null) {
            experience_title_line.visibility = View.VISIBLE

            experience_title_text_view.visibility = View.VISIBLE

            experience_text_view.visibility = View.VISIBLE
            when (user?.developmentExperience) {
                0 -> {
                    val experience = getString(R.string.experience0)
                    experience_text_view.text = experience
                }
                1 -> {
                    val experience = getString(R.string.experience1)
                    experience_text_view.text = experience
                }
                2 -> {
                    val experience = getString(R.string.experience2)
                    experience_text_view.text = experience
                }
                3 -> {
                    val experience = getString(R.string.experience3)
                    experience_text_view.text = experience
                }
            }
        }

        // プログラミング言語
        if (user?.programmingLanguage != null) {
            language_title_line.visibility = View.VISIBLE

            language_title_text_view.visibility = View.VISIBLE

            language_text_view.visibility = View.VISIBLE
            language_text_view.text = user?.programmingLanguage
        }

        // 過去に作ったもの
        if (user?.myApps != null) {
            my_apps_title_line.visibility = View.VISIBLE

            my_apps_title_text_view.visibility = View.VISIBLE

            my_apps_text_view.visibility = View.VISIBLE
            my_apps_text_view.text = user?.myApps
        }

        // profile画像
        loadRoundImage(profile_image_view, user?.imageUrl!!)

        // request
        val content = user?.name + "さんから" +  community?.name + "へ参加リクエストが来ています。承認しますか？"
        requests_content_text_view.text = content
        request_confirm_button.setOnClickListener {
            // buttonを押せない様にする
            request_confirm_button.isEnabled = false
            request_disconfirm_button.isEnabled = false

            MyChatManager.setmContext(context)
            MyChatManager.confirmCommunityJoinRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    // fetch されるまでfragmentをremoveしない
                    var isExist = false
                    communityRequestsList
                            .filter { request -> request.first == communityId && request.second.uid == user?.uid }
                            .forEach { isExist = true }

                    if (!isExist) {
                        fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                        fragmentManager.popBackStack()
                        Toast.makeText(context, getString(R.string.confirm_toast), Toast.LENGTH_SHORT).show()
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

                                isExist = false
                                communityRequestsList
                                        .filter { request -> request.first == communityId && request.second.uid == user?.uid }
                                        .forEach { isExist = true }

                                if (!isExist) {
                                    fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                                    fragmentManager.popBackStack()
                                    Toast.makeText(context, getString(R.string.confirm_toast), Toast.LENGTH_SHORT).show()
                                } else {
                                    handler.postDelayed(this, 100)
                                }
                            }
                        }, 100)
                    }
                }
            }, user?.uid!!, community?.communityId!!, NetworkConstants().CONFIRM_REQUEST)
        }

        request_disconfirm_button.setOnClickListener{
            // buttonを押せない様にする
            request_confirm_button.isEnabled = false
            request_disconfirm_button.isEnabled = false

            MyChatManager.setmContext(context)
            MyChatManager.disconfirmCommunityJoinRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    // fetch されるまでfragmentをremoveしない
                    var isExist = false
                    communityRequestsList
                            .filter { request -> request.first == communityId && request.second.uid == user?.uid }
                            .forEach { isExist = true }

                    if (!isExist) {
                        fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                        fragmentManager.popBackStack()
                        Toast.makeText(context, getString(R.string.disconfirm_toast), Toast.LENGTH_SHORT).show()
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

                                isExist = false
                                communityRequestsList
                                        .filter { request -> request.first == communityId && request.second.uid == user?.uid }
                                        .forEach { isExist = true }

                                if (!isExist) {
                                    fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                                    fragmentManager.popBackStack()
                                    Toast.makeText(context, getString(R.string.disconfirm_toast), Toast.LENGTH_SHORT).show()
                                } else {
                                    handler.postDelayed(this, 100)
                                }
                            }
                        }, 100)
                    }
                }
            }, user?.uid!!, community?.communityId!!, NetworkConstants().DISCONFIRM_REQUEST)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
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

        fun newInstance(position: Int): CommunityRequestsFragment {
            val fragment = CommunityRequestsFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
