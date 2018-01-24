package com.go26.chatapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
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
        user_name.text = user?.name
        request_button.setOnClickListener {

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
