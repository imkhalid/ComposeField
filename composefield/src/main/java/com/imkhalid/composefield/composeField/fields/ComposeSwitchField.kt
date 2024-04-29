package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imkhalid.composefield.composeField.ComposeFieldState

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
        Row(
            modifier=Modifier
                .defaultMinSize(minWidth = OutlinedTextFieldDefaults.MinWidth, minHeight = OutlinedTextFieldDefaults.MinHeight)
                .clickable {
                    if (state.text == falseValue){
                        newValue.invoke(Pair(true,""),trueValue?:"")
                    }else{
                        newValue.invoke(Pair(true,""),falseValue?:"")
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.field.label,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(start = 10.dp))
            Switch(
                checked =state.text==trueValue ,
                onCheckedChange = null)
        }
    }


}