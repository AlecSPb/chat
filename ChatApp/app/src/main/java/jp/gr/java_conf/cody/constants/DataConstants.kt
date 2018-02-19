package jp.gr.java_conf.cody.constants

import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.model.FriendModel
import jp.gr.java_conf.cody.model.MessageModel
import jp.gr.java_conf.cody.model.UserModel

/**
 * Created by daigo on 2018/01/14.
 */
class DataConstants {

    companion object {
        var userList: ArrayList<UserModel> = arrayListOf()
        var selectedUserList: ArrayList<UserModel> = arrayListOf()
        var mapList: MutableMap<String, UserModel> = mutableMapOf()
        var myCommunities: MutableList<CommunityModel> = mutableListOf()
        var currentUser: UserModel? = UserModel()
        var myFriends: MutableList<UserModel> = mutableListOf()
        var myFriendsMap: MutableMap<String, UserModel> = mutableMapOf()

        /**
         * Chat
         */
        var communityMessageMap: MutableMap<String, ArrayList<MessageModel>>? = mutableMapOf()
        var communityMap: MutableMap<String, CommunityModel>? = LinkedHashMap()
        var userMap: MutableMap<String, UserModel>? = mutableMapOf()
        var communityMembersMap: MutableMap<String, ArrayList<UserModel>>? = mutableMapOf()
        var friendMap: MutableMap<String, FriendModel> = mutableMapOf()

        // Search
        var popularCommunityList: MutableList<CommunityModel> = mutableListOf()
        var foundCommunityListByName: MutableList<CommunityModel> = mutableListOf()
        var foundCommunityListByLocation: MutableList<CommunityModel> = mutableListOf()
        var foundUserList: MutableList<UserModel> = mutableListOf()

        // Request
        var myFriendRequests: MutableList<UserModel> = mutableListOf()
        var myCommunityRequests: MutableList<CommunityModel> = mutableListOf()
        var friendRequests: MutableList<UserModel> = mutableListOf()
        var communityRequestsList: MutableList<Pair<String, UserModel>> = mutableListOf()

        var myFriendRequestsMap: MutableMap<String, UserModel> = mutableMapOf()
        var myCommunityRequestsMap: MutableMap<String, CommunityModel> = mutableMapOf()
        var friendRequestsMap: MutableMap<String, UserModel> = mutableMapOf()
        var communityRequestsMap: MutableMap<String, MutableList<UserModel>> = mutableMapOf()

        // contacts
        var communityList: MutableList<CommunityModel> = mutableListOf()
        var friendList: MutableList<FriendModel> = mutableListOf()
        var communityMemberList: MutableList<UserModel> = mutableListOf()
    }
}