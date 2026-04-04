package com.pimenov.crm.data.repository

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.usecase.GetChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.InsertChatMessageUseCase
import com.pimenov.crm.core.database.usecase.SaveNoteUseCase
import com.pimenov.crm.core.database.usecase.SaveTaskUseCase
import com.pimenov.crm.domain.agent.AgentResult
import com.pimenov.crm.domain.agent.TextAnalyzerAgent
import com.pimenov.crm.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val insertChatMessage: InsertChatMessageUseCase,
    private val getChatMessages: GetChatMessagesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val saveTask: SaveTaskUseCase,
    private val agent: TextAnalyzerAgent
) : ChatRepository {

    override suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage> {
        val userMsg = ChatMessage(
            conversationId = conversationId,
            role = "user",
            content = userMessage
        )
        insertChatMessage(userMsg)

        return runCatching {
            val allMessages = getChatMessages(conversationId)
            val history = allMessages.dropLast(1)

            val agentResult = agent.analyze(history, userMessage)

            saveExtractedItems(agentResult)

            val replyText = buildReplyText(agentResult)

            val assistantMsg = ChatMessage(
                conversationId = conversationId,
                role = "assistant",
                content = replyText
            )
            insertChatMessage(assistantMsg)
            assistantMsg
        }
    }

    private suspend fun saveExtractedItems(result: AgentResult) {
        for (note in result.notes) {
            saveNote(Note(title = note.title, content = note.content))
        }
        for (task in result.tasks) {
            saveTask(Task(title = task.title))
        }
    }

    private fun buildReplyText(result: AgentResult): String = buildString {
        append(result.reply)

        if (result.notes.isNotEmpty() || result.tasks.isNotEmpty()) {
            append("\n\n---")
        }

        if (result.notes.isNotEmpty()) {
            append("\n\uD83D\uDCDD Создано заметок: ${result.notes.size}")
            for (note in result.notes) {
                append("\n  • ${note.title}")
            }
        }

        if (result.tasks.isNotEmpty()) {
            append("\n\u2705 Создано задач: ${result.tasks.size}")
            for (task in result.tasks) {
                append("\n  • ${task.title}")
            }
        }
    }
}
