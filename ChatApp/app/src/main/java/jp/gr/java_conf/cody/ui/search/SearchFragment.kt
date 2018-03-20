package jp.gr.java_conf.cody.ui.search


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.databinding.DataBindingUtil
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.SearchAdapter
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.popularCommunityList
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.databinding.FragmentSearchBinding
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.viewmodel.SearchFragmentViewModel
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

        Log.d("test", "search Fragment")

        MyChatManager.setmContext(context)
        MyChatManager.fetchPopularCommunity(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch popular", "success")

                val isValid = obj as Boolean
                if (isValid) {
                    if (popularCommunityList.isEmpty()) {
                        empty_view.visibility = View.VISIBLE
                    } else {
                        Log.d("test", "fetch success")
                        search_scroll_view.visibility = View.VISIBLE
                        popularCommunityList = popularCommunityList.sortedWith(compareByDescending(CommunityModel::memberCount)).toMutableList()
                        setAdapter()
                    }
                }
            }
        }, NetworkConstants().FETCH_POPULAR_COMMUNITY)

    }

    private fun setViews() {
        //bottomNavigationView　表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.VISIBLE
        bottomNavigationView.menu.findItem(R.id.navigation_search).isChecked = true

        search_button.setOnClickListener {
            val searchCommunityLocationFragment = SearchCommunityLocationFragment.newInstance()
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, searchCommunityLocationFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun setAdapter() {
        search_recycler_view.isNestedScrollingEnabled = false
        search_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = SearchAdapter(context) { position ->
            val communityJoinRequestFragment = CommunityJoinRequestFragment.newInstance(AppConstants().POPULAR_COMMUNITY, position)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, communityJoinRequestFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
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
