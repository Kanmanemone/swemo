package com.example.memo.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
    onDeleteClick: () -> Unit = {},
) {
    // TextFieldState는 생성 후 memo.contents가 바뀌어도 텍스트를 자동으로 갱신하지 않는다.
    // MemoFeed가 같은 id의 카드를 재사용하므로, 내용이 바뀌면 상태를 다시 만들어 최신 내용을 표시한다.
    val state = remember(memo.contents) { bulletedListTextFieldState(memo.contents.toBulletedListText()) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Box(modifier = modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BulletedListTextField(
                        state = state,
                        enabled = false, // 편집 막기. 글자색은 textStyle로 직접 주므로 흐려지지 않는다
                        textStyle = bulletedListTextStyle(),
                    )
                }
                // disabled여도 TextField가 터치를 소비하므로, 그 위에 덮은 레이어가 탭·길게 누르기를 먼저 받는다
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .combinedClickable(
                            onClick = onClick,
                            onLongClickLabel = "메모 메뉴 열기",
                            onLongClick = { isMenuExpanded = true },
                            role = Role.Button,
                        ),
                )
            }
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("메모 제거") },
                onClick = {
                    isMenuExpanded = false
                    onDeleteClick()
                },
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
