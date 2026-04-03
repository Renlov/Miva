package com.pimenov.crm.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.domain.model.Task
import com.pimenov.crm.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, DONE }

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter = _filter.asStateFlow()

    val tasks = combine(repository.observeAll(), _filter) { all, f ->
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
        viewModelScope.launch { repository.save(Task(title = title)) }
    }

    fun toggleDone(id: Long) {
        viewModelScope.launch { repository.toggleDone(id) }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { repository.delete(id) }
    }
}
