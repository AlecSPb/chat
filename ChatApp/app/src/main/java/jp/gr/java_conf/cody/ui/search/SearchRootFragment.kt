package jp.gr.java_conf.cody.ui.search


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.SearchRootFragmentPagerAdapter
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.contract.SearchRootFragmentContract
import jp.gr.java_conf.cody.databinding.FragmentSearchRootBinding
import jp.gr.java_conf.cody.viewmodel.SearchRootFragmentViewModel
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
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //viewPagerの設定
        val fragmentManager = childFragmentManager

        viewPager = view.findViewById(R.id.search_view_pager)
        viewModel.setSearchHint(0)
        viewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val searchWord = search_edit_text.text.toString()
                    if (searchWord != "") {
                        when (search_edit_text.hint.toString()) {
                            "活動場所で検索" -> {
                                MyChatManager.searchCommunityLocation(object : NotifyMeInterface {
                                    override fun handleData(obj: Any, requestCode: Int?) {
                                        val valid: Boolean = obj as Boolean
                                        if (valid) {
                                            reloadAdapter(0)
                                        }
                                    }
                                }, searchWord, NetworkConstants().SEARCH_LOCATION)
                            }
                            "コミュニティ名で検索" -> {
                                MyChatManager.searchCommunityName(object : NotifyMeInterface {
                                    override fun handleData(obj: Any, requestCode: Int?) {
                                        val valid: Boolean = obj as Boolean
                                        if (valid) {
                                            reloadAdapter(1)
                                        }
                                    }
                                }, searchWord, NetworkConstants().SEARCH_COMUUNITY)
                            }
                            "ユーザーを検索" -> {
                                MyChatManager.searchUserName(object : NotifyMeInterface {
                                    override fun handleData(obj: Any, requestCode: Int?) {
                                        val valid: Boolean = obj as Boolean
                                        if (valid) {
                                            reloadAdapter(2)
                                        }
                                    }
                                }, searchWord, NetworkConstants().SEARCH_USER)
                            }
                        }
                    }
                }
            }

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
        MyChatManager.setmContext(context)
        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchWord = p0.toString()
                when (search_edit_text.hint.toString()) {
                    "活動場所で検索" -> {
                        MyChatManager.searchCommunityLocation(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                val valid: Boolean = obj as Boolean
                                if (valid) {
                                    reloadAdapter(0)
                                }
                            }
                        }, searchWord, NetworkConstants().SEARCH_LOCATION)
                    }
                    "コミュニティ名で検索" -> {
                        MyChatManager.searchCommunityName(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                val valid: Boolean = obj as Boolean
                                if (valid) {
                                    reloadAdapter(1)
                                }
                            }
                        }, searchWord, NetworkConstants().SEARCH_COMUUNITY)
                    }
                    "ユーザーを検索" -> {
                        MyChatManager.searchUserName(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                val valid: Boolean = obj as Boolean
                                if (valid) {
                                    reloadAdapter(2)
                                }
                            }
                        }, searchWord, NetworkConstants().SEARCH_USER)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        back_button.setOnClickListener {
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }

    }

    override fun reloadAdapter(index: Int) {
        viewPager?.let { adapter?.destroyAllItem(it) }
        adapter = SearchRootFragmentPagerAdapter(fragmentManager)
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
