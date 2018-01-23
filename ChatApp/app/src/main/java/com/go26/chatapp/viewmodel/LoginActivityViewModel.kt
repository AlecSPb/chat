package com.go26.chatapp.viewmodel

import android.content.Context
import android.databinding.ObservableField
import android.view.View
import com.go26.chatapp.constants.PrefConstants
import com.google.firebase.auth.FirebaseAuth
import com.go26.chatapp.contract.LoginActivityContract
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.SecurePrefs
import com.go26.chatapp.util.SharedPrefManager

import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson


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
                        userModel?.image_url = user?.photoUrl.toString()
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