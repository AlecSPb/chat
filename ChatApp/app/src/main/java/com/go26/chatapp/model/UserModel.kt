package com.go26.chatapp.model

/**
 * Created by daigo on 2018/01/13.
 */
data class UserModel(var uid: String? = null,
                     var name: String? = null,
                     var image_url: String? = null,
                     var email: String? = null,
                     var community: HashMap<String, Boolean> = hashMapOf(),
                     var deviceIds: HashMap<String, String> = hashMapOf(),
                     var online: Boolean? = null,
                     var unread_community_count: Int? = null,
                     var last_seen_online: String? = null,
                     var last_seen_message_timestamp: String? = null,
                     var admin: Boolean? = null,
                     var delete_till: String? = null,
                     var active: Boolean? = null)