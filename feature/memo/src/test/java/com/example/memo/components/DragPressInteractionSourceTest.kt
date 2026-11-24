package com.example.memo.components

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [DragPressInteractionSource]가 드래그 동안 clickable의 눌림 취소를 붙잡아, 처음 ripple을 유지하는지 검사한다.
 * ripple이 실제로 받는 신호([DragPressInteractionSource.interactions])만 본다.
 */
class DragPressInteractionSourceTest {

    /** hold 중 clickable이 보내는 Cancel(emit 경로)은 ripple에 가지 않고, release 때 같은 눌림의 Release로 간다. */
    @Test
    fun hold_cancelIsHeld_thenReleasedOnRelease() = runBlocking {
        val interactionSource = DragPressInteractionSource()
        val interactions = mutableListOf<Interaction>()
        val job = launch(Dispatchers.Unconfined) { interactionSource.interactions.collect { interactions += it } }
        val press = PressInteraction.Press(Offset.Zero)

        interactionSource.emit(press)
        interactionSource.hold()
        interactionSource.emit(PressInteraction.Cancel(press)) // clickable은 Cancel을 emit으로 보낸다
        assertEquals(listOf<Interaction>(press), interactions)

        interactionSource.release()
        assertEquals(2, interactions.size)
        assertSame(press, (interactions[1] as PressInteraction.Release).press)
        job.cancel()
    }

    /** hold 중이 아니면 Cancel을 그대로 보낸다 (평소 탭 동작은 바뀌지 않는다). tryEmit 경로도 함께 확인한다. */
    @Test
    fun notHolding_cancelPassesThrough() = runBlocking {
        val interactionSource = DragPressInteractionSource()
        val interactions = mutableListOf<Interaction>()
        val job = launch(Dispatchers.Unconfined) { interactionSource.interactions.collect { interactions += it } }
        val press = PressInteraction.Press(Offset.Zero)

        interactionSource.tryEmit(press)
        interactionSource.tryEmit(PressInteraction.Cancel(press))
        assertEquals(2, interactions.size)
        assertSame(press, (interactions[1] as PressInteraction.Cancel).press)
        job.cancel()
    }

    /** 붙잡은 Cancel이 없으면 release는 아무것도 보내지 않는다 (같은 눌림에 Release가 두 번 가지 않는다). */
    @Test
    fun releaseWithoutHeldCancel_emitsNothing() = runBlocking {
        val interactionSource = DragPressInteractionSource()
        val interactions = mutableListOf<Interaction>()
        val job = launch(Dispatchers.Unconfined) { interactionSource.interactions.collect { interactions += it } }

        interactionSource.hold()
        interactionSource.release()
        assertTrue(interactions.isEmpty())
        job.cancel()
    }
}
