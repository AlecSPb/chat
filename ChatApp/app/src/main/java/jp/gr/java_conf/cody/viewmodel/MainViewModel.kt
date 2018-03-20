package jp.gr.java_conf.cody.viewmodel

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.contract.MainActivityContract
import jp.gr.java_conf.cody.model.ChatRoomModel
import jp.gr.java_conf.cody.ui.chat.ChatRoomsFragment
import jp.gr.java_conf.cody.ui.contacts.ContactsFragment
import jp.gr.java_conf.cody.ui.profile.ProfileFragment
import jp.gr.java_conf.cody.ui.search.SearchFragment

/**
 * Created by daigo on 2018/01/10.
 */
class MainViewModel(private val activity: AppCompatActivity, val view: MainActivityContract) {
    fun onNavigationItemSelected(item: MenuItem) : Boolean {
        if (item.itemId != view.getBottomNavigationViewId()) {
            val fragmentManager = activity.supportFragmentManager

            if (currentUser != null) {
                when (item.itemId) {
                    R.id.navigation_search -> {
                        val searchFragment = SearchFragment.newInstance()
                        fragmentManager.beginTransaction().replace(R.id.fragment, searchFragment).commit()
                        return true
                    }
                    R.id.navigation_contacts -> {
                        val contactsFragment = ContactsFragment.newInstance()
                        fragmentManager.beginTransaction().replace(R.id.fragment, contactsFragment).commit()
                        return true
                    }
                    R.id.navigation_chat -> {
                        val chatRoomsFragment = ChatRoomsFragment.newInstance(false, ChatRoomModel())
                        fragmentManager.beginTransaction().replace(R.id.fragment, chatRoomsFragment).commit()
                        return true
                    }
                    R.id.navigation_profile -> {
                        val profileFragment = ProfileFragment.newInstance(false)
                        fragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit()
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }
}