package com.example.memo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.Category
import com.example.designsystem.icon.SwemoIcons

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
        // 머릿말(헤더) 역할
        TopAppBar(
            title = { Text("Category") }
        )

        categories.forEach { category ->
            TextButton(
                onClick = {
                    onCategorySelected(category.id)
                }
            ) {
                Text(text = category.name)
            }
        }

        Button(
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