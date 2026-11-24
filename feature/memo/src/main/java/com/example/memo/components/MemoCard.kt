package com.example.memo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

/**
 * 메모 내용과 메뉴만 그린다. 탭·길게 누르기는 받지 않는다.
 * 내용을 그리는 TextField가 disabled여도 터치를 소비하므로, 터치는 호출부가 이 카드 위에 덮은 레이어로 받는다.
 */
/** 카드 모양. 카드 위에 덮는 터치 레이어도 같은 모양으로 잘라야 하므로 한 곳에서 정한다 */
internal val MemoCardShape: Shape
    @Composable get() = CardDefaults.shape

@Composable
internal fun MemoCard(
    memo: Memo,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean = false,
    onMenuExpandedChange: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    // TextFieldState는 생성 후 memo.contents가 바뀌어도 텍스트를 자동으로 갱신하지 않는다.
    // MemoFeed가 같은 id의 카드를 재사용하므로, 내용이 바뀌면 상태를 다시 만들어 최신 내용을 표시한다.
    val state = remember(memo.contents) { bulletedListTextFieldState(memo.contents.toBulletedListText()) }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MemoCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            BulletedListTextField(
                state = state,
                modifier = Modifier.padding(16.dp),
                enabled = false, // 편집 막기. 글자색은 textStyle로 직접 주므로 흐려지지 않는다
                textStyle = bulletedListTextStyle(),
            )
        }

        // 메뉴 앵커: 카드 오른쪽 끝에 붙은 폭 0, 카드 높이의 영역. 메뉴가 카드 오른쪽 아래에 뜬다
        Box(
            modifier = Modifier
                .matchParentSize()
                .wrapContentWidth(Alignment.End)
                .width(0.dp),
        ) {
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { onMenuExpandedChange(false) },
            ) {
                DropdownMenuItem(
                    text = { Text("메모 제거") },
                    onClick = {
                        onMenuExpandedChange(false)
                        onDeleteClick()
                    },
                )
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
