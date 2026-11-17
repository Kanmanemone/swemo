package com.example.memo.components

import com.example.model.MemoContent

/**
 * [MemoContent] 목록 ↔ 불릿 리스트 텍스트.
 *
 * - 한 줄이 한 [MemoContent]다. 줄 맨 앞이 `라벨: `이면(Pill 규칙, `Pill.kt`) 라벨과 본문으로 나누고, 아니면 빈 라벨에 줄 전체가 본문이다.
 * - id는 줄 순서대로 -1, -2, ...인 임시 id다. 저장할 때 DB가 새 id를 매긴다.
 */
internal fun CharSequence.toMemoContents(): List<MemoContent> = buildList {
    val text = this@toMemoContents
    forEachParagraph(text) { start, end ->
        val id = -(size + 1L)
        val pill = text.pillRangeOf(start, end)
        add(
            if (pill == null) {
                MemoContent(id = id, label = "", text = text.substring(start, end))
            } else {
                MemoContent(
                    id = id,
                    label = text.substring(pill.labelStart, pill.labelEnd),
                    text = text.substring(pill.labelEnd + PILL_DELIMITER.length, end),
                )
            }
        )
    }
}

/** [toMemoContents]의 반대. 빈 라벨은 `라벨: ` 없이 본문만 쓴다. */
internal fun List<MemoContent>.toBulletedListText(): String =
    joinToString("\n") { if (it.label.isEmpty()) it.text else "${it.label}$PILL_DELIMITER${it.text}" }
