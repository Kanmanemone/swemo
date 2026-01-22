package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Category

class CategoryPreviewParameterProvider : PreviewParameterProvider<List<Category>> {
    override val values: Sequence<List<Category>>
        get() = sequenceOf(
            listOf(
                Category(id = "1", name = "category 1"),
                Category(id = "2", name = "category 2"),
                Category(id = "3", name = "category 3"),
            )
        )
}