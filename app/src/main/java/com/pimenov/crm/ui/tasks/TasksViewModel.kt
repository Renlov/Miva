package com.pimenov.crm.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.usecase.DeleteTaskUseCase
import com.pimenov.crm.core.database.usecase.ObserveTasksUseCase
import com.pimenov.crm.core.database.usecase.SaveTaskUseCase
import com.pimenov.crm.core.database.usecase.ToggleTaskDoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, DONE }

class TasksViewModel(
    observeTasks: ObserveTasksUseCase,
    private val saveTask: SaveTaskUseCase,
    private val toggleTaskDone: ToggleTaskDoneUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter = _filter.asStateFlow()

    val tasks = combine(observeTasks(), _filter) { all, f ->
        when (f) {
            TaskFilter.ALL -> all
            TaskFilter.ACTIVE -> all.filter { !it.isDone }
            TaskFilter.DONE -> all.filter { it.isDone }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFilter(f: TaskFilter) {
        _filter.value = f
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { saveTask(Task(title = title)) }
    }

    fun toggleDone(id: Long) {
        viewModelScope.launch { toggleTaskDone(id) }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { deleteTask.invoke(id) }
    }
}
