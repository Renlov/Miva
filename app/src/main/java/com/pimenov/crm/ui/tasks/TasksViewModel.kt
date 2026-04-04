package com.pimenov.crm.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.usecase.DeleteTaskUseCase
import com.pimenov.crm.core.database.usecase.ObserveTasksUseCase
import com.pimenov.crm.core.database.usecase.SaveTaskUseCase
import com.pimenov.crm.core.database.usecase.ToggleTaskDoneUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, DONE }

data class PendingDelete(
    val task: Task,
    val remainingSeconds: Int = UNDO_TIMEOUT_SECONDS
) {
    companion object {
        const val UNDO_TIMEOUT_SECONDS = 5
    }
}

class TasksViewModel(
    observeTasks: ObserveTasksUseCase,
    private val saveTask: SaveTaskUseCase,
    private val toggleTaskDone: ToggleTaskDoneUseCase,
    private val deleteTask: DeleteTaskUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _pendingDelete = MutableStateFlow<PendingDelete?>(null)
    val pendingDelete = _pendingDelete.asStateFlow()

    private val _snackbarDismissed = MutableSharedFlow<Unit>()
    val snackbarDismissed = _snackbarDismissed.asSharedFlow()

    private var deleteJob: Job? = null

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

    fun requestDelete(task: Task) {
        // Cancel any previous pending delete and execute it immediately
        finalizePendingDelete()

        _pendingDelete.value = PendingDelete(task = task)
        deleteJob = viewModelScope.launch {
            // Delete from DB immediately (will restore on undo)
            deleteTask.invoke(task.id)

            // Countdown
            for (i in PendingDelete.UNDO_TIMEOUT_SECONDS downTo 1) {
                _pendingDelete.value = _pendingDelete.value?.copy(remainingSeconds = i)
                delay(1_000)
            }

            // Timer expired — finalize
            _pendingDelete.value = null
            _snackbarDismissed.emit(Unit)
        }
    }

    fun undoDelete() {
        val pending = _pendingDelete.value ?: return
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null

        viewModelScope.launch {
            saveTask(pending.task)
            _snackbarDismissed.emit(Unit)
        }
    }

    fun dismissDelete() {
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null
    }

    private fun finalizePendingDelete() {
        deleteJob?.cancel()
        deleteJob = null
        _pendingDelete.value = null
    }
}
