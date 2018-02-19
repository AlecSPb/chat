package jp.gr.java_conf.cody.ui.search


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.SearchLocationAdapter
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundCommunityListByLocation
import kotlinx.android.synthetic.main.fragment_search_location.*


class SearchLocationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_search_location, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        if (foundCommunityListByLocation.size == 0) {
            empty_view.visibility = View.VISIBLE
        } else {
            search_location_recycler_view.visibility = View.VISIBLE
            search_location_recycler_view.layoutManager = LinearLayoutManager(context)
            val adapter = SearchLocationAdapter(foundCommunityListByLocation) { position ->
                val intent = Intent(context, CommunityJoinRequestActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("type", AppConstants().SEARCH_LOCATION)
                activity.startActivity(intent)
            }
            search_location_recycler_view.adapter = adapter
        }
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
