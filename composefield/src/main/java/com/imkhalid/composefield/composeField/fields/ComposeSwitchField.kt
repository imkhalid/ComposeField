package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

class ComposeSwitchField {

    @Composable
    fun Build(state: ComposeFieldState, newValue: (Pair<Boolean,String>, String) -> Unit) {
        val trueValue = state.field.defaultValues.find { x->x.text.contains("yes",true) ||
                x.text.contains("true",true) ||
                x.text.contains("male",true)
        }?.id
        val falseValue = state.field.defaultValues.find { x->x.text.contains("no",true) ||
                x.text.contains("false",true) ||
                x.text.contains("female",true)
        }?.id

        val label = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = responsiveTextSize(size = 13).sp,
                    color = ComposeFieldTheme.hintColor,
                )
            ) {
                append(state.field.label)
            }
            if (state.field.required== ComposeFieldYesNo.YES){
                withStyle(
                    style = SpanStyle(
                        fontSize = responsiveTextSize(size = 13).sp,
                        color = Color.Red
                    )
                ) {
                    append("*")
                }
            }
        }

        Row(
            modifier= Modifier
                .defaultMinSize(
                    minWidth = OutlinedTextFieldDefaults.MinWidth,
                    minHeight = OutlinedTextFieldDefaults.MinHeight
                )
                .clickable {
                    if (state.text == falseValue) {
                        newValue.invoke(Pair(true, ""), trueValue ?: "")
                    } else {
                        newValue.invoke(Pair(true, ""), falseValue ?: "")
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(start = 10.dp))
            Switch(
                checked =state.text==trueValue ,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedBorderColor = ComposeFieldTheme.focusedBorderColor,
                    checkedIconColor =  ComposeFieldTheme.focusedBorderColor,
                    uncheckedBorderColor = ComposeFieldTheme.unfocusedBorderColor,
                    uncheckedIconColor = ComposeFieldTheme.unfocusedBorderColor,
                    checkedTrackColor = ComposeFieldTheme.focusedBorderColor.copy(alpha = 0.4f),
                    uncheckedTrackColor = ComposeFieldTheme.unfocusedBorderColor.copy(alpha = 0.4f),
                    checkedThumbColor = ComposeFieldTheme.focusedBorderColor,
                    uncheckedThumbColor = ComposeFieldTheme.unfocusedBorderColor,
                )
            )
        }
    }


}