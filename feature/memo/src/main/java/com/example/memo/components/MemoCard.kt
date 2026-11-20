package com.example.memo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
internal fun MemoCard(
    memo: Memo,
    modifier: Modifier = Modifier,
    // 탭·길게 누르기를 받는 레이어에 붙일 제스처. 드래그 재정렬처럼 MemoFeed가 정한다
    gestureModifier: Modifier = Modifier,
    isMenuExpanded: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onMenuDismiss: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    // TextFieldState는 생성 후 memo.contents가 바뀌어도 텍스트를 자동으로 갱신하지 않는다.
    // MemoFeed가 같은 id의 카드를 재사용하므로, 내용이 바뀌면 상태를 다시 만들어 최신 내용을 표시한다.
    val state = remember(memo.contents) { bulletedListTextFieldState(memo.contents.toBulletedListText()) }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
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
                        .clickable(role = Role.Button, onClick = onClick)
                        .semantics {
                            onLongClick(label = "메모 메뉴 열기") {
                                onLongClick()
                                true
                            }
                        }
                        .then(gestureModifier),
                )
            }
        }

        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(0.dp),
            ) {
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onMenuDismiss,
                ) {
                    DropdownMenuItem(
                        text = { Text("메모 제거") },
                        onClick = {
                            onMenuDismiss()
                            onDeleteClick()
                        },
                    )
                }
            }
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
