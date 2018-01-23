package com.go26.chatapp.constants

import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.MessageModel
import com.go26.chatapp.model.UserModel

/**
 * Created by daigo on 2018/01/14.
 */
class DataConstants {

    companion object {
        var userList: ArrayList<UserModel>? = ArrayList()
        var selectedUserList: ArrayList<UserModel>? = ArrayList()
        var mapList: MutableMap<String, UserModel> = mutableMapOf()
        var myCommunities: ArrayList<CommunityModel>? = ArrayList()
        var currentUser: UserModel? = UserModel()

        /**
         * Chat
         */
        var communityMessageMap: MutableMap<String, ArrayList<MessageModel>>? = mutableMapOf()
        var communityMap: MutableMap<String, CommunityModel>? = LinkedHashMap()
        var userMap: MutableMap<String, UserModel>? = mutableMapOf()
        var communityMembersMap: MutableMap<String, ArrayList<UserModel>>? = mutableMapOf()
    }
}