package com.pimenov.crm.domain.agent

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.pimenov.crm.core.database.model.ChatMessage
import org.json.JSONObject

class TextAnalyzerAgent {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-2.0-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
            systemInstruction = content { text(SYSTEM_PROMPT) }
        )

    suspend fun analyze(
        history: List<ChatMessage>,
        userMessage: String
    ): AgentResult {
        val chatHistory = history.map { msg ->
            val role = if (msg.role == "user") "user" else "model"
            content(role = role) { text(msg.content) }
        }

        val chat = model.startChat(history = chatHistory)
        val response = chat.sendMessage(userMessage)
        val json = response.text ?: return AgentResult(
            reply = "Нет ответа",
            notes = emptyList(),
            tasks = emptyList()
        )

        return parseResponse(json)
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
                    add(ExtractedTask(title = taskObj.optString("title", "")))
                }
            }
        }

        return AgentResult(reply = reply, notes = notes, tasks = tasks)
    }

    companion object {
        private val SYSTEM_PROMPT = """
            You are an intelligent CRM assistant. Your job is to:
            1. Have a helpful conversation with the user
            2. Analyze the user's message for actionable items
            3. Extract notes (important information to remember) and tasks (things to do)

            ALWAYS respond with valid JSON in this exact format:
            {
              "reply": "Your conversational response to the user",
              "notes": [
                {"title": "Short title", "content": "Detailed content of the note"}
              ],
              "tasks": [
                {"title": "Task description"}
              ]
            }

            Rules:
            - "reply" is ALWAYS required — your natural response to the user
            - "notes" — extract when user shares important information, ideas, meeting notes, decisions, facts worth saving
            - "tasks" — extract when user mentions something that needs to be done, deadlines, action items, reminders
            - If there are no notes or tasks to extract, return empty arrays: "notes": [], "tasks": []
            - Keep note titles short (3-5 words), content detailed
            - Keep task titles actionable and clear
            - Respond in the same language the user writes in
        """.trimIndent()
    }
}
