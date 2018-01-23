package com.go26.chatapp.ui


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go26.chatapp.R
import com.go26.chatapp.contract.SearchRootFragmentContract
import com.go26.chatapp.adapter.SearchRootFragmentPagerAdapter
import com.go26.chatapp.viewmodel.SearchRootFragmentViewModel
import com.go26.chatapp.databinding.FragmentSearchRootBinding
import kotlinx.android.synthetic.main.fragment_search_root.*


class SearchRootFragment : Fragment(), SearchRootFragmentContract {

    lateinit var viewModel: SearchRootFragmentViewModel
    private var viewPager: ViewPager? = null
    private var adapter: SearchRootFragmentPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = SearchRootFragmentViewModel(this)
        val binding: FragmentSearchRootBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_root, container, false)
        binding.viewModel = viewModel

        val root = binding.root
        setViews(root)

        return root
    }

    private fun setViews(view: View) {
        //viewPagerの設定
        val fragmentManager = childFragmentManager

        viewPager = view.findViewById(R.id.search_view_pager)
        viewModel.setSearchHint(0)
        viewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                viewModel.setSearchHint(position)
            }
        })

        adapter = SearchRootFragmentPagerAdapter(fragmentManager)
        viewPager?.adapter = adapter
        val tabLayout: TabLayout = view.findViewById(R.id.search_tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        // back buttonイベント
        view.isFocusableInTouchMode = true
        view.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        search_word_button.setOnClickListener {
            if (search_edit_text.text != null) {
                Log.d("search", search_edit_text.hint.toString())
                Log.d("word", search_edit_text.text.toString())

                when (search_edit_text.hint.toString()) {
                    "コミュニティ" -> {
                        reloadAdapter(0)
                    }
                    "場所" -> {
                        reloadAdapter(1)
                    }
                }
            }
        }
    }

    override fun reloadAdapter(index: Int) {
        val adapter = SearchRootFragmentPagerAdapter(fragmentManager)
        viewPager?.let { adapter.destroyAllItem(it) }
        viewPager?.adapter = adapter
        viewPager?.currentItem = index
    }

    companion object {

        fun newInstance(): SearchRootFragment {
            val fragment = SearchRootFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
