package com.core.presentation.ui.theme.palette

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.palette.background.BackgroundPalette
import com.core.presentation.ui.theme.palette.background.darkBackgroundPalette
import com.core.presentation.ui.theme.palette.background.lightBackgroundPalette
import com.core.presentation.ui.theme.palette.border.BorderPalette
import com.core.presentation.ui.theme.palette.border.darkBorderPalette
import com.core.presentation.ui.theme.palette.border.lightBorderPalette
import com.core.presentation.ui.theme.palette.content.ContentPalette
import com.core.presentation.ui.theme.palette.content.darkContentPalette
import com.core.presentation.ui.theme.palette.content.lightContentPalette
import com.core.presentation.ui.theme.palette.semantic.SemanticBackgroundPalette
import com.core.presentation.ui.theme.palette.semantic.SemanticBorderPalette
import com.core.presentation.ui.theme.palette.semantic.SemanticContentPalette
import com.core.presentation.ui.theme.palette.semantic.darkSemanticBackgroundPalette
import com.core.presentation.ui.theme.palette.semantic.darkSemanticBorderPalette
import com.core.presentation.ui.theme.palette.semantic.darkSemanticContentPalette
import com.core.presentation.ui.theme.palette.semantic.lightSemanticBackgroundPalette
import com.core.presentation.ui.theme.palette.semantic.lightSemanticBorderPalette
import com.core.presentation.ui.theme.palette.semantic.lightSemanticContentPalette

open class ColorsPalette(
    open val backgroundPalette: BackgroundPalette,
    open val borderPalette: BorderPalette,
    open val contentPalette: ContentPalette,
    open val semanticBackgroundPalette: SemanticBackgroundPalette,
    open val semanticBorderPalette: SemanticBorderPalette,
    open val semanticContentPalette: SemanticContentPalette,
)

@Immutable
data class LightColorsPalette(
    override val backgroundPalette: BackgroundPalette = lightBackgroundPalette,
    override val borderPalette: BorderPalette = lightBorderPalette,
    override val contentPalette: ContentPalette = lightContentPalette,
    override val semanticBackgroundPalette: SemanticBackgroundPalette = lightSemanticBackgroundPalette,
    override val semanticBorderPalette: SemanticBorderPalette = lightSemanticBorderPalette,
    override val semanticContentPalette: SemanticContentPalette = lightSemanticContentPalette,
) : ColorsPalette(
    backgroundPalette,
    borderPalette,
    contentPalette,
    semanticBackgroundPalette,
    semanticBorderPalette,
    semanticContentPalette,
)

@Immutable
data class DarkColorsPalette(
    override val backgroundPalette: BackgroundPalette = darkBackgroundPalette,
    override val borderPalette: BorderPalette = darkBorderPalette,
    override val contentPalette: ContentPalette = darkContentPalette,
    override val semanticBackgroundPalette: SemanticBackgroundPalette = darkSemanticBackgroundPalette,
    override val semanticBorderPalette: SemanticBorderPalette = darkSemanticBorderPalette,
    override val semanticContentPalette: SemanticContentPalette = darkSemanticContentPalette,
) : ColorsPalette(
    backgroundPalette,
    borderPalette,
    contentPalette,
    semanticBackgroundPalette,
    semanticBorderPalette,
    semanticContentPalette,
)
