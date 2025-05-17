package com.example.watchappbimo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import com.example.watchappbimo.presentation.theme.WatchappbimoTheme
import androidx.wear.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Text

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WatchappbimoTheme {
                WearApp()
            }
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberNavController()
    val viewModel: MessageViewModel = viewModel()

    // Yeni mesaj geldiğinde yönlendirme
    LaunchedEffect(Unit) {
        viewModel.onNewMessage = {
            navController.navigate("messageScreen")
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen()
        }
        composable("messageScreen") {
            MessageScreen(viewModel)
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        timeText = { TimeText() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Waiting for message...",
                style = MaterialTheme.typography.body1,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MessageScreen(viewModel: MessageViewModel = viewModel()) {
    val messages = viewModel.messages
    var currentIndex by remember { mutableStateOf(messages.lastIndex.coerceAtLeast(0)) }
    val scrollState = rememberScrollState()

    LaunchedEffect(currentIndex) {
        scrollState.scrollTo(0)
    }

    Scaffold(
        timeText = { TimeText() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = Color.White,
                    modifier = Modifier
                        .clickable { if (currentIndex > 0) currentIndex-- }
                        .padding(12.dp),
                    style = MaterialTheme.typography.button
                )

                Text(
                    text = "→",
                    color = Color.White,
                    modifier = Modifier
                        .clickable { if (currentIndex < messages.lastIndex) currentIndex++ }
                        .padding(12.dp),
                    style = MaterialTheme.typography.button
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = messages.getOrNull(currentIndex) ?: "No messages",
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
