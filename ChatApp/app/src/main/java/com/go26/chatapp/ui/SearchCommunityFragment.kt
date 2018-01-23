package com.go26.chatapp.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.go26.chatapp.R


class SearchCommunityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setViews()
        return inflater!!.inflate(R.layout.fragment_search_community, container, false)
    }

    private fun setViews() {}

    companion object {

        fun newInstance(): SearchCommunityFragment {
            val fragment = SearchCommunityFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
