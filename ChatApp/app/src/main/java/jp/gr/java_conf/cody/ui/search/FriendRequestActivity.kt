package jp.gr.java_conf.cody.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMemberList
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundUserList
import jp.gr.java_conf.cody.constants.DataConstants.Companion.myFriends
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import jp.gr.java_conf.cody.util.NetUtils
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

                age_text_view.visibility = View.VISIBLE
                val age = user?.age.toString() + "歳"
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

            //　使用言語
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
                val made = user?.myApps
                my_apps_text_view.text = made
            }

            // profile画像
            loadImageFromUrl(profile_image_view, user?.imageUrl!!)

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
                    request_title_line.visibility = View.VISIBLE
                    request_title_text_view.visibility = View.VISIBLE
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
                        request_button.text = getString(R.string.send_friend_request)
                        request_button.setOnClickListener {
                            if (NetUtils(this).isOnline()) {
                                MyChatManager.setmContext(this)
                                MyChatManager.sendFriendRequest(object : NotifyMeInterface {
                                    override fun handleData(obj: Any, requestCode: Int?) {
                                        finish()
                                        Toast.makeText(this@FriendRequestActivity, "フレンドリクエストを送信しました", Toast.LENGTH_SHORT).show()
                                    }
                                }, currentUser!!, user!!, NetworkConstants().SEND_FRIEND_REQUEST)
                            } else {
                                CDialog(this)
                                        .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                                        .setDuration(2000)
                                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                                        .show()
                            }
                        }
                    } else {
                        request_button.text = getString(R.string.in_request)
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
