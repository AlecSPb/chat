package jp.gr.java_conf.cody.model

/**
 * Created by daigo on 2018/01/13.
 */
data class MessageModel(var message: String? = "",
                        var senderId: String? = "",
                        var senderName: String? = "",
                        var senderImage: String? = "",
                        var timestamp: String? = "",
                        var messageId: String? = "",
                        var readStatus: HashMap<String, Boolean> = hashMapOf())