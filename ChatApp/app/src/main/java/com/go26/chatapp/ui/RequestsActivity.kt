package com.go26.chatapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.go26.chatapp.R
import com.go26.chatapp.adapter.CommunityRequestsAdapter
import com.go26.chatapp.adapter.FriendRequestsAdapter
import com.go26.chatapp.constants.DataConstants.Companion.communityRequestsList
import com.go26.chatapp.constants.DataConstants.Companion.friendRequests
import kotlinx.android.synthetic.main.activity_requests.*

class RequestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        setViews()
    }

    private fun setViews() {
        friend_requests_recycler_view.layoutManager = LinearLayoutManager(this)
        val friendRequestsAdapter = FriendRequestsAdapter(friendRequests) { position ->

        }
        friend_requests_recycler_view.adapter = friendRequestsAdapter

        community_requests_recycler_view.layoutManager = LinearLayoutManager(this)
        val communityRequestsAdapter = CommunityRequestsAdapter(communityRequestsList) { position ->

        }
        community_requests_recycler_view.adapter = communityRequestsAdapter
    }
}
