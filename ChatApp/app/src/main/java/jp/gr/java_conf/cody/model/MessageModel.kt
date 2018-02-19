package jp.gr.java_conf.cody.model

/**
 * Created by daigo on 2018/01/13.
 */
data class MessageModel(var message: String? = "",
                        var sender_id: String? = "",
                        var timestamp: String? = "",
                        var message_id: String? = "",
                        var read_status: HashMap<String, Boolean> = hashMapOf())