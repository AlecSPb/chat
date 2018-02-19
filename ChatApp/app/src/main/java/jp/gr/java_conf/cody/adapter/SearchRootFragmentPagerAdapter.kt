package jp.gr.java_conf.cody.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import android.support.v4.view.ViewPager
import jp.gr.java_conf.cody.ui.search.SearchCommunityNameFragment
import jp.gr.java_conf.cody.ui.search.SearchLocationFragment
import jp.gr.java_conf.cody.ui.search.SearchUserFragment


/**
 * Created by daigo on 2018/01/12.
 */
class SearchRootFragmentPagerAdapter(val fragment: FragmentManager) : FragmentStatePagerAdapter(fragment) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return SearchLocationFragment.newInstance()
            }
            1 -> {
                return SearchCommunityNameFragment.newInstance()
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
                return "場所"
            }
            1 -> {
                return "コミュニティ"
            }
            2 -> {
                return "ユーザー"
            }
            else -> {
                return "場所"
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
