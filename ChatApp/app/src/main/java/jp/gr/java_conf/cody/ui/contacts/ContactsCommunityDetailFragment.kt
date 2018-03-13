package jp.gr.java_conf.cody.ui.contacts


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
import jp.gr.java_conf.cody.constants.DataConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMap
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import jp.gr.java_conf.cody.util.NetUtils
import kotlinx.android.synthetic.main.fragment_contacts_community_detail.*
import java.util.*


class ContactsCommunityDetailFragment : Fragment() {
    var id: String? = null
    private var admin: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        id = arguments.getString("id")

        return inflater!!.inflate(R.layout.fragment_contacts_community_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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

        val communityModel: CommunityModel = communityMap!![id]!!


        if (communityModel.members[currentUser?.uid]?.admin != null) {
            admin = true
        }

        // 名前
        name_text_view.text = communityModel.name

        // 説明
        if (communityModel.description != null) {
            description_text_view.visibility = View.VISIBLE
            description_text_view.text = communityModel.description
        }

        // 特徴
        if (communityModel.feature != null) {
            if (communityModel.feature != 0) {
                feature_title_line.visibility = View.VISIBLE

                feature_title_text_view.visibility = View.VISIBLE

                feature_text_view.visibility = View.VISIBLE
                when (communityModel.feature) {
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
        if (communityModel.location != null) {
            location_title_line.visibility = View.VISIBLE

            location_title_text_view.visibility = View.VISIBLE

            location_text_view.visibility = View.VISIBLE
            location_text_view.text = communityModel.location

        }

        // 最近の活動
        if (communityModel.lastActivity != null) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = communityModel.lastActivity?.date?.toLong()!!
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            activity_date_text_view.visibility = View.VISIBLE
            activity_date_text_view.text = String.format("%d / %02d / %02d", year, month+1, day)

            activity_location_text_view.visibility = View.VISIBLE
            activity_location_text_view.text = communityModel.lastActivity?.location

            activity_contents_text_view.visibility = View.VISIBLE
            activity_contents_text_view.text = communityModel.lastActivity?.activityContents

            activity_button.visibility = View.VISIBLE
            activity_button.setOnClickListener {
                val communityActivityHistoryFragment = CommunityActivityHistoryFragment.newInstance(id)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityActivityHistoryFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        } else {
            activity_empty_text_view.visibility = View.VISIBLE
        }

        if (communityModel.members[currentUser?.uid!!]?.admin != null && communityModel.members[currentUser?.uid!!]?.admin!!) {
            activity_post_button.visibility = View.VISIBLE
            activity_post_button.setOnClickListener {
                val communityActivityPostFragment = CommunityActivityPostFragment.newInstance(id)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityActivityPostFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }

        // メンバー
        community_member_title_line.visibility = View.VISIBLE

        community_member_title_text_view.visibility = View.VISIBLE

        community_member_button.visibility = View.VISIBLE
        community_member_button.setOnClickListener {
            val communityMemberFragment = CommunityMemberFragment.newInstance(id)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, communityMemberFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // profile画像
        loadImageFromUrl(profile_image_view, communityModel.imageUrl!!)

    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if (admin) {
            inflater!!.inflate(R.menu.contacts_detail_for_admin_toolbar_item, menu)
        } else {
            inflater!!.inflate(R.menu.contacts_detail_toolbar_item, menu)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.edit_community -> {
                val editCommunityFragment = EditCommunityFragment.newInstance(id!!)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, editCommunityFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            R.id.leave_community -> {
                if (NetUtils(context).isOnline()) {
                    MyChatManager.setmContext(context)
                    MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            Toast.makeText(context, "You have been exited from community", Toast.LENGTH_LONG).show()
                            fragmentManager.popBackStack()
                            fragmentManager.beginTransaction().remove(this@ContactsCommunityDetailFragment).commit()
                        }
                    }, id, DataConstants.currentUser?.uid)
                } else {
                    CDialog(context)
                            .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                            .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show()
                }
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(id: String?): ContactsCommunityDetailFragment {
            val fragment = ContactsCommunityDetailFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
