package com.pimenov.crm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pimenov.crm.domain.model.ChatMessage

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long = 0,
    val role: String = "user",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): ChatMessage = ChatMessage(id, conversationId, role, content, timestamp)

    companion object {
        fun fromDomain(msg: ChatMessage): ChatMessageEntity =
            ChatMessageEntity(msg.id, msg.conversationId, msg.role, msg.content, msg.timestamp)
    }
}
