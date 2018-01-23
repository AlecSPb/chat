package com.go26.chatapp.viewmodel

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.go26.chatapp.contract.MainActivityContract
import com.go26.chatapp.R
import com.go26.chatapp.ui.ChatRoomsFragment
import com.go26.chatapp.ui.ContactsFragment
import com.go26.chatapp.ui.ProfileFragment
import com.go26.chatapp.ui.SearchFragment

/**
 * Created by daigo on 2018/01/10.
 */
class MainViewModel(private val activity: AppCompatActivity, val view: MainActivityContract) {
    fun onNavigationItemSelected(item: MenuItem) : Boolean {
        val fragmentManager = activity.supportFragmentManager
        val currentId = view.getBottomNavigationViewId()

        when (item.itemId) {
            R.id.navigation_search -> {
//                when (currentId) {
//                    R.id.navigation_home -> {
//                        fragmentManager.beginTransaction().addToBackStack("home").commit()
//                    }
//                    R.id.navigation_profile -> {
//                        fragmentManager.beginTransaction().addToBackStack("profile").commit()
//                    }
//                }

                val searchFragment = SearchFragment.newInstance()
                fragmentManager.beginTransaction().replace(R.id.fragment, searchFragment).commit()
                return true
            }
            R.id.navigation_contacts -> {
//                when (currentId) {
//                    R.id.navigation_home -> {
//                        fragmentManager.beginTransaction().addToBackStack("home").commit()
//                    }
//                    R.id.navigation_profile -> {
//                        fragmentManager.beginTransaction().addToBackStack("profile").commit()
//                    }
//                }

                val contactsFragment = ContactsFragment.newInstance()
                fragmentManager.beginTransaction().replace(R.id.fragment, contactsFragment).commit()
                return true
            }
            R.id.navigation_chat -> {
//                val f =fragmentManager.findFragmentByTag("Home")
//                if(f == null) {
//                    val homeFragment = HomeFragment
//                    fragmentManager.beginTransaction().replace(R.id.fragment, homeFragment.newInstance(),"Home").commit()
//                    return true
//                } else {
//                    fragmentManager.beginTransaction().replace(R.id.fragment,f).commit()
//                }
                val chatRoomsFragment = ChatRoomsFragment.newInstance()
                fragmentManager.beginTransaction().replace(R.id.fragment, chatRoomsFragment).commit()
                return true
            }
            R.id.navigation_profile -> {
//                val f =fragmentManager.findFragmentByTag("Profile")
//                if(f == null) {
//                    val profileFragment = ProfileFragment
//                    fragmentManager.beginTransaction().replace(R.id.fragment, profileFragment.newInstance(),"Profile").commit()
//                    return true
//                } else {
//                    fragmentManager.beginTransaction().replace(R.id.fragment,f).commit()
//                }
                val profileFragment = ProfileFragment.newInstance()
                fragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit()
                return true
            }
        }
        return true
    }
}