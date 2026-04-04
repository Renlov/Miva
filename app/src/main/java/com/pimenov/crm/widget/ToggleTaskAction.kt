package com.pimenov.crm.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.pimenov.crm.core.database.repository.TaskRepository
import org.koin.core.context.GlobalContext

val taskIdKey = ActionParameters.Key<Long>("task_id")

class ToggleTaskAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val id = parameters[taskIdKey] ?: return
        val repository = GlobalContext.get().get<TaskRepository>()
        repository.toggleDone(id)
        TasksWidget().updateAll(context)
    }
}
