package com.eliseo.kotlinexercise.presentation.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eliseo.kotlinexercise.designsystem.components.AppCard
import com.eliseo.kotlinexercise.designsystem.components.AppPrimaryButton
import com.eliseo.kotlinexercise.designsystem.components.AppTaskRow
import com.eliseo.kotlinexercise.designsystem.components.AppTextField
import com.eliseo.kotlinexercise.designsystem.theme.AppSpacing

@Composable
fun TodoScreen(
    uiState: TodoUiState,
    onNewTaskTitleChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onToggleTask: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Text(
            text = "Mis tareas",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        AppTextField(
            value = uiState.newTaskTitle,
            onValueChange = onNewTaskTitleChange,
            label = "Nueva tarea",
        )
        AppPrimaryButton(
            text = "Agregar tarea",
            onClick = onAddTask,
            enabled = uiState.newTaskTitle.isNotBlank(),
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            else -> {
                AppCard(
                    title = "${uiState.pendingCount} pendientes · ${uiState.completedCount} completadas",
                    subtitle =
                        if (uiState.tasks.isEmpty()) {
                            "Agrega tu primera tarea arriba"
                        } else {
                            "Toca el checkbox para marcar como hecha"
                        },
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        AppTaskRow(
                            title = task.title,
                            isCompleted = task.isCompleted,
                            onToggle = { onToggleTask(task.id) },
                        )
                    }
                }
            }
        }
    }
}
