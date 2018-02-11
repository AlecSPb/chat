package com.go26.chatapp.ui.contacts.requests


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*

import com.go26.chatapp.R
import com.go26.chatapp.adapter.CommunityRequestsAdapter
import com.go26.chatapp.adapter.FriendRequestsAdapter
import com.go26.chatapp.constants.DataConstants
import kotlinx.android.synthetic.main.fragment_requests.*


class RequestsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_requests, container, false)
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
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = "リクエスト一覧"
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

        if (DataConstants.friendRequests.isEmpty() && DataConstants.communityRequestsList.isEmpty()) {
            empty_view.visibility = View.VISIBLE
        } else {
            if (!DataConstants.communityRequestsList.isEmpty()) {
                community_requests_title_text_view.visibility = View.VISIBLE
                community_requests_recycler_view.visibility = View.VISIBLE
                community_requests_recycler_view.layoutManager = LinearLayoutManager(context)
                val communityRequestsAdapter = CommunityRequestsAdapter(context, DataConstants.communityRequestsList)
                community_requests_recycler_view.adapter = communityRequestsAdapter
            }

            if (!DataConstants.friendRequests.isEmpty()) {
                friend_requests_title_text_view.visibility = View.VISIBLE
                friend_requests_recycler_view.visibility = View.VISIBLE
                friend_requests_recycler_view.layoutManager = LinearLayoutManager(context)
                val friendRequestsAdapter = FriendRequestsAdapter(context, DataConstants.friendRequests)
                friend_requests_recycler_view.adapter = friendRequestsAdapter
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun newInstance(): RequestsFragment {
            val fragment = RequestsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
