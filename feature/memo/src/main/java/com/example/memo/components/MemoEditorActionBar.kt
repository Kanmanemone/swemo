package com.example.memo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.DevicePreviews
import com.example.designsystem.icon.SwemoIcons

@Composable
fun MemoEditorActionBar(
    onAddContentClick: () -> Unit = {},
    onAddMemoClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onAddContentClick,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                imageVector = SwemoIcons.Add,
                contentDescription = null
            )
        }
        Button(
            onClick = onAddMemoClick,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                imageVector = SwemoIcons.Send,
                contentDescription = null
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoEditorActionBarPreview() {
    MemoEditorActionBar()
}