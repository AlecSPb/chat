package com.go26.chatapp.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import kotlinx.android.synthetic.main.activity_community_join_request.*


class CommunityJoinRequestActivity : AppCompatActivity() {
    var community: CommunityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_join_request)
        community = DataConstants.foundCommunityListByName?.let { it[intent.getIntExtra("position", 0)] }
        setViews()
    }

    private fun setViews() {
        if (community != null) {
            community_name.text = community?.name

            var isRequested = false
            val currentUser = DataConstants.currentUser
            if (currentUser?.myCommunityRequests?.size != 0) {
                for (request in currentUser?.myCommunityRequests!!) {
                    if (request.value && request.key == community?.communityId) {
                        isRequested = true
                    }
                }

            }

            if (!isRequested) {
                request_button.setOnClickListener {
                    MyChatManager.sendCommunityJoinRequest(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            finish()
                        }
                    }, DataConstants.currentUser!!, community!!, NetworkConstants().SEND_COMMUNITY_JOIN_REQUEST)
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
