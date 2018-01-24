package com.go26.chatapp.ui


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.go26.chatapp.R
import com.go26.chatapp.adapter.SearchUserAdapter
import com.go26.chatapp.constants.DataConstants
import kotlinx.android.synthetic.main.fragment_search_user.*


class SearchUserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_search_user, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        search_user_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = SearchUserAdapter(DataConstants.foundUserList!!) { position ->
            val intent = Intent(context, FriendRequestActivity::class.java)
            intent.putExtra("position", position)
            activity.startActivity(intent)
        }
        search_user_recycler_view.adapter = adapter
    }

    companion object {

        fun newInstance(): SearchUserFragment {
            val fragment = SearchUserFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
