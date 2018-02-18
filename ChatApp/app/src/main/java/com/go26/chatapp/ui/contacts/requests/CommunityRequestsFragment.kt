package com.go26.chatapp.ui.contacts.requests


import android.os.Bundle
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val position = arguments.getInt("position")
        user = communityRequestsList[position].second
        val communityId = communityRequestsList[position].first
        community = communityMap!![communityId]

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
        activity.supportActionBar?.title = "Community参加リクエスト"
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

        name_text_view.text = user?.name
        if (user?.programmingLanguage != null) {
            val language = "使用言語: " + user?.programmingLanguage
            language_text_view.visibility = View.VISIBLE
            language_text_view.text = language
        }
        if (user?.myApps != null) {
            my_apps_title_text_view.visibility = View.VISIBLE
            my_apps_text_view.visibility = View.VISIBLE
            my_apps_text_view.text = user?.myApps
        }
        loadRoundImage(profile_image_view, user?.imageUrl!!)

        val content = user?.name + "さんから" +  community?.name + "へ参加リクエストが来ています。承認しますか？"
        requests_content_text_view.text = content
        request_confirm_button.setOnClickListener {
            // buttonを押せない様にする
            request_confirm_button.isEnabled = false
            request_disconfirm_button.isEnabled = false

            MyChatManager.setmContext(context)
            MyChatManager.confirmCommunityJoinRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                    fragmentManager.popBackStack()
                    Toast.makeText(context, "リクエストを承認しました", Toast.LENGTH_SHORT).show()
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
                    fragmentManager.beginTransaction().remove(this@CommunityRequestsFragment).commit()
                    fragmentManager.popBackStack()
                    Toast.makeText(context, "リクエストを拒否しました", Toast.LENGTH_SHORT).show()
                }
            }, user?.uid!!, community?.communityId!!, NetworkConstants().DISCONFIRM_REQUEST)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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

        fun newInstance(position: Int): CommunityRequestsFragment {
            val fragment = CommunityRequestsFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
