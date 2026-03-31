package com.example.model

data class Memo(val categoryId: Long, val id: Long, val contents: List<MemoContent> = emptyList())