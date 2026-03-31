package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Category

class CategoryPreviewParameterProvider : PreviewParameterProvider<List<Category>> {
    override val values: Sequence<List<Category>>
        get() = sequenceOf(
            listOf(
                Category(id = 1L, name = "category 1"),
                Category(id = 2L, name = "category 2"),
                Category(id = 3L, name = "category 3"),
            )
        )
}