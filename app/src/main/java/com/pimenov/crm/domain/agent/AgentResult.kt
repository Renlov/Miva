package com.pimenov.crm.domain.agent

data class AgentResult(
    val reply: String,
    val notes: List<ExtractedNote>,
    val tasks: List<ExtractedTask>
)

data class ExtractedNote(
    val title: String,
    val content: String
)

data class ExtractedTask(
    val title: String
)
