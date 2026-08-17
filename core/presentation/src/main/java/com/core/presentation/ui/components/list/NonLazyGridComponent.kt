package com.core.presentation.ui.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp

@Composable
fun NonLazyGridComponent(
    modifier: Modifier,
    columns: Int,
    itemCount: Int,
    verticalSpacing: Dp,
    horizontalSpacing: Dp,
    content: @Composable (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .semantics {
                collectionInfo = CollectionInfo(
                    rowCount = (itemCount + columns - 1) / columns,
                    columnCount = columns
                )
            },
        verticalArrangement = Arrangement.spacedBy(
            space = verticalSpacing,
        ),
    ) {
        var rows = (itemCount / columns)

        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = horizontalSpacing,
                ),
            ) {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .semantics(
                                mergeDescendants = true
                            ) {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = rowId,
                                    rowSpan = 0,
                                    columnIndex = columnId,
                                    columnSpan = 0,
                                )
                            },
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}
