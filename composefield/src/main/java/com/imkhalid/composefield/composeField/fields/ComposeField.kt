package com.imkhalid.composefieldproject.composeField.fields


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.fields.ComposeCheckBoxField
import com.imkhalid.composefield.composeField.fields.ComposeCurrencyField
import com.imkhalid.composefield.composeField.fields.ComposeDatePickerField
import com.imkhalid.composefield.composeField.fields.ComposeDropDownField
import com.imkhalid.composefield.composeField.fields.ComposeMobileField
import com.imkhalid.composefield.composeField.fields.ComposeRadioGroupField
import com.imkhalid.composefield.composeField.fields.ComposeSwitchField
import com.imkhalid.composefield.composeField.fields.ComposeTimePickerField
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.states.rememberFieldState
import com.imkhalid.composefield.theme.ComposeFieldTheme

abstract class ComposeField {
    var currentUserCountryCode = ""
    var focusCallback: ((isValidated: Boolean, fieldName: String) -> Unit)? = null
    var selectedContactCallback:((name: String,number: String)-> Unit)?= null
    lateinit var localRequester:BringIntoViewRequester

    @Composable
    fun TrailingIconBasic(
        field: ComposeFieldState,
        passwordVisible: Boolean,
        onClick: (() -> Unit)? = null
    ) {
        if (field.field.keyboardType is ComposeKeyboardTypeAdv.PASSWORD && field.text.isNotEmpty()) {
            Icon(
                imageVector = if (passwordVisible)
                    Icons.Outlined.Visibility
                else
                    Icons.Outlined.VisibilityOff,
                tint = Color(0xff9D9D9D),
                contentDescription = "Hide/Show",
                modifier = Modifier
                    .padding(start = responsiveSize(14))
                    .size(responsiveSize(20))
                    .clickable { onClick?.invoke() }
            )
        }
    }

    fun trailingIcon(
        field: ComposeFieldModule,
        passwordVisible: Boolean,
        onClick: (() -> Unit)? = null
    ): (@Composable () -> Unit)? {
        return if (field.keyboardType is ComposeKeyboardTypeAdv.PASSWORD) {
            {
                Image(
                    painter = if (passwordVisible)
                        painterResource(id = R.drawable.ic_open_password)
                    else
                        painterResource(id = R.drawable.ic_close_password),
                    contentDescription = "Hide/Show",
                    modifier = Modifier
                        .size(responsiveSize(31))
                        .clickable { onClick?.invoke() }
                )
            }
        }else if (field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO && field.keyboardType.showPicker){
            {
                Image(
                    painter = painterResource(R.drawable.ic_lib_contact),
                    contentDescription = "",
                    modifier = Modifier.size(responsiveSize(31))
                        .clip(CircleShape)
                        .clickable{
                            onClick?.invoke()
                        }
                )
            }
        } else null
    }



    @Composable
    fun PreBuild(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        // ✅ Common logic that must run before any child implementation
        localRequester = remember { BringIntoViewRequester() }

        // Call the abstract method which will be implemented by child classes
        Build(modifier, state, newValue)
        LaunchedEffect(state.hasError) {
            if (state.hasError) {
                localRequester.bringIntoView()
            }
        }
    }

    @Composable
    abstract fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
    )
}

class ComposeFieldBuilder {
    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        userCountry:String="2",
        stateHolder: ComposeFieldStateHolder = rememberFieldState(name = "", label = ""),
        focusCallback: ((isValidated: Boolean, fieldName: String) -> Unit)? = null,
        onValueChangeForChild: ((value: String) -> Unit)? = null,
        onValueChange: ((name: String, value: String) -> Unit)? = null,
        contactCallback:((name: String,number: String)-> Unit)?= null
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge.copy(
                fontFamily = stateHolder.state.field.fieldStyle.getFontFamily() // Or your custom font
            )
        ) {

            val state = stateHolder.state
            val field =
                when (state.field.type) {
                    ComposeFieldType.TEXT_BOX,
                    ComposeFieldType.TEXT_AREA -> {
                        if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO) {
                            ComposeMobileField().apply {
                                selectedContactCallback = contactCallback
                            }
                        } else if (state.field.keyboardType is ComposeKeyboardTypeAdv.CURRENCY) {
                            ComposeCurrencyField()
                        } else {
                            ComposeTextField().setFocusCallback(focusCallback)
                        }
                    }

                    ComposeFieldType.DROP_DOWN -> ComposeDropDownField()
                    ComposeFieldType.DATE_PICKER -> ComposeDatePickerField()
                    ComposeFieldType.TIME_PICKER -> ComposeTimePickerField()
                    ComposeFieldType.DATE_TIME_PICKER -> ComposeDatePickerField()
                    ComposeFieldType.SWITCH -> ComposeSwitchField()
                    ComposeFieldType.CHECK_BOX -> ComposeCheckBoxField()
                    ComposeFieldType.RADIO_BUTTON -> ComposeRadioGroupField()
                }

            field.currentUserCountryCode = userCountry
            if (state.field.hidden == ComposeFieldYesNo.NO)
                field.PreBuild(
                    state = state,
                    newValue = { error, newVal ->
                        updateFieldState(error, newVal, stateHolder, onValueChangeForChild)
                        onValueChange?.invoke(state.field.name, newVal)
                    },
                    modifier = modifier
                )
        }
    }

    fun updateFieldState(
        error: Pair<Boolean, String>,
        text: String,
        stateHolder: ComposeFieldStateHolder,
        onValueChangeForChild: ((value: String) -> Unit)?
    ) {
        stateHolder.updatedState(error, text, onValueChangeForChild)
    }
}
