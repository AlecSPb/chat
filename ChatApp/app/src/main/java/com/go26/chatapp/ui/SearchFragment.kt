package com.go26.chatapp.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.databinding.DataBindingUtil
import android.support.v4.app.FragmentTransaction
import com.go26.chatapp.R
import com.go26.chatapp.viewmodel.SearchFragmentViewModel
import com.go26.chatapp.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_root.*


class SearchFragment : Fragment() {

    lateinit var viewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = SearchFragmentViewModel(activity)
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.viewModel = viewModel

        val root = binding.root
        setViews(root)

        return root
    }

    private fun setViews(view: View) {

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
    companion object {

        fun newInstance(): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
