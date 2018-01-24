package com.go26.chatapp.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.model.CommunityModel
import kotlinx.android.synthetic.main.activity_community_join_request.*

class CommunityJoinRequestActivity : AppCompatActivity() {
    var community: CommunityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_join_request)
        community = DataConstants.foundCommunityList?.let { it[intent.getIntExtra("position", 0)] }
        setViews()
    }

    private fun setViews() {
        community_name.text = community?.name
        request_button.setOnClickListener {

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
