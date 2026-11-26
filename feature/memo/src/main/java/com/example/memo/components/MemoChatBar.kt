package com.example.memo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.memo.EditorMode
import com.example.ui.DevicePreviews

/** 메모 입력란과 그 아래 버튼 줄. 입력란은 4줄까지 늘어나고, 그 뒤로는 안에서 스크롤된다. */
@Composable
internal fun MemoChatBar(
    state: TextFieldState,
    mode: EditorMode,
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(24.dp)),
    ) {
        BulletedListTextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            textStyle = bulletedListTextStyle(),
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 4),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            IconButton(
                onClick = onClearAllClick,
                enabled = mode == EditorMode.Update || !state.isBulletedListEmpty(),
                colors = colors,
            ) {
                Icon(
                    imageVector = SwemoIcons.ClearAll,
                    contentDescription = null
                )
            }
            FilledIconButton(
                onClick = onSendClick,
                enabled = !state.isBulletedListEmpty(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // 생략하면 아이콘 색이 onPrimary가 아니라 주변 글자색(어두운 색)이 된다
                    disabledContainerColor = Color.Transparent, // 비어 있을 땐 배경 없이 회색 아이콘만
                ),
                // shortcut: M3 Wide 아이콘 버튼 크기를 직접 적음, M3 1.5+로 올리면 IconButtonDefaults.smallContainerSize(IconButtonWidthOption.Wide)로 교체
                modifier = Modifier.size(width = 52.dp, height = 40.dp),
            ) {
                Icon(
                    imageVector = when (mode) {
                        EditorMode.Insert -> SwemoIcons.Send
                        EditorMode.Update -> SwemoIcons.Edit
                    },
                    contentDescription = null
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun MemoChatBarPreview() {
    SwemoTheme {
        MemoChatBar(
            state = remember { bulletedListTextFieldState("label 1: Fake memo 7\n사과: 빨간 것\n라벨 없는 줄") },
            mode = EditorMode.Insert,
        )
    }
}
