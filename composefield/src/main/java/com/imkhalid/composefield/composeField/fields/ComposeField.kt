package com.imkhalid.composefieldproject.composeField.fields

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.model.ComposeFieldModule

open class ComposeField {
    var focusCallback:((isValidated:Boolean,fieldName:String) -> Unit)? = null
    @Composable
    fun TrailingIcon(field: ComposeFieldModule, passwordVisible:Boolean, onClick:(()->Unit)?=null) {
        if (field.keyboardType== ComposeKeyboardType.PASSWORD) {
            Image(
                painter = if (passwordVisible)
                    painterResource(id = R.drawable.ic_open_password)
                else
                    painterResource(id = R.drawable.ic_close_password),
                contentDescription = "Toggle password visibility",
                modifier = Modifier.clickable {
                    onClick?.invoke()
                }
            )
        }
    }
}