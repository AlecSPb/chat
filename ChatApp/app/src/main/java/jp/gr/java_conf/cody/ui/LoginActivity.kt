package jp.gr.java_conf.cody.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.twitter.sdk.android.core.*
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.contract.LoginActivityContract
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.NetUtils
import jp.gr.java_conf.cody.util.SharedPrefManager
import jp.gr.java_conf.cody.viewmodel.LoginActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.TwitterAuthProvider
import com.twitter.sdk.android.core.TwitterSession




class LoginActivity : AppCompatActivity(), LoginActivityContract {
    private var auth: FirebaseAuth? = null
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Twitter SDK
        val authConfig = TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret))

        val twitterConfig = TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build()

        Twitter.initialize(twitterConfig)

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        currentUser = SharedPrefManager.getInstance(this@LoginActivity).savedUserModel

        viewModel = LoginActivityViewModel(this, this)

        setViews()
    }

    private fun setViews() {

        // loginボタン
        twitterLoginButton.callback = object : Callback<TwitterSession>() {
            override fun failure(exception: TwitterException?) {
                Log.w("Login", "twitterLogin:failure", exception)
            }

            override fun success(result: Result<TwitterSession>?) {
                twitterLoginButton.isEnabled = false
                Log.d("Login", "twitterLogin:success" + result)
                handleTwitterSession(result?.data!!)

            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (currentUser != null) {
            if (NetUtils(this).isOnline()) {
                // progress
                showProgressDialog()

                MyChatManager.setmContext(this)
                MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        // progress
                        hideProgressDialog()

                        val isFirst = obj as Boolean
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("isFirst", isFirst)
                        startActivity(intent)
                        finish()
                    }

                }, currentUser, NetworkConstants().LOGIN_REQUEST)
            } else {
                twitterLoginButton.isEnabled = true
            }

        } else {
            twitterLoginButton.isEnabled = true
        }
    }

    private fun handleTwitterSession(session: TwitterSession) {
        Log.d("Login", "handleTwitterSession:" + session)
        showProgressDialog()

        val credential = TwitterAuthProvider.getCredential(
                session.authToken.token,
                session.authToken.secret)

        viewModel.firebaseAuthWithTwitter(credential, auth)
    }

    private fun showProgressDialog() {
        progress_view.visibility = View.VISIBLE
        avi.visibility = View.VISIBLE
        avi.show()
    }

    private fun hideProgressDialog() {
        progress_view.visibility = View.GONE
        avi.hide()
        avi.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }

    override fun firebaseLogin(userModel: UserModel) {
        MyChatManager.setmContext(this)
        MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val isFirst = obj as Boolean
                hideProgressDialog()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("isFirst", isFirst)
                startActivity(intent)
                finish()
            }
        }, userModel, NetworkConstants().LOGIN_REQUEST)
    }

    override fun toastSignInError(task: Task<AuthResult>) {
       hideProgressDialog()

        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
    }
}
