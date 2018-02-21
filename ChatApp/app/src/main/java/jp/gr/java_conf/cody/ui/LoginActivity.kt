package jp.gr.java_conf.cody.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.contract.LoginActivityContract
import jp.gr.java_conf.cody.databinding.ActivityLoginBinding
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.SharedPrefManager
import jp.gr.java_conf.cody.viewmodel.LoginActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, LoginActivityContract {
    private val RC_SIGN_IN = 9001
    private var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        currentUser = SharedPrefManager.getInstance(this@LoginActivity).savedUserModel

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        viewModel = LoginActivityViewModel(this, this)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel

        setViews()
    }

    private fun setViews() {

        // loginボタン
        googleLoginButton.setOnClickListener {
            viewModel.setLoginButtonEnabled(false)
            moveToSignInPage()
        }
    }

    override fun onStart() {
        super.onStart()

        if (currentUser != null) {
            MyChatManager.setmContext(this)
            MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    val isFirst = obj as Boolean
                    viewModel.setProgressBarVisibility(View.INVISIBLE)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("isFirst", isFirst)
                    startActivity(intent)
                    finish()
                }

            }, currentUser, NetworkConstants().LOGIN_REQUEST)

        } else {
            viewModel.setLoginButtonEnabled(true)
            viewModel.setProgressBarVisibility(View.INVISIBLE)
        }
    }

    override fun moveToSignInPage() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.setProgressBarVisibility(View.INVISIBLE)
        viewModel.setLoginButtonEnabled(false)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account, auth)
            } catch (e: ApiException) {
                Toast.makeText(this, "signin failed", Toast.LENGTH_SHORT).show()
                viewModel.setLoginButtonEnabled(true)
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        viewModel.setProgressBarVisibility(View.INVISIBLE)
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    override fun firebaseLogin(userModel: UserModel) {
        MyChatManager.setmContext(this)
        MyChatManager.loginCreateAndUpdate(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val isFirst = obj as Boolean
                viewModel.setProgressBarVisibility(View.INVISIBLE)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("isFirst", isFirst)
                startActivity(intent)
                finish()
            }
        }, userModel, NetworkConstants().LOGIN_REQUEST)
    }

    override fun toastSignInError(task: Task<AuthResult>) {
        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
    }
}
