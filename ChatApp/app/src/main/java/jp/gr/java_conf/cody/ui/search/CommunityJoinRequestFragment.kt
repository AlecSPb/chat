package jp.gr.java_conf.cody.ui.search


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
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
import jp.gr.java_conf.cody.ui.contacts.CommunityActivityHistoryFragment
import jp.gr.java_conf.cody.ui.contacts.CommunityMemberFragment
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import jp.gr.java_conf.cody.util.NetUtils
import kotlinx.android.synthetic.main.fragment_community_join_request.*
import java.util.*


class CommunityJoinRequestFragment : Fragment() {
    private var communityModel: CommunityModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val pos = arguments.getInt("pos")
        val type = arguments.getString("type")

        when (type) {
            AppConstants().SEARCH_NAME -> {
                communityModel = foundCommunityListByName[pos]
            }
            AppConstants().SEARCH_LOCATION -> {
                communityModel = foundCommunityListByLocation[pos]
            }
            AppConstants().POPULAR_COMMUNITY -> {
                communityModel = popularCommunityList[pos]
            }
        }
        return inflater!!.inflate(R.layout.fragment_community_join_request, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(tool_bar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        if (communityModel != null) {
            // 名前
            name_text_view.text = communityModel?.name

            // 説明
            if (communityModel?.description != null) {
                description_text_view.visibility = View.VISIBLE
                description_text_view.text = communityModel?.description
            }

            // 特徴
            if (communityModel?.feature != null) {
                if (communityModel?.feature != 0) {
                    feature_title_line.visibility = View.VISIBLE

                    feature_title_text_view.visibility = View.VISIBLE

                    feature_text_view.visibility = View.VISIBLE
                    when (communityModel?.feature) {
                        1 -> {
                            val feature = getString(R.string.feature1)
                            feature_text_view.text = feature
                        }
                        2 -> {
                            val feature = getString(R.string.feature2)
                            feature_text_view.text = feature
                        }
                        3 -> {
                            val feature = getString(R.string.feature3)
                            feature_text_view.text = feature
                        }
                        4 -> {
                            val feature = getString(R.string.feature4)
                            feature_text_view.text = feature
                        }
                    }
                }
            }

            // 活動場所
            if (communityModel?.location != null) {
                location_title_line.visibility = View.VISIBLE

                location_title_text_view.visibility = View.VISIBLE
                location_title_text_view.text = getString(R.string.location)

                location_text_view.visibility = View.VISIBLE
                location_text_view.text = communityModel?.location
            }

            // 最近の活動
            if (communityModel?.lastActivity != null) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = communityModel?.lastActivity?.date?.toLong()!!
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                activity_date_text_view.visibility = View.VISIBLE
                activity_date_text_view.text = String.format("%d / %02d / %02d", year, month+1, day)

                activity_location_text_view.visibility = View.VISIBLE
                activity_location_text_view.text = communityModel?.lastActivity?.location

                activity_contents_text_view.visibility = View.VISIBLE
                activity_contents_text_view.text = communityModel?.lastActivity?.activityContents

                activity_button.visibility = View.VISIBLE
                activity_button.setOnClickListener {
                    val communityActivityHistoryFragment = CommunityActivityHistoryFragment.newInstance(communityModel?.communityId)
                    val fragmentManager: FragmentManager = activity.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment, communityActivityHistoryFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            } else {
                activity_empty_text_view.visibility = View.VISIBLE
            }

            // メンバー
            community_member_title_line.visibility = View.VISIBLE

            community_member_title_text_view.visibility = View.VISIBLE

            community_member_button.visibility = View.VISIBLE
            community_member_button.setOnClickListener {
                val communityMemberFragment = CommunityMemberFragment.newInstance(communityModel?.communityId)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityMemberFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

            // profile画像
            loadImageFromUrl(profile_image_view, communityModel?.imageUrl)


            // 自分が所属しているコミュニティの場合、申請ボタン非表示
            var isMyCommunity = false
            if (myCommunities.size != 0) {
                for (myCommunity: CommunityModel in myCommunities) {
                    isMyCommunity = (myCommunity.communityId == communityModel?.communityId)
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
                        if (request.value && request.key == communityModel?.communityId) {
                            isRequested = true
                        }
                    }
                }

                if (!isRequested) {
                    request_button.setOnClickListener {
                        if (NetUtils(context).isOnline()) {
                            MyChatManager.setmContext(context)
                            MyChatManager.sendCommunityJoinRequest(object : NotifyMeInterface {
                                override fun handleData(obj: Any, requestCode: Int?) {
                                    fragmentManager.beginTransaction().remove(this@CommunityJoinRequestFragment).commit()
                                    fragmentManager.popBackStack()
                                    Toast.makeText(context, getString(R.string.sent_community_request), Toast.LENGTH_SHORT).show()
                                }
                            }, currentUser, communityModel!!, NetworkConstants().SEND_COMMUNITY_JOIN_REQUEST)
                        } else {
                            CDialog(context)
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
            } else {
                request_button.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                true
            }
            else -> {
                false
            }
        }
    }

    companion object {

        fun newInstance(type: String, pos: Int): CommunityJoinRequestFragment {
            val fragment = CommunityJoinRequestFragment()
            val args = Bundle()
            args.putString("type", type)
            args.putInt("pos", pos)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
