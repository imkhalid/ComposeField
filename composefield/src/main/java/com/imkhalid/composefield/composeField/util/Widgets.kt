package com.imkhalid.composefield.composeField.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.RichTooltipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.theme.ComposeFieldTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ShowToolTip(
    modifier: Modifier = Modifier,
    info: String = "Rich tooltips support multiple lines of informational text."
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                colors = ComposeFieldTheme.toolTipColors?: TooltipDefaults.richTooltipColors(),
                title = { Text("Info") }
            ) {
                Text(info)
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = { /* Icon button's click event */ }) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Show more information"
            )
        }
    }
}

@Composable
fun ShowToolTipA(modifier: Modifier = Modifier,info: String) {
    var show by remember { mutableStateOf(false) }
    var anchorBounds by remember { mutableStateOf<Rect?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = "Info Icon",
        modifier=Modifier.then(modifier).size(responsiveSize(20))
            .clickable{
                show= anchorBounds!=null
                // Auto-dismiss after 5 seconds
                coroutineScope.launch {
                    delay(5000)
                    show = false
                }
            }
    )

    if (show) {
        Popup (
            offset = IntOffset(anchorBounds?.left?.toInt()?:0, anchorBounds?.bottom?.toInt()?:0),
            onDismissRequest = {
                show = false
            }
        ) {
            Box(
                modifier = Modifier
                    .background(Color.DarkGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(info, color = Color.White)
            }
        }
    }
}


@Composable
fun ErrorView(modifier: Modifier = Modifier,hasError: Boolean,errorMessage: String) {
    if (hasError) {
        Text(
            text = errorMessage,
            color = ComposeFieldTheme.errorMessageColor,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp,
            modifier =modifier
        )
    }
}
