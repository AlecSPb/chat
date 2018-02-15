package com.go26.chatapp.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.communityMemberList
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.foundUserList
import com.go26.chatapp.constants.DataConstants.Companion.myFriends
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import kotlinx.android.synthetic.main.activity_friend_request.*

class FriendRequestActivity : AppCompatActivity() {
    var user: UserModel? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        type = intent.getStringExtra("type")
        val pos = intent.getIntExtra("position", 0)

        if (type == "search") {
            user = foundUserList[pos]
        } else if (type == "communityMember") {
            user = communityMemberList[pos]
        }
        setViews()
    }

    private fun setViews() {
        //actionbar
        this.setSupportActionBar(tool_bar)
        this.supportActionBar?.setDisplayShowTitleEnabled(false)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (user != null) {
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
                age_title_text_view.text = "年齢"

                age_text_view.visibility = View.VISIBLE
                val age = user?.age.toString() + "歳"
                age_text_view.text = age

            }
            //　使用言語
            if (user?.programmingLanguage != null) {
                language_title_line.visibility = View.VISIBLE

                language_title_text_view.visibility = View.VISIBLE
                language_title_text_view.text = "プログラミング言語"

                language_text_view.visibility = View.VISIBLE
                language_text_view.text = user?.programmingLanguage
            }

            // 過去に作ったもの
            if (user?.myApps != null) {
                my_apps_title_line.visibility = View.VISIBLE

                my_apps_title_text_view.visibility = View.VISIBLE
                my_apps_title_text_view.text = "過去に作ったアプリ"

                my_apps_text_view.visibility = View.VISIBLE
                val made = user?.myApps
                my_apps_text_view.text = made
            }

            // profile画像
            Glide.with(this)
                    .load(user?.imageUrl)
                    .into(profile_image_view)

            // フレンド申請
            // 自分を除く
            if (user?.uid != currentUser?.uid) {
                // フレンドも除く
                var isMyFriend = false
                if (!myFriends.isEmpty()) {
                    for (myFriend in myFriends) {
                        isMyFriend = (myFriend.uid == user?.uid)
                        if (isMyFriend) break
                    }
                }

                if (!isMyFriend) {
                    request_button.visibility = View.VISIBLE

                    var isRequested = false
                    if (currentUser?.myFriendRequests?.size != 0) {
                        for (request in currentUser?.myFriendRequests!!) {
                            if (request.value && request.key == user?.uid) {
                                isRequested = true
                            }
                        }
                    }

                    if (!isRequested) {
                        request_button.text = "申請"
                        request_button.setOnClickListener {
                            MyChatManager.setmContext(this)
                            MyChatManager.sendFriendRequest(object : NotifyMeInterface {
                                override fun handleData(obj: Any, requestCode: Int?) {
                                    finish()
                                    Toast.makeText(this@FriendRequestActivity, "フレンドリクエストを送信しました", Toast.LENGTH_SHORT).show()
                                }
                            }, currentUser!!, user!!, NetworkConstants().SEND_FRIEND_REQUEST)
                        }
                    } else {
                        request_button.text = "申請中"
                        request_button.isEnabled = false
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
