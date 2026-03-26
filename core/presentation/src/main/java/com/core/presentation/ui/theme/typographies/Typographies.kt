package com.core.presentation.ui.theme.typographies

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.body.BodyTypographies
import com.core.presentation.ui.theme.typographies.display.DisplayTypographies
import com.core.presentation.ui.theme.typographies.label.LabelTypographies
import com.core.presentation.ui.theme.typographies.title.TitleTypographies

@Immutable
class Typographies(
    spDimensions: SpDimensions,
    emDimensions: EmDimensions,
    contrastLevel: Float
) {
    val body = BodyTypographies(
        contrastLevel = contrastLevel,
        spDimensions = spDimensions,
        emDimensions = emDimensions
    )

    val display = DisplayTypographies(
        contrastLevel = contrastLevel,
        spDimensions = spDimensions,
        emDimensions = emDimensions
    )

    val label = LabelTypographies(
        contrastLevel = contrastLevel,
        spDimensions = spDimensions,
        emDimensions = emDimensions
    )

    val title = TitleTypographies(
        contrastLevel = contrastLevel,
        spDimensions = spDimensions,
        emDimensions = emDimensions
    )
}
