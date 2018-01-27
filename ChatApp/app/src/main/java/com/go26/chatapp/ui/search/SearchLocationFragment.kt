package com.go26.chatapp.ui.search


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.go26.chatapp.R


class SearchLocationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setViews()
        return inflater!!.inflate(R.layout.fragment_search_location, container, false)
    }

    private fun setViews() {
    }


    companion object {
        fun newInstance(): SearchLocationFragment {
            val fragment = SearchLocationFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
