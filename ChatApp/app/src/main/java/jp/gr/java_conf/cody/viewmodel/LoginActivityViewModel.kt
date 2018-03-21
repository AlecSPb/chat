package jp.gr.java_conf.cody.viewmodel

import android.content.Context
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
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

    fun setLoginButtonEnabled(enabled: Boolean) {
        loginButtonIsEnabled.set(enabled)
    }

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

    fun firebaseAuthWithTwitter(credential: AuthCredential, auth: FirebaseAuth?) {
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "signInWithCredential:success")
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
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "signInWithCredential:failure", task.exception)
                        this.view.toastSignInError(task)
                    }
                }
    }
}