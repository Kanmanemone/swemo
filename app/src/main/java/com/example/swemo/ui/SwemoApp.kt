package com.example.swemo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.FakeMemoRepository
import com.example.data.MemoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwemoApp() {
    TestScreen()
}

@Composable
fun TestScreen() {
    val repository: MemoRepository = remember { FakeMemoRepository() }
    val memos by repository.getMemos().collectAsState(initial = emptyList())
    val memosText = memos.joinToString("\n") { it.content }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = {
                    repository.insertMemo("Inserted memo")
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(memosText)
    }
}