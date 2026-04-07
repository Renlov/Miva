package com.pimenov.crm.domain.agent

import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.pimenov.crm.core.database.model.ChatMessage
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class TextAnalyzerAgent {

    private val functions = Firebase.functions

    suspend fun analyze(
        history: List<ChatMessage>,
        userMessage: String
    ): AgentResult {
        val contents = buildList {
            for (msg in history) {
                val role = if (msg.role == "user") "user" else "model"
                add(
                    mapOf(
                        "role" to role,
                        "parts" to listOf(mapOf("text" to msg.content))
                    )
                )
            }
            add(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(mapOf("text" to userMessage))
                )
            )
        }

        val data = mapOf(
            "contents" to contents,
            "systemInstruction" to mapOf(
                "role" to "user",
                "parts" to listOf(mapOf("text" to SYSTEM_PROMPT))
            )
        )

        val result = functions.getHttpsCallable("chat").call(data).await()

        @Suppress("UNCHECKED_CAST")
        val responseMap = result.data as? Map<String, Any?>
        val text = responseMap?.get("text") as? String
            ?: return AgentResult(reply = "Нет ответа", notes = emptyList(), tasks = emptyList())

        return parseResponse(text)
    }

    private fun parseResponse(json: String): AgentResult {
        val obj = JSONObject(json)

        val reply = obj.optString("reply", "")

        val notesArray = obj.optJSONArray("notes")
        val notes = buildList {
            if (notesArray != null) {
                for (i in 0 until notesArray.length()) {
                    val noteObj = notesArray.getJSONObject(i)
                    add(
                        ExtractedNote(
                            title = noteObj.optString("title", ""),
                            content = noteObj.optString("content", "")
                        )
                    )
                }
            }
        }

        val tasksArray = obj.optJSONArray("tasks")
        val tasks = buildList {
            if (tasksArray != null) {
                for (i in 0 until tasksArray.length()) {
                    val taskObj = tasksArray.getJSONObject(i)
                    val reminderIso = taskObj.optString("reminder_at", "")
                    add(
                        ExtractedTask(
                            title = taskObj.optString("title", ""),
                            reminderAt = parseIsoTimestamp(reminderIso)
                        )
                    )
                }
            }
        }

        return AgentResult(reply = reply, notes = notes, tasks = tasks)
    }

    private fun parseIsoTimestamp(iso: String): Long? {
        if (iso.isBlank() || iso == "null") return null
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm", java.util.Locale.ROOT)
                .parse(iso)?.time
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private val SYSTEM_PROMPT = """
            You are an intelligent CRM assistant called MIVA. Your job is to:
            1. Have a helpful conversation with the user
            2. Analyze the user's message for actionable items
            3. Extract notes (important information to remember) and tasks (things to do)
            4. Detect reminders — when user says "напомни", "не забудь", or asks for a reminder,
               set reminder_at to the appropriate ISO datetime

            ALWAYS respond with valid JSON in this exact format:
            {
              "reply": "Your conversational response to the user",
              "notes": [
                {"title": "Short title", "content": "Detailed content of the note"}
              ],
              "tasks": [
                {"title": "Task description", "reminder_at": "2025-01-15T09:00" or null}
              ]
            }

            Rules:
            - "reply" is ALWAYS required — your natural response to the user
            - "notes" — extract when user shares important information, ideas, meeting notes, decisions
            - "tasks" — extract when user mentions something that needs to be done, deadlines, reminders
            - "reminder_at" — ISO datetime (yyyy-MM-ddTHH:mm) when user wants to be reminded.
              Use null if no specific time is mentioned.
              "завтра" = next day at 09:00, "послезавтра" = day after tomorrow at 09:00,
              "в пятницу" = next Friday at 09:00, etc.
            - If there are no notes or tasks, return empty arrays
            - Keep note titles short (3-5 words), content detailed
            - Keep task titles actionable and clear
            - Respond in the same language the user writes in
        """.trimIndent()
    }
}
