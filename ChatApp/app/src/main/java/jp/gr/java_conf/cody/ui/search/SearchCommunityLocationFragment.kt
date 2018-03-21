package jp.gr.java_conf.cody.ui.search


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.SearchCommunityLocationAdapter
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityActivityFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityFeatureFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMemberCountFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.filterCount
import jp.gr.java_conf.cody.constants.DataConstants.Companion.foundCommunityListByLocation
import jp.gr.java_conf.cody.constants.NetworkConstants
import kotlinx.android.synthetic.main.fragment_search_community_location.*


class SearchCommunityLocationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // initialize
        filterCount = 0
        communityFeatureFilter = 0
        communityMemberCountFilter = false
        communityActivityFilter = false

        return inflater!!.inflate(R.layout.fragment_search_community_location, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    override fun onStart() {
        super.onStart()

        if (filterCount != 0) {
            val filter = getString(R.string.filter_selected) + filterCount
            filter_button.text = filter
        } else {
            filter_button.text = getString(R.string.filter)
        }
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        back_button.setOnClickListener {
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }

        filter_button.setOnClickListener {
            val intent = Intent(context, SearchFilterActivity::class.java)
            activity.startActivity(intent)
        }

        // edit text
        MyChatManager.setmContext(context)
        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchWord = p0.toString()

                MyChatManager.setmContext(context)
                MyChatManager.searchCommunityLocation(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        val valid: Boolean = obj as Boolean
                        if (valid) {
                            setAdapter()
                        }
                    }
                }, searchWord, NetworkConstants().SEARCH_LOCATION)
            }
        })
    }

    private fun setAdapter() {
        if (foundCommunityListByLocation.size == 0) {
            empty_view.visibility = View.VISIBLE
            search_location_recycler_view.visibility = View.GONE
        } else {
            empty_view.visibility = View.GONE
            search_location_recycler_view.visibility = View.VISIBLE
            search_location_recycler_view.layoutManager = LinearLayoutManager(context)
            val adapter = SearchCommunityLocationAdapter(foundCommunityListByLocation) { position ->
                val communityJoinRequestFragment = CommunityJoinRequestFragment.newInstance(AppConstants().POPULAR_COMMUNITY, position)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityJoinRequestFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            search_location_recycler_view.adapter = adapter
        }
    }

    companion object {

        fun newInstance(): SearchCommunityLocationFragment {
            val fragment = SearchCommunityLocationFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
