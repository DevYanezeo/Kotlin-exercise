package com.eliseo.kotlinexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eliseo.kotlinexercise.data.repository.InMemoryTaskRepository
import com.eliseo.kotlinexercise.designsystem.theme.AppTheme
import com.eliseo.kotlinexercise.domain.usecase.AddTaskUseCase
import com.eliseo.kotlinexercise.domain.usecase.GetTasksUseCase
import com.eliseo.kotlinexercise.domain.usecase.ToggleTaskUseCase
import com.eliseo.kotlinexercise.presentation.todo.TodoScreen
import com.eliseo.kotlinexercise.presentation.todo.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = InMemoryTaskRepository()
        val viewModel =
            TodoViewModel(
                getTasksUseCase = GetTasksUseCase(repository),
                addTaskUseCase = AddTaskUseCase(repository),
                toggleTaskUseCase = ToggleTaskUseCase(repository),
            )

        setContent {
            AppTheme {
                val uiState by viewModel.uiState.collectAsState()
                TodoScreen(
                    uiState = uiState,
                    onNewTaskTitleChange = viewModel::onNewTaskTitleChange,
                    onAddTask = viewModel::addTask,
                    onToggleTask = viewModel::toggleTask,
                )
            }
        }
    }
}
