package com.example.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.SwemoTheme

@Composable
fun LabelChipGroup(
    labels: Set<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            labels.forEach { label ->
                Button(
                    onClick = {

                    },
                    shape = RoundedCornerShape(50), // 높이에 비해 큰 값 → 알약 모양
                ) {
                    Text(label)
                }
            }
        }
    )
}

@Preview
@Composable
fun LabelChipGroupPreview() {
    SwemoTheme {
        LabelChipGroup(
            labels = setOf("label 1", "label 2", "label 3", "label 4", "label 5", "label 6", "label 7", "label 8", "label 9")
        )
    }
}