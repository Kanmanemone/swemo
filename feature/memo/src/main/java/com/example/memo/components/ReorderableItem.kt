package com.example.memo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

/** LazyColumn 하나에서 지금 끌고 있는 항목과 그 이동량. 오프셋은 LazyListItemInfo와 같은 논리 좌표(reverseLayout이면 아래가 시작)다. */
internal class ReorderableState {
    var key by mutableStateOf<Any?>(null)
        private set
    // 화면 위치 = 시작 자리 + 손가락 총 이동량. 자리가 바뀌어도 LazyColumn이 정한 새 자리를 그릴 때 읽으므로 따로 보정하지 않는다
    private var startOffset = 0 // 드래그를 시작할 때 끌던 항목의 레이아웃 자리
    var dragDelta by mutableFloatStateOf(0f) // 손가락이 움직인 총량
        private set
    private var menuDismissed = false
    private var hasMoved = false
    private var pendingMoveIndex: Int? = null // 옮겨 보낸 도착 자리(targetItem.index). 화면에 반영될 때까지 기다리며, null이면 기다리는 이동이 없다

    fun start(key: Any, listState: LazyListState) {
        this.key = key
        startOffset = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key }?.offset ?: 0
        dragDelta = 0f
        menuDismissed = false
        hasMoved = false
        pendingMoveIndex = null
    }

    /** @return 이번 이동으로 처음 [dragDistance]를 넘었는지. true면 호출부가 메뉴를 닫는다 */
    fun drag(delta: Float, dragDistance: Float, listState: LazyListState, onMove: (from: Int, to: Int) -> Unit): Boolean {
        dragDelta += delta
        val crossed = !menuDismissed && abs(dragDelta) > dragDistance
        if (crossed) menuDismissed = true
        // 스크롤을 먼저 한다: reorder()의 requestScrollToItem보다 늦게 스크롤되면 그 요청을 덮어써 한 프레임 튄다
        scrollIfOverEdge(listState)
        reorder(listState, onMove)
        return crossed
    }

    /** 끌린 항목이 가장자리를 넘은 만큼 바로 스크롤한다. 손가락이 움직일 때만 불리므로 가만히 대고 있으면 멈춘다(공식 [LazyColumnDragAndDropDemo](https://github.com/androidx/androidx/blob/androidx-main/compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt)와 같음). */
    private fun scrollIfOverEdge(listState: LazyListState) {
        val layoutInfo = listState.layoutInfo
        val draggingItem = layoutInfo.visibleItemsInfo.firstOrNull { it.key == key } ?: return
        val start = startOffset + dragDelta
        val end = start + draggingItem.size
        // 가장자리는 contentPadding 안쪽이다. viewport는 bar 밑까지 깔려 있어서, 그 끝을 쓰면 bar에 가려진 뒤에야 스크롤된다
        val overscroll = when {
            dragDelta > 0 -> (end - (layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding)).coerceAtLeast(0f)
            dragDelta < 0 -> (start - (layoutInfo.viewportStartOffset + layoutInfo.beforeContentPadding)).coerceAtMost(0f)
            else -> 0f
        }
        // 공식 데모의 scrollBy(코루틴)와 달리 즉시 적용돼, 바로 뒤 reorder()가 스크롤된 배치를 읽는다
        if (overscroll != 0f) listState.dispatchRawDelta(overscroll)
    }

    /** 끌린 항목의 중심(손가락 위치 기준)이 이웃의 중간선을 넘으면 [onMove]로 알린다. */
    private fun reorder(listState: LazyListState, onMove: (from: Int, to: Int) -> Unit) {
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        val draggingItem = visibleItems.firstOrNull { it.key == key } ?: return

        // onMove 직후 한 프레임은 visibleItems가 예전 배치라, 그때 판정하면 같은 이동을 두 번 한다
        when (pendingMoveIndex) {
            null -> {}                                    // 기다리는 이동 없음: 그대로 판정
            draggingItem.index -> pendingMoveIndex = null // 이동이 화면에 반영됨: 그대로 판정
            else -> return                                // 아직 옛 배치: 이번엔 건너뜀
        }

        val targetItem = visibleItems.findTargetItem(draggingItem, startOffset + dragDelta + draggingItem.size / 2f) ?: return
        onMove(draggingItem.index, targetItem.index)
        hasMoved = true // 사용자가 손을 떼어 드래그가 끝날 때 DB 작업을 해야되는지 판단하는 용도
        pendingMoveIndex = targetItem.index // 'onMove() 발생' ~ '화면에 반영됨' 사이에 reorder() 함수 실행 막는 용도 (위에 있는 when문)

        // LazyColumn은 보유한 items에 변화가 생겨도, 변화 전 화면에 보이던 첫 번째 항목이 그 자리에 그대로 있게끔 스크롤을 조작하는 기본 동작이 있다. 따라서 이 기본 동작 대신 내가 커스텀한 위치로 스크롤이 이동되게 예약한다.
        if (draggingItem.index == listState.firstVisibleItemIndex || targetItem.index == listState.firstVisibleItemIndex) {
            listState.requestScrollToItem(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }

    /** 끌던 항목을 그릴 때 레이아웃 자리에서 옮길 거리. 그 프레임의 실제 자리를 읽어 빼므로 화면에서는 늘 손가락 아래에 있다 */
    fun dragOffset(listState: LazyListState): Float {
        val draggingItem = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == key } ?: return 0f
        return startOffset + dragDelta - draggingItem.offset
    }

    /** @return 순서가 바뀌어 저장할 일이 있는지 */
    fun stop(): Boolean {
        key = null
        dragDelta = 0f
        return hasMoved
    }
}

/** 끌리는 항목은 손가락 위치로 옮겨 그리고(레이아웃 자리는 그대로), 나머지 항목은 자리가 바뀔 때 애니메이션으로 옮긴다. */
internal fun Modifier.draggedItem(itemScope: LazyItemScope, dragState: ReorderableState, key: Any, listState: LazyListState): Modifier {
    val base: Modifier = if (dragState.key == key) {
        this // 끌리는 항목: 손가락을 따라가야 하니 애니메이션 없음
    } else {
        with(itemScope) {
            // 나머지: 자리 이동 애니메이션. fade는 꺼서 추가·삭제 동작을 유지한다
            this@draggedItem.animateItem(fadeInSpec = null, fadeOutSpec = null)
        }
    }
    return base
        // 이웃 카드 밑으로 가려지지 않게 위에 그린다
        .zIndex(if (dragState.key == key) 1f else 0f)
        .graphicsLayer {
            val direction = if (listState.layoutInfo.reverseLayout) -1f else 1f
            translationY = if (dragState.key == key) dragState.dragOffset(listState) * direction else 0f
        }
}

/**
 * 탭하면 [onClick]을 부르고, 길게 누르면 [onLongPress](메뉴 열기)와 함께 드래그가 시작돼 순서가 바뀐다.
 * 드래그하는 동안에도 처음 눌렀을 때의 ripple이 유지된다.
 * [MenuDismissDistance]보다 멀리 끌면 [onDragAway]로 메뉴를 닫고, 순서가 바뀐 채 손을 떼면 [onDragEnd]를 부른다.
 */
@Composable
internal fun Modifier.reorderableClickable(
    key: Any,
    dragState: ReorderableState,
    listState: LazyListState,
    onMove: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    onLongPress: () -> Unit,
    onDragAway: () -> Unit,
    onClick: () -> Unit,
): Modifier {
    // pointerInput(key)은 재시작하지 않으므로 처음 받은 람다가 옛 목록을 쥐고 있게 된다. 람다를 key로 넣으면 드래그가 끊긴다
    val currentOnMove by rememberUpdatedState(onMove)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    val currentOnDragAway by rememberUpdatedState(onDragAway)

    val interactionSource = remember { DragPressInteractionSource() }

    return this
        .clickable(
            onClick = onClick,
            interactionSource = interactionSource,
            indication = ripple(),
            role = Role.Button,
        )
        .pointerInput(key) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    interactionSource.hold()
                    dragState.start(key, listState)
                    currentOnLongPress()
                },
                onDrag = { change, amount ->
                    // 목록 스크롤이 같은 움직임을 가져가지 못하게 한다
                    change.consume()
                    val direction = if (listState.layoutInfo.reverseLayout) -1f else 1f
                    val crossedDismissDistance = dragState.drag(
                        delta = amount.y * direction,
                        dragDistance = MenuDismissDistance.toPx(),
                        listState = listState,
                        onMove = currentOnMove,
                    )
                    if (crossedDismissDistance) currentOnDragAway()
                },
                onDragEnd = {
                    interactionSource.release()
                    if (dragState.stop()) currentOnDragEnd()
                },
                onDragCancel = {
                    interactionSource.release()
                    if (dragState.stop()) currentOnDragEnd()
                },
            )
        }
}

/**
 * 드래그하는 동안 처음 눌림의 ripple을 유지하는 [MutableInteractionSource].
 *
 * 드래그가 터치를 consume하면 clickable이 눌림을 Cancel해 ripple이 꺼진다.
 * [hold]를 부른 뒤부터 [release]를 부르기 전까지 오는 Cancel은 넘기지 않고 보관했다가, [release]에서 Release로 바꿔 넘긴다.
 */
internal class DragPressInteractionSource(
    private val delegate: MutableInteractionSource = MutableInteractionSource(),
) : MutableInteractionSource {

    override val interactions: Flow<Interaction>
        get() = delegate.interactions

    private var isHolding = false
    private var pressInteraction: PressInteraction.Press? = null

    fun hold() {
        isHolding = true
    }

    fun release() {
        isHolding = false
        pressInteraction?.let { delegate.tryEmit(PressInteraction.Release(it)) }
        pressInteraction = null
    }

    override suspend fun emit(interaction: Interaction) {
        if (!tryHoldCancel(interaction)) delegate.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean =
        tryHoldCancel(interaction) || delegate.tryEmit(interaction)

    private fun tryHoldCancel(interaction: Interaction): Boolean {
        if (!isHolding || interaction !is PressInteraction.Cancel) return false
        pressInteraction = interaction.press
        return true
    }
}

// 손가락 떨림 정도로는 메뉴가 닫히지 않게 두는 여유
private val MenuDismissDistance = 24.dp

/** 끌린 항목 중심이 넘어선 가장 먼 항목. 중간선 기준이라 크기가 다른 항목과 바꾼 직후 흔들림에 되돌아가지 않는다. */
internal fun List<LazyListItemInfo>.findTargetItem(
    draggingItem: LazyListItemInfo,
    center: Float
): LazyListItemInfo? =
    lastOrNull { it.index > draggingItem.index && center > it.offset + it.size / 2f }
        ?: firstOrNull { it.index < draggingItem.index && center < it.offset + it.size / 2f }

/** 예: [a, b, c, d].moved(0, 2) = [b, c, a, d]. 새 리스트를 돌려줘야 mutableStateOf에 대입했을 때 Compose가 알아챈다. */
internal fun <T> List<T>.moved(from: Int, to: Int): List<T> =
    toMutableList().apply { add(to, removeAt(from)) }
