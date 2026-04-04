package com.pimenov.crm.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pimenov.crm.core.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT DISTINCT conversationId FROM chat_messages ORDER BY conversationId DESC")
    fun observeConversations(): Flow<List<Long>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessages(conversationId: Long): List<ChatMessageEntity>

    @Insert
    suspend fun insert(entity: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()

    @Query("SELECT COALESCE(MAX(conversationId), 0) + 1 FROM chat_messages")
    suspend fun nextConversationId(): Long
}
