package com.example.memo.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * LazyColumn 항목을 길게 누른 뒤 끌어서 순서를 바꾸는 상태.
 *
 * - 드래그 중에는 임시 순서만 바꾸고, 끝나면 바뀐 순서를 한 번만 돌려준다.
 * - 목록은 LazyColumn에 넘기는 순서 그대로 다룬다.
 */
@Stable
internal class DragReorderState<T>(
    private val listState: LazyListState,
    private val scope: CoroutineScope,
    private val keyOf: (T) -> Any,
) {
    private var source: List<T>? = null
    private var order by mutableStateOf<List<T>?>(null)
    private var draggingKey by mutableStateOf<Any?>(null)
    private var initialOffset = 0
    private var delta by mutableFloatStateOf(0f)

    // 저장한 순서가 DB에서 돌아오기 전에 원래 순서로 잠깐 되돌아가 튀지 않도록, 원본이 바뀔 때까지 임시 순서를 보여 준다
    fun shown(items: List<T>): List<T> = order?.takeIf { source === items } ?: items

    fun start(key: Any, items: List<T>) {
        val info = infoOf(key) ?: return
        source = items
        order = items
        draggingKey = key
        initialOffset = info.offset
        delta = 0f
    }

    fun drag(dy: Float) {
        val list = order ?: return
        val dragging = draggingKey?.let(::infoOf) ?: return
        // reverseLayout이면 LazyListItemInfo.offset은 아래에서 위로 잰다
        delta += if (listState.layoutInfo.reverseLayout) -dy else dy
        // 직전 이동이 아직 배치되지 않았으면 옛 위치로 계산해 되돌려 바꾸게 되므로, 다음 배치를 기다린다
        if (list.indexOfFirst { keyOf(it) == dragging.key } != dragging.index) return
        val middle = (initialOffset + delta + dragging.size / 2f).toInt()
        val target = listState.layoutInfo.visibleItemsInfo.firstOrNull {
            it.key != dragging.key && middle in it.offset until it.offset + it.size
        } ?: return

        // 첫 번째로 보이는 항목이 옮겨지면 LazyColumn이 그 항목을 따라 스크롤하므로 지금 위치에 고정한다
        val firstIndex = listState.firstVisibleItemIndex
        if (dragging.index == firstIndex || target.index == firstIndex) {
            val firstOffset = listState.firstVisibleItemScrollOffset
            scope.launch { listState.scrollToItem(firstIndex, firstOffset) }
        }
        order = list.moved(
            from = list.indexOfFirst { keyOf(it) == dragging.key },
            to = list.indexOfFirst { keyOf(it) == target.key },
        )
    }

    /** 드래그가 끝났을 때 순서가 바뀌었으면 새 순서, 아니면 null. */
    fun end(): List<T>? {
        draggingKey = null
        return order.takeIf { it != source }
    }

    fun cancel() {
        draggingKey = null
        order = null
    }

    fun isDragging(key: Any): Boolean = key == draggingKey

    // 손가락을 따라가도록, 항목의 현재 자리에서 벗어난 만큼을 화면 좌표로 돌려준다
    fun translationY(key: Any): Float {
        if (key != draggingKey) return 0f
        val info = infoOf(key) ?: return 0f
        val logical = initialOffset + delta - info.offset
        return if (listState.layoutInfo.reverseLayout) -logical else logical
    }

    private fun infoOf(key: Any): LazyListItemInfo? =
        listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }
}

@Composable
internal fun <T> rememberDragReorderState(
    listState: LazyListState,
    keyOf: (T) -> Any,
): DragReorderState<T> {
    val scope = rememberCoroutineScope()
    return remember(listState) { DragReorderState(listState, scope, keyOf) }
}

internal fun <T> List<T>.moved(from: Int, to: Int): List<T> =
    toMutableList().apply { add(to, removeAt(from)) }

/**
 * 길게 누른 뒤 움직이면 순서 바꾸기, 움직이지 않고 떼면 [onLongPress].
 *
 * 둘 중 하나를 손을 뗀 뒤에 정하므로 메뉴와 드래그가 함께 켜지지 않는다.
 * 손을 뗄 때 이벤트를 소비하므로 이 Modifier보다 앞에 둔 clickable의 탭은 실행되지 않는다.
 */
internal fun <T> Modifier.dragToReorder(
    state: DragReorderState<T>,
    key: Any,
    items: List<T>,
    onLongPress: () -> Unit,
    onReorder: (List<T>) -> Unit,
): Modifier = pointerInput(state, key, items) {
    var total = Offset.Zero
    var moved = false
    detectDragGesturesAfterLongPress(
        onDragStart = {
            total = Offset.Zero
            moved = false
            state.start(key, items)
        },
        onDrag = { _, amount ->
            total += amount
            if (total.getDistance() > viewConfiguration.touchSlop) moved = true
            state.drag(amount.y)
        },
        onDragEnd = {
            if (moved) {
                state.end()?.let(onReorder)
            } else {
                state.cancel()
                onLongPress()
            }
        },
        onDragCancel = state::cancel,
    )
}

/** LazyColumn 항목의 가장 바깥 Modifier에 붙여, 끌리는 항목을 손가락 위치에 다른 항목 위로 그린다. */
internal fun Modifier.draggedItem(state: DragReorderState<*>, key: Any): Modifier =
    zIndex(if (state.isDragging(key)) 1f else 0f)
        .graphicsLayer { translationY = state.translationY(key) }
