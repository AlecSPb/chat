package jp.gr.java_conf.cody.model

import java.io.Serializable

/**
 * Created by daigo on 2018/02/02.
 */
data class FriendModel(var friendId: String? = null,
                       var friendDeleted: Boolean? = null,
                       var joinRequests: HashMap<String, Boolean> = hashMapOf(),
                       var lastMessage: MessageModel? = MessageModel(),
                       var members: HashMap<String, UserModel> = hashMapOf()) : Serializable