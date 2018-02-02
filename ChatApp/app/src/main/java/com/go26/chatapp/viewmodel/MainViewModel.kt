package com.go26.chatapp.viewmodel

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.go26.chatapp.contract.MainActivityContract
import com.go26.chatapp.R
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.ui.ChatRoomsFragment
import com.go26.chatapp.ui.ContactsFragment
import com.go26.chatapp.ui.ProfileFragment
import com.go26.chatapp.ui.search.SearchFragment

/**
 * Created by daigo on 2018/01/10.
 */
class MainViewModel(private val activity: AppCompatActivity, val view: MainActivityContract) {
    fun onNavigationItemSelected(item: MenuItem) : Boolean {
        val fragmentManager = activity.supportFragmentManager

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
                val profileFragment = ProfileFragment.newInstance()
                fragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit()
                return true
            }
        }
        return true
    }
}