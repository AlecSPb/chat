package com.go26.chatapp.contract

import com.go26.chatapp.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

/**
 * Created by daigo on 2018/01/13.
 */
interface LoginActivityContract {
    fun moveToSignInPage()
    fun firebaseLogin(userModel: UserModel)
    fun toastSignInError(task: Task<AuthResult>)
}