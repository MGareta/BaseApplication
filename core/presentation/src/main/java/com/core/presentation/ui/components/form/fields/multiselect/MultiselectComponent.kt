package com.core.presentation.ui.components.form.fields.multiselect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Composable
fun <T : Any> MultiselectComponent(
    modifier: Modifier,
    state: MultiSelectFieldState<T>,
    items: List<T>,
    isEnabled: (T) -> Boolean,
    content: @Composable (T) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.dp10,
        ),
    ) {
        items.forEach {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isEnabled(it),
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.dp2,
                ),
                colors = CardColors(
                    containerColor = if (state.getData()?.contains(it) == true) {
                        LocalPalette.current.backgroundPalette.accentLow
                    } else {
                        LocalPalette.current.backgroundPalette.base
                    },
                    contentColor = if (state.getData()?.contains(it) == true) {
                        LocalPalette.current.contentPalette.accent
                    } else {
                        LocalPalette.current.contentPalette.mid
                    },
                    disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
                    disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
                ),
                onClick = {
                    if (state.getData()?.contains(it) == true) {
                        state.unselect(selectValue = it)
                    } else {
                        state.select(selectValue = it)
                    }
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .padding(
                            horizontal = LocalDimensions.current.dp.dp16,
                            vertical = LocalDimensions.current.dp.dp12,
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    content(it)
                }
            }
        }
    }
}
