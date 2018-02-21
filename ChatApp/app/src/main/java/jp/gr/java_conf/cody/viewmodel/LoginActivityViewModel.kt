package jp.gr.java_conf.cody.viewmodel

import android.content.Context
import android.databinding.ObservableField
import android.view.View
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import jp.gr.java_conf.cody.constants.PrefConstants
import jp.gr.java_conf.cody.contract.LoginActivityContract
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.SecurePrefs
import jp.gr.java_conf.cody.util.SharedPrefManager


/**
 * Created by daigo on 2018/01/13.
 */
class LoginActivityViewModel(val view: LoginActivityContract, val context: Context) {
    var userModel: UserModel? = UserModel()
    var loginButtonIsEnabled: ObservableField<Boolean> = ObservableField(false)
    var progressBarVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

    fun setProgressBarVisibility(visibility: Int) {
//        progressBarVisibility.set(visibility)
    }

    fun setLoginButtonEnabled(enabled: Boolean) {
        loginButtonIsEnabled.set(enabled)
    }

//    fun onLoginButtonClicked(view: View) {
//
//    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?, auth: FirebaseAuth?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        userModel?.uid = user?.uid
                        userModel?.name = user?.displayName
                        userModel?.email = user?.email
                        userModel?.imageUrl = user?.photoUrl.toString()
                        userModel?.online = true
                        SharedPrefManager.getInstance(context).savePreferences(PrefConstants().USER_DATA, Gson().toJson(userModel))
                        user?.let { SecurePrefs(context).put(PrefConstants().USER_ID, it.uid) }
                        user?.email?.let { SecurePrefs(context).put(PrefConstants().USER_EMAIL, it) }
                        userModel?.let { this.view.firebaseLogin(it) }
                    } else {
                        this.view.toastSignInError(task)
                    }
                }
    }
}