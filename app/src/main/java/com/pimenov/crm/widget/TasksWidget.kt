package com.pimenov.crm.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import com.pimenov.crm.core.database.model.Task
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext

class TasksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val koin = GlobalContext.get()
        val repository = koin.get<com.pimenov.crm.core.database.repository.TaskRepository>()
        val tasks = repository.observeAll().first()

        provideContent {
            GlanceTheme {
                TasksWidgetContent(tasks = tasks)
            }
        }
    }
}

private val bgColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color.White,
    night = androidx.compose.ui.graphics.Color(0xFF1C1B1F)
)
private val titleColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color.Black,
    night = androidx.compose.ui.graphics.Color.White
)
private val mutedColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color.Gray,
    night = androidx.compose.ui.graphics.Color.Gray
)
private val doneCheckColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    night = androidx.compose.ui.graphics.Color(0xFF81C784)
)
private val accentColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color(0xFF6750A4),
    night = androidx.compose.ui.graphics.Color(0xFFD0BCFF)
)
private val onAccentColor = ColorProvider(
    day = androidx.compose.ui.graphics.Color.White,
    night = androidx.compose.ui.graphics.Color(0xFF381E72)
)

@Composable
private fun TasksWidgetContent(tasks: List<Task>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        Text(
            text = "MIVA",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет задач",
                    style = TextStyle(fontSize = 14.sp, color = mutedColor)
                )
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxWidth()
            ) {
                items(tasks, itemId = { it.id }) { task ->
                    TaskItem(task = task)
                }
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(40.dp)
                .cornerRadius(20.dp)
                .background(accentColor)
                .clickable(actionRunCallback<OpenTasksAction>()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Открыть задачи",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = onAccentColor,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun TaskItem(task: Task) {
    val textColor = if (task.isDone) mutedColor else titleColor
    val checkColor = if (task.isDone) doneCheckColor else mutedColor

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(
                actionRunCallback<ToggleTaskAction>(
                    actionParametersOf(taskIdKey to task.id)
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (task.isDone) "✅" else "⬜",
            style = TextStyle(fontSize = 18.sp, color = checkColor)
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        Text(
            text = task.title,
            style = TextStyle(
                fontSize = 14.sp,
                color = textColor,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            ),
            maxLines = 2
        )
    }
}
