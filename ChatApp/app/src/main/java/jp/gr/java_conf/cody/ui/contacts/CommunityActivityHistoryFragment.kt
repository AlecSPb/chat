package jp.gr.java_conf.cody.ui.contacts


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.CommunityActivityHistoryAdapter
import jp.gr.java_conf.cody.constants.FirebaseConstants
import kotlinx.android.synthetic.main.fragment_community_activity_history.*


class CommunityActivityHistoryFragment : Fragment() {

    var communityId: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        communityId = arguments.getString("id")

        return inflater!!.inflate(R.layout.fragment_community_activity_history, container, false)
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
        activity.supportActionBar?.title = getString(R.string.activity_history)
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

        val ref: Query = FirebaseDatabase.getInstance().reference.child(FirebaseConstants().COMMUNITY_ACTIVITIES)
                ?.child(communityId)!!
        val adapter = CommunityActivityHistoryAdapter(context, ref)
        activity_history_recycler_view.layoutManager = LinearLayoutManager(context)
        activity_history_recycler_view.adapter = adapter
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

        fun newInstance(id: String?): CommunityActivityHistoryFragment {
            val fragment = CommunityActivityHistoryFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
