package com.imkhalid.composefield.composeField.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.theme.ComposeFieldTheme

data class ComposeFieldStyle(
    val textSizes:ComposeFieldTextStyle,
    val colors:ComposeFieldColors,
    val fieldStyle: ComposeFieldTheme.FieldStyle,
    val fontFamily: FontFamily,
){
    @Composable fun getTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.textColor,
            fontSize = responsiveTextSize(
                textSizes.textSize
            ).sp,
            fontWeight = textSizes.fontWeight,
            fontFamily = fontFamily,
        )
    }

    @Composable fun getHelderTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.infoColor,
            fontSize = responsiveTextSize(
                textSizes.infoSize
            ).sp,
            fontWeight = textSizes.infoFontWeight,
            fontFamily = fontFamily,
        )
    }

    @Composable fun getLabelTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.labelColor,
            fontSize = responsiveTextSize(
                textSizes.labelSize
            ).sp,
            fontWeight = textSizes.labelFontWeight,
            fontFamily = fontFamily,
        )
    }

    @Composable fun getErrorTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.errorColor,
            fontSize = responsiveTextSize(
                textSizes.errorSize
            ).sp,
            fontWeight = textSizes.errorFontWeight,
            fontFamily = fontFamily,
        )
    }

    @Composable fun getHintTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.hintColor,
            fontSize = responsiveTextSize(
                textSizes.hintSize
            ).sp,
            fontWeight = textSizes.hintFontWeight,
            fontFamily = fontFamily,
        )
    }

    companion object{
        fun defaultComposeFieldStyle():ComposeFieldStyle{
            return ComposeFieldStyle(
                textSizes = getComposeFieldTextStyle(),
                colors = getComposeFieldColors(),
                fieldStyle = ComposeFieldTheme.FieldStyle.STICK_LABEL,
                fontFamily = FontFamily.Default,
            )
        }
        fun getComposeFieldColors(): ComposeFieldColors {
            return ComposeFieldColors(
                textColor = Color(0xff000000),
                labelColor = Color(0xff000000),
                hintColor = Color(0xFFD5D3D3),
                infoColor = Color(0xff000000),
                errorColor = Color(0xFFC20000),
                focusedLabelColor = Color(0xff000000),
                unfocusedLabelColor = Color(0xff000000),
                focusedBorderColor = Color(0xff000000),
                unfocusedBorderColor = Color(0xFFD5D3D3),
                errorBorderColor = Color(0xFFC20000),
                containerColor = Color(0xffffffff)
            )
        }

        fun getComposeFieldTextStyle(): ComposeFieldTextStyle {
            return ComposeFieldTextStyle(
                textSize = 16,
                labelSize = 16,
                hintSize = 14,
                errorSize = 12,
                infoSize = 12,
                fontWeight = FontWeight.Medium,
                labelFontWeight = FontWeight.Medium,
                hintFontWeight = FontWeight.Normal,
                errorFontWeight = FontWeight.Normal,
                infoFontWeight = FontWeight.Normal,
            )
        }
    }

}

data class ComposeFieldTextStyle(
    val textSize: Int,
    val labelSize: Int,
    val hintSize:Int,
    val errorSize: Int,
    val infoSize: Int,
    val fontWeight: FontWeight,
    val labelFontWeight: FontWeight,
    val hintFontWeight: FontWeight,
    val errorFontWeight: FontWeight,
    val infoFontWeight: FontWeight,
)

data class ComposeFieldColors(
    val textColor : Color,
    val labelColor: Color,
    val hintColor: Color,
    val infoColor: Color,
    val errorColor: Color,
    val focusedLabelColor: Color,
    val unfocusedLabelColor: Color,
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val errorBorderColor: Color,
    val containerColor: Color,
)
