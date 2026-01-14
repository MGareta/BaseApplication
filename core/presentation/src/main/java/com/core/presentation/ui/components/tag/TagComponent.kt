package com.core.presentation.ui.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.core.presentation.ui.components.tag.model.TagType
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun TagComponent(modifier: Modifier, type: TagType) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        color = type.containerColor(),
        contentColor = type.contentColor(),
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = LocalDimensions.current.dp.dp8,
                    vertical = LocalDimensions.current.dp.dp4,
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = LocalDimensions.current.dp.dp4,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            type.icon?.let {
                Icon(
                    modifier = Modifier
                        .size(
                            size = LocalDimensions.current.dp.dp16,
                        ),
                    painter = painterResource(
                        id = it,
                    ),
                    contentDescription = type.label,
                )
            }

            Text(
                modifier = Modifier,
                text = type.label,
                style = LocalTypographies.current.low.xs,
            )
        }
    }
}
