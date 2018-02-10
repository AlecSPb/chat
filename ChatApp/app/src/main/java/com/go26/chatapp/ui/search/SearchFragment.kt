package com.go26.chatapp.ui.search


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.databinding.DataBindingUtil
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.adapter.SearchAdapter
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants.Companion.popularCommunityList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.viewmodel.SearchFragmentViewModel
import com.go26.chatapp.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    lateinit var viewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = SearchFragmentViewModel(activity)
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
        if (popularCommunityList.size == 0) {
            MyChatManager.fetchPopularCommunity(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    Log.d("fetch popular", "success")
                    popularCommunityList = popularCommunityList.asReversed()
                    setAdapter()
                }
            }, NetworkConstants().FETCH_POPULAR_COMMUNITY)
        } else {
            setAdapter()
        }
    }

    private fun setViews() {
        search_button.setOnClickListener {
            val searchRootFragment = SearchRootFragment.newInstance()
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, searchRootFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun setAdapter() {
        search_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = SearchAdapter(context) { position ->
            val intent = Intent(context, CommunityJoinRequestActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("type", AppConstants().POPULAR_COMMUNITY)
            activity.startActivity(intent)
        }

        search_recycler_view.adapter = adapter
    }

    companion object {

        fun newInstance(): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
