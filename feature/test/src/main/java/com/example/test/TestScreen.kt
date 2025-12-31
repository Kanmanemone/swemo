package com.example.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.data.FakeMemoRepository
import com.example.data.MemoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .background(color = Color.LightGray)
                    .fillMaxHeight()
                    .width(250.dp)
            )
        }
    ) {
        val repository: MemoRepository = remember { FakeMemoRepository() }
        val memos by repository.getMemos().collectAsState(initial = emptyList())
        val memosText = memos.joinToString("\n") { it.content }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text("Test Screen")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch { drawerState.open() }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.menu_24dp_5f6368_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            repository.insertMemo("Inserted memo")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_24dp_5f6368_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                }
            )
            Text(
                text = memosText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}