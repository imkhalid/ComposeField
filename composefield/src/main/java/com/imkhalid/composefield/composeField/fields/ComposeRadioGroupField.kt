package com.imkhalid.composefield.composeField.fields

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

class ComposeRadioGroupField : ComposeField() {

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        MyBuild(state = state, newValue = newValue, modifier = modifier)
    }

    @Composable
    private fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val twoOption =
            state.field.defaultValues.size == 2 &&
                state.field.defaultValues.all { x -> x.text.length <= 10 }
        RadioGroupField(modifier = modifier) {
            val label = buildAnnotatedString {
                withStyle(
                    style =
                        SpanStyle(
                            fontSize = responsiveTextSize(size = 13).sp,
                            color = ComposeFieldTheme.focusedBorderColor,
                        )
                ) {
                    append(state.field.label)
                }
                if (state.field.required == ComposeFieldYesNo.YES) {
                    withStyle(
                        style =
                            SpanStyle(
                                fontSize = responsiveTextSize(size = 13).sp,
                                color = Color.Red
                            )
                    ) {
                        append("*")
                    }
                }
            }
            Text(text = label, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp))
            Spacer(modifier = Modifier.padding(top = 5.dp))
            if (twoOption) {
                Row(modifier = Modifier.padding(start = 20.dp)) {
                    state.field.defaultValues.forEach {
                        RoundedCornerRadiobox(
                            label = it.text,
                            checkedColor = ComposeFieldTheme.focusedBorderColor,
                            isChecked = state.text == it.id,
                            onValueChange = { b -> newValue(Pair(true, ""), it.id) }
                        )
                    }
                }
            } else {
                state.field.defaultValues.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable { newValue(Pair(true, ""), it.id) }
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(8.dp),
                            selected = state.text == it.id,
                            onClick = null
                        )
                        Text(
                            text = it.text,
                            fontSize = responsiveTextSize(size = 15).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioGroupField(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    when (ComposeFieldTheme.fieldStyle) {
        ComposeFieldTheme.FieldStyle.OUTLINE ->
            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                content = { content.invoke() }
            )
        ComposeFieldTheme.FieldStyle.CONTAINER,
        ComposeFieldTheme.FieldStyle.NORMAL ->
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier =
                    modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .border(
                            width = 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(5.dp),
                content = { content.invoke() }
            )
    }
}

@Composable
fun RoundedCornerRadiobox(
    label: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    size: Float = 20f,
    checkedColor: Color = Color.Blue,
    uncheckedColor: Color = Color.White,
    onValueChange: ((Boolean) -> Unit)? = null
) {
    val checkboxColor: Color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor)
    val density = LocalDensity.current
    val duration = 200

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .heightIn(40.dp) // height of 48dp to comply with minimum touch target size
                .toggleable(
                    value = isChecked,
                    role = Role.Checkbox,
                    onValueChange = { onValueChange?.invoke(it) }
                )
    ) {
        Box(
            modifier =
                Modifier.size(size.dp)
                    .background(color = checkboxColor, shape = RoundedCornerShape(4.dp))
                    .border(width = 1.5.dp, color = checkedColor, shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isChecked,
                enter =
                    slideInHorizontally(animationSpec = tween(duration)) {
                        with(density) { (size * -0.5).dp.roundToPx() }
                    } +
                        expandHorizontally(
                            expandFrom = Alignment.Start,
                            animationSpec = tween(duration)
                        ),
                exit = fadeOut()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = uncheckedColor)
            }
        }
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = label,
            fontSize = responsiveTextSize(size = 15).sp
        )
    }
}
