package jp.gr.java_conf.cody.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import jp.gr.java_conf.cody.BottomNavigationViewHelper
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.contract.MainActivityContract
import jp.gr.java_conf.cody.databinding.ActivityMainBinding
import jp.gr.java_conf.cody.ui.profile.ProfileFragment
import jp.gr.java_conf.cody.ui.search.SearchFragment
import jp.gr.java_conf.cody.util.NetUtils
import jp.gr.java_conf.cody.util.SharedPrefManager
import jp.gr.java_conf.cody.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityContract {
    private var bottomNavigationView: BottomNavigationView? = null
    private var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isFirst = intent.getBooleanExtra("isFirst", false)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = MainViewModel(this, this)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setViews()
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            if (currentUser != null) {
                if (NetUtils(this).isOnline()) {
                    Toast.makeText(this, getString(R.string.cannot_login), Toast.LENGTH_SHORT).show()

                    // progress
                    progress_view.visibility = View.VISIBLE
                    avi.visibility = View.VISIBLE
                    avi.show()

                    MyChatManager.setmContext(this)
                    MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            initialFetchData()
                        }

                    }, currentUser, NetworkConstants().LOGIN_REQUEST)
                }
            }
        } else {
            // progress
            progress_view.visibility = View.VISIBLE
            avi.visibility = View.VISIBLE
            avi.show()

            initialFetchData()
        }
    }

    private fun initialFetchData() {
        MyChatManager.setmContext(this@MainActivity)
        currentUser = SharedPrefManager.getInstance(this@MainActivity).savedUserModel

        MyChatManager.setOnlinePresence()
//        MyChatManager.updateFCMTokenAndDeviceId(this@MainActivity, FirebaseInstanceId.getInstance().token!!)

        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                fetchData()
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, true)
    }

    private fun fetchData() {
        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch current user", "success")

                progress_view.visibility = View.GONE
                avi.hide()
            }

        } ,currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, false)

        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                Log.d("fetch my communities", "success")

                progress_view.visibility = View.GONE
                avi.hide()
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
        if (isFirst) {
            val profileFragment = ProfileFragment.newInstance(true)
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

    override fun getBottomNavigationViewId(): Int? {
        return bottomNavigationView?.selectedItemId
    }

}
