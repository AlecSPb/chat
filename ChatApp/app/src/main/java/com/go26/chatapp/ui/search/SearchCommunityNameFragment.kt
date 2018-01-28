package com.go26.chatapp.ui.search


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.go26.chatapp.R
import com.go26.chatapp.adapter.SearchCommunityNameAdapter
import com.go26.chatapp.constants.DataConstants
import kotlinx.android.synthetic.main.fragment_search_community.*


class SearchCommunityNameFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_search_community, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        search_community_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = SearchCommunityNameAdapter(DataConstants.foundCommunityList!!) { position ->
            val intent = Intent(context, CommunityJoinRequestActivity::class.java)
            intent.putExtra("position", position)
            activity.startActivity(intent)
        }
        search_community_recycler_view.adapter = adapter
    }

    companion object {

        fun newInstance(): SearchCommunityNameFragment {
            val fragment = SearchCommunityNameFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
