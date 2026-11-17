package com.example.memo.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MemoCard(
    memo: Memo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    // 메모 내용이 바뀌면 상태를 새로 만든다 (MemoFeed는 같은 id의 카드를 재사용한다)
    val state = remember(memo.contents) { bulletedListTextFieldState(memo.contents.toBulletedListText()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            BulletedListTextField(
                state = state,
                enabled = false, // 탭·길게 누르기를 카드가 받게 한다. 글자색은 textStyle로 직접 주므로 흐려지지 않는다
                textStyle = bulletedListTextStyle(),
                pillStyle = PillStyle(color = MaterialTheme.colorScheme.secondaryContainer),
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoCardPreview() {
    SwemoTheme {
        MemoCard(
            memo = Memo(
                categoryId = 0L,
                id = 0L,
                contents = listOf(
                    MemoContent(id = 1L, label = "label 1", text = "Fake memo 7"),
                    MemoContent(id = 2L, label = "label 2", text = "Fake memo 7"),
                    MemoContent(id = 3L, label = "label 3", text = "Fake memo 7"),
                )
            )
        )
    }
}