package jp.gr.java_conf.cody.contract

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import jp.gr.java_conf.cody.model.UserModel

/**
 * Created by daigo on 2018/01/13.
 */
interface LoginActivityContract {
    fun firebaseLogin(userModel: UserModel)
    fun toastSignInError(task: Task<AuthResult>)
}