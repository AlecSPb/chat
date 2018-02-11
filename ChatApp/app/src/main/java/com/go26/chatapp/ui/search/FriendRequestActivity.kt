package com.go26.chatapp.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.foundUserList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.activity_friend_request.*

class FriendRequestActivity : AppCompatActivity() {
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        val pos = intent.getIntExtra("position", 0)
        user = foundUserList[pos]
        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.setDisplayShowTitleEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (user != null) {
            name_text_view.text = user?.name
            if (user?.programmingLanguage != null) {
                language_text_view.visibility = View.VISIBLE
                val language = "使用言語: " + user?.programmingLanguage
                language_text_view.text = language
            } else {
                language_text_view.visibility = View.GONE
            }

            if (user?.whatMade != null) {
                made_title_text_view.visibility = View.VISIBLE
                made_text_view.visibility = View.VISIBLE
                val made = user?.whatMade
                made_text_view.text = made
            } else {
                made_text_view.visibility = View.GONE
            }

            loadRoundImage(profile_image_view, user?.imageUrl!!)

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
                    }, DataConstants.currentUser!!, user!!, NetworkConstants().SEND_FRIEND_REQUEST)
                }
            } else {
                request_button.text = "申請中"
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
