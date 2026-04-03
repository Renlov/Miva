package com.pimenov.crm.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequestBody(
    val model: String = "gpt-4o-mini",
    val messages: List<MessageDto>
)

data class MessageDto(
    val role: String,
    val content: String
)

data class ChatResponseBody(
    val choices: List<ChoiceDto>
)

data class ChoiceDto(
    val message: MessageDto,
    @SerializedName("finish_reason") val finishReason: String?
)
