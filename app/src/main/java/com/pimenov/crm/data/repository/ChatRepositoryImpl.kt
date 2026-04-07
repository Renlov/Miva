package com.pimenov.crm.data.repository

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.model.Note
import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.usecase.GetChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.InsertChatMessageUseCase
import com.pimenov.crm.core.database.usecase.SaveNoteUseCase
import com.pimenov.crm.core.database.usecase.SaveTaskUseCase
import com.pimenov.crm.domain.agent.AgentResult
import com.pimenov.crm.domain.agent.ExtractedTask
import com.pimenov.crm.domain.agent.IntentParser
import com.pimenov.crm.domain.agent.TextAnalyzerAgent
import com.pimenov.crm.domain.reminder.ReminderScheduler
import com.pimenov.crm.core.domain.repository.ChatRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRepositoryImpl(
    private val insertChatMessage: InsertChatMessageUseCase,
    private val getChatMessages: GetChatMessagesUseCase,
    private val saveNote: SaveNoteUseCase,
    private val saveTask: SaveTaskUseCase,
    private val agent: TextAnalyzerAgent,
    private val intentParser: IntentParser,
    private val reminderScheduler: ReminderScheduler
) : ChatRepository {

    override suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage> {
        val userMsg = ChatMessage(
            conversationId = conversationId,
            role = "user",
            content = userMessage
        )
        insertChatMessage(userMsg)

        // Try local intent parser first (free, offline, instant)
        val localResult = tryLocalParse(userMessage)
        if (localResult != null) {
            val assistantMsg = ChatMessage(
                conversationId = conversationId,
                role = "assistant",
                content = localResult
            )
            insertChatMessage(assistantMsg)
            return Result.success(assistantMsg)
        }

        // Fallback to Gemini cloud agent
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

    /**
     * Try to handle the message locally with regex patterns.
     * Returns formatted reply string if handled, null otherwise.
     */
    private suspend fun tryLocalParse(userMessage: String): String? {
        val parsed = intentParser.tryParse(userMessage) ?: return null

        val task = Task(
            title = parsed.taskTitle,
            reminderAt = parsed.reminderAt
        )
        val taskId = saveTask(task)

        if (parsed.reminderAt != null) {
            reminderScheduler.schedule(taskId, parsed.taskTitle, parsed.reminderAt)
        }

        return buildLocalReply(parsed.taskTitle, parsed.reminderAt)
    }

    private fun buildLocalReply(title: String, reminderAt: Long?): String = buildString {
        append("✅ Задача создана: «$title»")
        if (reminderAt != null) {
            val formatted = DATE_FORMAT.format(Date(reminderAt))
            append("\n\uD83D\uDD14 Напоминание: $formatted")
        }
    }

    private suspend fun saveExtractedItems(result: AgentResult) {
        for (note in result.notes) {
            saveNote(Note(title = note.title, content = note.content))
        }
        for (task in result.tasks) {
            val savedTask = Task(
                title = task.title,
                reminderAt = task.reminderAt
            )
            val taskId = saveTask(savedTask)
            scheduleReminderIfNeeded(taskId, task)
        }
    }

    private fun scheduleReminderIfNeeded(taskId: Long, task: ExtractedTask) {
        val reminderAt = task.reminderAt ?: return
        reminderScheduler.schedule(taskId, task.title, reminderAt)
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
                if (task.reminderAt != null) {
                    val formatted = DATE_FORMAT.format(Date(task.reminderAt))
                    append(" \uD83D\uDD14 $formatted")
                }
            }
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
    }
}
