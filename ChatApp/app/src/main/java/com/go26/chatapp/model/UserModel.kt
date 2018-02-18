package com.go26.chatapp.model

import java.io.Serializable

/**
 * Created by daigo on 2018/01/13.
 */
data class UserModel(var uid: String? = null,
                     var name: String? = null,
                     var imageUrl: String? = null,
                     var email: String? = null,
                     var communities: HashMap<String, Boolean> = hashMapOf(),
                     var friends: HashMap<String, Boolean> = hashMapOf(),
                     var friendRequests: HashMap<String, Boolean> = hashMapOf(),
                     var myFriendRequests: HashMap<String, Boolean> = hashMapOf(),
                     var myCommunityRequests: HashMap<String, Boolean> = hashMapOf(),
                     var deviceIds: HashMap<String, String> = hashMapOf(),
                     var online: Boolean? = null,
                     var unreadCount: Int? = null,
                     var lastSeenOnline: String? = null,
                     var lastSeenMessageTimestamp: String? = null,
                     var admin: Boolean? = null,
                     var deleteTill: String? = null,
                     var joinTime: String? = null,
                     var programmingLanguage: String? = null,
                     var age: Int? = null,
                     var myApps: String? = null,
                     var selfIntroduction: String? = null,
                     var developmentExperience: Int? = null,
                     var active: Boolean? = null) : Serializable