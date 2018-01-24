package com.go26.chatapp.model

import java.io.Serializable

/**
 * Created by daigo on 2018/01/14.
 */
data class CommunityModel(var name: String? = null,
                          var imageUrl: String? = null,
                          var communityId: String? = null,
                          var communityDeleted: Boolean? = null,
                          var community: Boolean? = null,
                          var lastMessage: MessageModel? = MessageModel(),
                          var members: HashMap<String, UserModel> = hashMapOf()) : Serializable