package jp.gr.java_conf.cody.ui.contacts


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.CommunityMemberAdapter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMap
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.ui.search.FriendRequestActivity
import kotlinx.android.synthetic.main.fragment_community_member.*


class CommunityMemberFragment : Fragment() {
    private var communityModel: CommunityModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val communityId = arguments.getString("communityId")
        communityModel = communityMap!![communityId]

        return inflater!!.inflate(R.layout.fragment_community_member, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "メンバー"
        setHasOptionsMenu(true)

        MyChatManager.setmContext(context)
        MyChatManager.fetchCommunityMember(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val isValid = obj as Boolean
                if (isValid) {
                    val communityMemberAdapter = CommunityMemberAdapter(context) { position ->
                        val intent = Intent(context, FriendRequestActivity::class.java)
                        intent.putExtra("position", position)
                        intent.putExtra("type", "communityMember")
                        activity.startActivity(intent)
                    }
                    community_member__recycler_view.layoutManager = LinearLayoutManager(context)
                    community_member__recycler_view.adapter = communityMemberAdapter
                }
            }
        }, communityModel, NetworkConstants().FETCH_COMMUNITY_MEMBER)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {
        fun newInstance(communityId: String?): CommunityMemberFragment {
            val fragment = CommunityMemberFragment()
            val args = Bundle()
            args.putString("communityId", communityId)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
