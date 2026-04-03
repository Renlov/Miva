package com.pimenov.crm.data.remote

import com.pimenov.crm.data.remote.dto.ChatRequestBody
import com.pimenov.crm.data.remote.dto.ChatResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApiService {

    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") auth: String,
        @Body body: ChatRequestBody
    ): ChatResponseBody
}
