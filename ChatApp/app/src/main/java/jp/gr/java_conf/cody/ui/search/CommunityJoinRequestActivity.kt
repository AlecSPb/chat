package jp.gr.java_conf.cody.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundCommunityListByLocation
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundCommunityListByName
import jp.gr.java_conf.cody.constants.DataConstants.Companion.myCommunities
import jp.gr.java_conf.cody.constants.DataConstants.Companion.popularCommunityList
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import kotlinx.android.synthetic.main.activity_community_join_request.*


class CommunityJoinRequestActivity : AppCompatActivity() {
    var community: CommunityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_join_request)

        val pos = intent.getIntExtra("position", 0)
        val type = intent.getStringExtra("type")

        when (type) {
            AppConstants().SEARCH_NAME -> {
                community = foundCommunityListByName[pos]
            }
            AppConstants().SEARCH_LOCATION -> {
                community = foundCommunityListByLocation[pos]
            }
            AppConstants().POPULAR_COMMUNITY -> {
                community = popularCommunityList[pos]
            }
        }

        setViews()
    }

    private fun setViews() {
        //actionbar
        this.setSupportActionBar(tool_bar)
        this.supportActionBar?.setDisplayShowTitleEnabled(false)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (community != null) {
            // 名前
            name_text_view.text = community?.name

            // 説明
            if (community?.description != null) {
                description_text_view.visibility = View.VISIBLE
                description_text_view.text = community?.description
            }

            // 活動場所
            if (community?.location != null) {
                location_title_line.visibility = View.VISIBLE

                location_title_text_view.visibility = View.VISIBLE
                location_title_text_view.text = getString(R.string.location)

                location_text_view.visibility = View.VISIBLE
                location_text_view.text = community?.location
            }

            // メンバー
            community_member_title_line.visibility = View.VISIBLE

            community_member_title_text_view.visibility = View.VISIBLE
            community_member_text_view.visibility = View.VISIBLE
            val memberCount = "メンバー: " + community?.memberCount + "人"
            community_member_text_view.text = memberCount

            // profile画像
            loadImageFromUrl(profile_image_view, community?.imageUrl!!)


            // 自分が所属しているコミュニティの場合、申請ボタン非表示
            var isMyCommunity = false
            if (myCommunities.size != 0) {
                for (myCommunity: CommunityModel in myCommunities) {
                    isMyCommunity = (myCommunity.communityId == community?.communityId)
                    if (isMyCommunity) break
                }
            }

            if (!isMyCommunity) {
                request_title_line.visibility = View.VISIBLE
                request_title_text_view.visibility = View.VISIBLE
                request_button.visibility = View.VISIBLE

                var isRequested = false
                val currentUser = currentUser
                if (currentUser?.myCommunityRequests?.size != 0) {
                    for (request in currentUser?.myCommunityRequests!!) {
                        if (request.value && request.key == community?.communityId) {
                            isRequested = true
                        }
                    }
                }

                if (!isRequested) {
                    request_button.text = getString(R.string.send_community_request)
                    request_button.setOnClickListener {
                        MyChatManager.setmContext(this)
                        MyChatManager.sendCommunityJoinRequest(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                finish()
                                Toast.makeText(this@CommunityJoinRequestActivity, "参加リクエストを送信しました", Toast.LENGTH_SHORT).show()
                            }
                        }, currentUser, community!!, NetworkConstants().SEND_COMMUNITY_JOIN_REQUEST)
                    }
                } else {
                    request_button.text = getString(R.string.in_request)
                    request_button.isEnabled = false
                }
            } else {
                request_button.visibility = View.GONE
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