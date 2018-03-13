package jp.gr.java_conf.cody.ui.search


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.SearchCommunityNameAdapter
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundCommunityListByName
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
        if (foundCommunityListByName.size == 0) {
            empty_view.visibility = View.VISIBLE
        } else {
            search_community_recycler_view.visibility = View.VISIBLE
            search_community_recycler_view.layoutManager = LinearLayoutManager(context)
            val adapter = SearchCommunityNameAdapter(foundCommunityListByName) { position ->
                val communityJoinRequestFragment = CommunityJoinRequestFragment.newInstance(AppConstants().POPULAR_COMMUNITY, position)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityJoinRequestFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            search_community_recycler_view.adapter = adapter
        }
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
