package com.eliseo.kotlinexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eliseo.kotlinexercise.data.repository.InMemoryAnxietyRepository
import com.eliseo.kotlinexercise.designsystem.theme.AppTheme
import com.eliseo.kotlinexercise.domain.usecase.GetAnxietyClickCountUseCase
import com.eliseo.kotlinexercise.domain.usecase.RegisterAnxietyClickUseCase
import com.eliseo.kotlinexercise.presentation.anxiety.AnxietyScreen
import com.eliseo.kotlinexercise.presentation.anxiety.AnxietyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = InMemoryAnxietyRepository()
        val viewModel =
            AnxietyViewModel(
                getAnxietyClickCountUseCase = GetAnxietyClickCountUseCase(repository),
                registerAnxietyClickUseCase = RegisterAnxietyClickUseCase(repository),
            )

        setContent {
            AppTheme {
                val uiState by viewModel.uiState.collectAsState()
                AnxietyScreen(
                    uiState = uiState,
                    onAnxietyClick = viewModel::onAnxietyClick,
                )
            }
        }
    }
}
