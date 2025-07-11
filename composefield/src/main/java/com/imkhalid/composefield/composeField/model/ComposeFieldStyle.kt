package com.imkhalid.composefield.composeField.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
    val tooltipPersisted: Boolean= false,
){
    fun getFontFamily(): FontFamily{
        return ComposeFontRegistry.resolve("customFont")
    }
    @Composable fun getTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.textColor,
            fontSize = responsiveTextSize(
                textSizes.textSize
            ).sp,
            fontWeight = textSizes.fontWeight,
            fontFamily = getFontFamily(),
        )
    }

    @Composable fun getHelderTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.infoColor,
            fontSize = responsiveTextSize(
                textSizes.infoSize
            ).sp,
            fontWeight = textSizes.infoFontWeight,
            fontFamily = getFontFamily(),
        )
    }

    @Composable fun getLabelTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.labelColor,
            fontSize = responsiveTextSize(
                textSizes.labelSize
            ).sp,
            fontWeight = textSizes.labelFontWeight,
            fontFamily = getFontFamily(),
        )
    }

    @Composable fun getDropDownTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            fontSize = responsiveTextSize(
                textSizes.labelSize
            ).sp,
            fontWeight = FontWeight.Normal,
            fontFamily = getFontFamily(),
        )
    }

    @Composable fun getErrorTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.errorColor,
            fontSize = responsiveTextSize(
                textSizes.errorSize
            ).sp,
            fontWeight = textSizes.errorFontWeight,
            fontFamily = getFontFamily(),
        )
    }

    @Composable fun getHintTextStyle(): TextStyle{
        return TextStyle.Default.copy(
            color = colors.hintColor,
            fontSize = responsiveTextSize(
                textSizes.hintSize
            ).sp,
            fontWeight = textSizes.hintFontWeight,
            fontFamily = getFontFamily(),
        )
    }

    fun toMap(): Map<String, Any> = mapOf(
        "textSizes" to textSizes.toMap(),
        "colors" to colors.toMap(),
        "fieldStyle" to fieldStyle.name,
        "toolTipPersisted" to tooltipPersisted.toString().lowercase()
    )

    companion object{


        fun defaultComposeFieldStyle():ComposeFieldStyle{
            return ComposeFieldStyle(
                textSizes = defaultComposeFieldTextStyle(),
                colors = defaultComposeFieldColors(),
                fieldStyle = ComposeFieldTheme.FieldStyle.STICK_LABEL,
            )
        }
        fun defaultComposeFieldColors(): ComposeFieldColors {
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

        fun defaultComposeFieldTextStyle(): ComposeFieldTextStyle {
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

        fun fromMap(map: Map<String, Any>): ComposeFieldStyle {
            return ComposeFieldStyle(
                textSizes = ComposeFieldTextStyle.fromMap(map["textSizes"] as Map<String, Any>),
                colors = ComposeFieldColors.fromMap(map["colors"] as Map<String, Int>),
                fieldStyle = ComposeFieldTheme.FieldStyle.valueOf(map["fieldStyle"] as String),
                tooltipPersisted = (map["toolTipPersisted"] as String).toBoolean()
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
){
    fun toMap(): Map<String, Any> = mapOf(
        "textSize" to textSize,
        "labelSize" to labelSize,
        "hintSize" to hintSize,
        "errorSize" to errorSize,
        "infoSize" to infoSize,
        "fontWeight" to fontWeight.weight,
        "labelFontWeight" to labelFontWeight.weight,
        "hintFontWeight" to hintFontWeight.weight,
        "errorFontWeight" to errorFontWeight.weight,
        "infoFontWeight" to infoFontWeight.weight
    )

    companion object {
        fun fromMap(map: Map<String, Any>) = ComposeFieldTextStyle(
            map["textSize"] as Int,
            map["labelSize"] as Int,
            map["hintSize"] as Int,
            map["errorSize"] as Int,
            map["infoSize"] as Int,
            FontWeight(map["fontWeight"] as Int),
            FontWeight(map["labelFontWeight"] as Int),
            FontWeight(map["hintFontWeight"] as Int),
            FontWeight(map["errorFontWeight"] as Int),
            FontWeight(map["infoFontWeight"] as Int),
        )
    }

}

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
){
    fun toMap(): Map<String, Int> = mapOf(
        "textColor" to textColor.toArgb(),
        "labelColor" to labelColor.toArgb(),
        "hintColor" to hintColor.toArgb(),
        "infoColor" to infoColor.toArgb(),
        "errorColor" to errorColor.toArgb(),
        "focusedLabelColor" to focusedLabelColor.toArgb(),
        "unfocusedLabelColor" to unfocusedLabelColor.toArgb(),
        "focusedBorderColor" to focusedBorderColor.toArgb(),
        "unfocusedBorderColor" to unfocusedBorderColor.toArgb(),
        "errorBorderColor" to errorBorderColor.toArgb(),
        "containerColor" to containerColor.toArgb()
    )

    companion object {
        fun fromMap(map: Map<String, Int>) = ComposeFieldColors(
            Color(map["textColor"]!!),
            Color(map["labelColor"]!!),
            Color(map["hintColor"]!!),
            Color(map["infoColor"]!!),
            Color(map["errorColor"]!!),
            Color(map["focusedLabelColor"]!!),
            Color(map["unfocusedLabelColor"]!!),
            Color(map["focusedBorderColor"]!!),
            Color(map["unfocusedBorderColor"]!!),
            Color(map["errorBorderColor"]!!),
            Color(map["containerColor"]!!)
        )
    }

}


internal object ComposeFontRegistry {
    private val fontMap = mutableMapOf<String, FontFamily>()

    fun register(id: String, fontFamily: FontFamily) {
        fontMap[id] = fontFamily
    }

    fun resolve(id: String): FontFamily = fontMap[id] ?: FontFamily.Default
}


