package com.go26.chatapp.model

import java.io.Serializable

/**
 * Created by daigo on 2018/02/01.
 */
data class ChatRoomModel(var id: String? = null,
                         var name: String? = null,
                         var imageUrl: String? = null,
                         var lastMessage: String? = null,
                         var unreadCount: Int? = null,
                         var type: String? = null) : Serializable