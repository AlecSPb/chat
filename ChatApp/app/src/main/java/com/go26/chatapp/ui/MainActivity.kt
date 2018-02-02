package com.go26.chatapp.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.go26.chatapp.BottomNavigationViewHelper
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.contract.MainActivityContract
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.viewmodel.MainViewModel
import com.go26.chatapp.databinding.ActivityMainBinding
import com.go26.chatapp.ui.search.SearchFragment
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity(), MainActivityContract {
    private var bottomNavigationView: BottomNavigationView? = null
    var onlineRef: DatabaseReference? = null
    var currentUserRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = MainViewModel(this, this)

        initialFetchData()
//        setViews()
    }

    private fun initialFetchData() {
        MyChatManager.setmContext(this@MainActivity)
        currentUser = SharedPrefManager.getInstance(this@MainActivity).savedUserModel

        MyChatManager.setOnlinePresence()
//        MyChatManager.updateFCMTokenAndDeviceId(this@MainActivity, FirebaseInstanceId.getInstance().token!!)

        MyChatManager.fetchAllUserInformation()

        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                fetchData()
                setViews()
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, true)

//        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
//            override fun handleData(obj: Any, requestCode: Int?) {
//                var i = 0
//                for (group in DataConstants.communityMap!!) {
//                    if (group.value.members.containsKey(currentUser?.uid!!)) {
//                        i += group.value.members.get(currentUser?.uid)?.unreadCount!!
//                    }
//
//                }
//            }
//
//        }, NetworkConstants().FETCH_GROUPS, currentUser, false)

    }

    private fun fetchData() {
        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch current user", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch my communities", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyFriends(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch my friends", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyCommunityRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_REQUESTS)

        MyChatManager.fetchMyFriendRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_REQUESTS)

        MyChatManager.fetchFriendRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        } ,currentUser, NetworkConstants().FETCH_REQUESTS)
    }

    private fun setViews() {
        // shiftModeをfalseにする
        bottomNavigationView = findViewById(R.id.navigation)
        val bottomNavigationViewHelper = BottomNavigationViewHelper()
        bottomNavigationView?.let { bottomNavigationViewHelper.disableShiftMode(it) }

        // default
        val f =fragmentManager.findFragmentByTag("Search")
        if(f == null) {
            val searchFragment = SearchFragment
            supportFragmentManager.beginTransaction().replace(R.id.fragment, searchFragment.newInstance(),"Search").commit()
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragment,f).commit()
        }
    }

    override fun getBottomNavigationViewId(): Int? {
        return bottomNavigationView?.selectedItemId
    }

    override fun onDestroy() {
        MyChatManager.goOffline(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
            }

        }, currentUser, NetworkConstants().GO_OFFLINE)
        super.onDestroy()
    }
}
