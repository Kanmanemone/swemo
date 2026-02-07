package com.example.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.designsystem.R

object SwemoIcons {
    val Add: ImageVector
        @Composable get() = ImageVector.vectorResource(id = R.drawable.add_24dp_5f6368_fill0_wght400_grad0_opsz24)

    val AddNotes: ImageVector
        @Composable get() = ImageVector.vectorResource(id = R.drawable.add_notes_24dp_5f6368_fill0_wght400_grad0_opsz24)

    val Menu: ImageVector
        @Composable get() = ImageVector.vectorResource(id = R.drawable.menu_24dp_5f6368_fill0_wght400_grad0_opsz24)

    val Remove: ImageVector
        @Composable get() = ImageVector.vectorResource(id = R.drawable.remove_24dp_5f6368_fill0_wght400_grad0_opsz24)

    val Send: ImageVector
        @Composable get() = ImageVector.vectorResource(id = R.drawable.send_24dp_5f6368_fill0_wght400_grad0_opsz24)
}