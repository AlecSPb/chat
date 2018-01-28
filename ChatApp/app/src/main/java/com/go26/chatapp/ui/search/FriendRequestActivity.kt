package com.go26.chatapp.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import kotlinx.android.synthetic.main.activity_friend_request.*

class FriendRequestActivity : AppCompatActivity() {
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

        user = DataConstants.foundUserList?.let { it[intent.getIntExtra("position",0)] }
        setViews()
    }

    private fun setViews() {
        if (user != null) {
            user_name.text = user?.name

            var isRequested = false
            if (currentUser?.myFriendRequests?.size != 0) {
                for (request in currentUser?.myFriendRequests!!) {
                    if (request.value && request.key == user?.uid) {
                        isRequested = true
                    }
                }
            }

            if (!isRequested) {
                request_button.setOnClickListener {
                    MyChatManager.sendFriendRequest(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            finish()
                        }
                    }, DataConstants.currentUser!!, user!!, NetworkConstants().SEND_FRIEND_REQUEST)
                }
            } else {
                request_button.text = "申請中"
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
