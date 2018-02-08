package com.go26.chatapp.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.foundCommunityListByName
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.activity_community_join_request.*


class CommunityJoinRequestActivity : AppCompatActivity() {
    var community: CommunityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_join_request)

        val pos = intent.getIntExtra("position", 0)
        community = foundCommunityListByName[pos]
        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.setDisplayShowTitleEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (community != null) {
            name_text_view.text = community?.name

            val location = "活動場所: " + community?.location
            location_text_view.text = location

            val memberCount = "メンバー: " + community?.memberCount.toString() + "人"
            member_count_text_view.text = memberCount

            description_text_view.text = community?.description

            loadRoundImage(profile_image_view, community?.imageUrl!!)

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
                request_button.text = "申請"
                request_button.setOnClickListener {
                    MyChatManager.sendCommunityJoinRequest(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            finish()
                            Toast.makeText(this@CommunityJoinRequestActivity, "参加リクエストを送信しました", Toast.LENGTH_SHORT).show()
                        }
                    }, DataConstants.currentUser!!, community!!, NetworkConstants().SEND_COMMUNITY_JOIN_REQUEST)
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
