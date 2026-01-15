package com.example.model

data class Memo(val categoryId: String, val id: String, val contents: List<MemoContent> = emptyList())