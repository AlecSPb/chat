package jp.gr.java_conf.cody.model

import java.io.Serializable

/**
 * Created by daigo on 2018/01/14.
 */
data class CommunityModel(var name: String? = null,
                          var imageUrl: String? = null,
                          var communityId: String? = null,
                          var communityDeleted: Boolean? = null,
                          var community: Boolean? = null,
                          var description: String? = null,
                          var location: String? = null,
                          var feature: Int? = null,
                          var memberCount: Int? = null,
                          var joinRequests: HashMap<String, Boolean> = hashMapOf(),
                          var lastMessage: MessageModel? = MessageModel(),
                          var lastActivity: CommunityActivityModel? = null,
                          var members: HashMap<String, UserModel> = hashMapOf()) : Serializable