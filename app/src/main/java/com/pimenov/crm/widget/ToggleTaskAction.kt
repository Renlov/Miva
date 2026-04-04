package com.pimenov.crm.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.pimenov.crm.core.database.repository.TaskRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

val taskIdKey = ActionParameters.Key<Long>("task_id")

class ToggleTaskAction : ActionCallback, KoinComponent {

    private val taskRepository: TaskRepository by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val id = parameters[taskIdKey] ?: return
        taskRepository.toggleDone(id)
        TasksWidget().updateAll(context)
    }
}
