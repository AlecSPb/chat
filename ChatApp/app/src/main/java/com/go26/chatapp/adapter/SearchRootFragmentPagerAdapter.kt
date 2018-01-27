package com.go26.chatapp.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.go26.chatapp.ui.search.SearchCommunityNameFragment
import com.go26.chatapp.ui.search.SearchLocationFragment
import android.view.ViewGroup
import android.support.v4.view.ViewPager
import com.go26.chatapp.ui.search.SearchUserFragment


/**
 * Created by daigo on 2018/01/12.
 */
class SearchRootFragmentPagerAdapter(val fragment: FragmentManager) : FragmentStatePagerAdapter(fragment) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return SearchCommunityNameFragment.newInstance()
            }
            1 -> {
                return SearchLocationFragment.newInstance()
            }
            2 -> {
                return SearchUserFragment.newInstance()
            }
            else -> {
                return SearchCommunityNameFragment.newInstance()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> {
                return "コミュニティ"
            }
            1 -> {
                return "場所"
            }
            2 -> {
                return "ユーザー"
            }
            else -> {
                return "コミュニティ"
            }
        }
    }

    fun destroyAllItem(pager: ViewPager) {
        for (i in 0 until count) {
            try {
                val obj = this.instantiateItem(pager, i)
                if (obj != null)
                    destroyItem(pager, i, obj)
            } catch (e: Exception) {
            }
        }
    }

    override fun destroyItem(container: ViewGroup?, position: Int, obj: Any) {
        super.destroyItem(container, position, obj)

        if (position <= count) {
            val manager = (obj as Fragment).fragmentManager
            val trans = manager.beginTransaction()
            trans.remove(obj)
            trans.commit()
        }
    }

}
