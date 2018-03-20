package jp.gr.java_conf.cody.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.stephentuso.welcome.WelcomeHelper
import jp.gr.java_conf.cody.BottomNavigationViewHelper
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.contract.MainActivityContract
import jp.gr.java_conf.cody.databinding.ActivityMainBinding
import jp.gr.java_conf.cody.model.ChatRoomModel
import jp.gr.java_conf.cody.ui.chat.ChatRoomsFragment
import jp.gr.java_conf.cody.ui.contacts.ContactsFragment
import jp.gr.java_conf.cody.ui.profile.ProfileFragment
import jp.gr.java_conf.cody.ui.search.SearchFragment
import jp.gr.java_conf.cody.util.SharedPrefManager
import jp.gr.java_conf.cody.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), MainActivityContract {
    private var bottomNavigationView: BottomNavigationView? = null
    private var isFirst = false
    private var intro: WelcomeHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyChatManager.setmContext(this)
        currentUser = SharedPrefManager.getInstance(this@MainActivity).savedUserModel
        MyChatManager.setOnlinePresence()

        isFirst = intent.getBooleanExtra("isFirst", false)

        intro = WelcomeHelper(this, IntroActivity::class.java)
        intro!!.show(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = MainViewModel(this, this)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setViews()
        Log.d("Main", "onCreate")
//        MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
//            override fun handleData(obj: Any, requestCode: Int?) {
//                if (requestCode == NetworkConstants().MAIN_LOGIN_REQUEST) {
//                    val isValid = obj as Boolean
//                    if (isValid) {
//                        Log.d("Main", "complete login create and update")
//                        Toast.makeText(this@MainActivity, "complete login create and update", Toast.LENGTH_LONG).show()
//                        initialFetchData()
//                        setFragment()
//                    } else {
//                        Log.d("Main", "failed login create and update")
//                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                        this@MainActivity.startActivity(intent)
//                        this@MainActivity.finish()
//                    }
//                }
//            }
//
//        }, currentUser, NetworkConstants().MAIN_LOGIN_REQUEST)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        intro?.onSaveInstanceState(outState)
    }

    private fun initialFetchData() {
//        MyChatManager.updateFCMTokenAndDeviceId(this@MainActivity, FirebaseInstanceId.getInstance().token!!)

        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                fetchData()
            }

        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, true)
    }

    private fun fetchData() {
        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch current user", "success")
                Toast.makeText(this@MainActivity, "fetch currentUser complete", Toast.LENGTH_LONG).show()
            }

        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch my communities", "success")
            }

        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyFriends(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch my friends", "success")
            }

        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyCommunityRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        }, currentUser, NetworkConstants().FETCH_REQUESTS)

        MyChatManager.fetchMyFriendRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        }, currentUser, NetworkConstants().FETCH_REQUESTS)

        MyChatManager.fetchFriendRequests(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch requests", "success")
            }

        }, currentUser, NetworkConstants().FETCH_REQUESTS)
    }

    private fun setViews() {
        // shiftModeをfalseにする
        bottomNavigationView = findViewById(R.id.navigation)
        val bottomNavigationViewHelper = BottomNavigationViewHelper()
        bottomNavigationView?.let { bottomNavigationViewHelper.disableShiftMode(it) }

    }

    private fun setFragment() {
        // default
        if (isFirst) {
            val profileFragment = ProfileFragment.newInstance(true)
            supportFragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit()
        } else {
            if (bottomNavigationView?.menu?.findItem(R.id.navigation_contacts)?.isChecked!!) {
                val contactsFragment = ContactsFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment, contactsFragment).commit()
            } else if (bottomNavigationView?.menu?.findItem(R.id.navigation_chat)?.isChecked!!) {
                val chatRoomsFragment = ChatRoomsFragment.newInstance(false, ChatRoomModel())
                supportFragmentManager.beginTransaction().replace(R.id.fragment, chatRoomsFragment).commit()
            } else if (bottomNavigationView?.menu?.findItem(R.id.navigation_profile)?.isChecked!!) {
                val profileFragment = ProfileFragment.newInstance(false)
                supportFragmentManager.beginTransaction().replace(R.id.fragment, profileFragment).commit()
            } else {
                val f = fragmentManager.findFragmentByTag("Search")
                if (f == null) {
                    val searchFragment = SearchFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, searchFragment, "Search").commit()
                } else {
                    fragmentManager.beginTransaction().replace(R.id.fragment, f).commit()
                }
            }
        }
    }

    override fun getBottomNavigationViewId(): Int? {
        return bottomNavigationView?.selectedItemId
    }

}
