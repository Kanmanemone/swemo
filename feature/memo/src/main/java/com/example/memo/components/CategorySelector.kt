package com.example.memo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Category
import com.example.ui.DevicePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categories: List<Category>,
    onCategorySelected: (Long) -> Unit,
    onAddCategoryButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            title = { Text("Category") }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(
                items = categories,
                key = { category -> category.id }
            ) { category ->
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onCategorySelected(category.id)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = category.name)
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            onClick = {
                onAddCategoryButtonClick()
            }
        ) {
            Icon(
                imageVector = SwemoIcons.Add,
                contentDescription = null
            )
        }
    }
}

@DevicePreviews
@Composable
fun CategorySelectorPreview() {
    SwemoTheme {
        CategorySelector(
            categories = listOf(
                Category(id = 1L, name = "전체"),
                Category(id = 2L, name = "업무"),
                Category(id = 3L, name = "개인"),
            ),
            onCategorySelected = {},
            onAddCategoryButtonClick = {},
        )
    }
}