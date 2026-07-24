package com.imkhalid.composefield.composeField

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun screenSize(): Size {
    val configuration = LocalConfiguration.current
    var  width = configuration.screenWidthDp.toFloat()
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
      width =  configuration.screenHeightDp.toFloat()

    var  height = configuration.screenHeightDp.toFloat()
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        height =  configuration.screenWidthDp.toFloat()

    return Size(
        width = width,
        height = height
    )
}

@Composable
fun responsiveSize(size: Size): Size {
    return Size(
        width = responsiveWidth(size = size.width.toInt()).value,
        height = responsiveHeight(size = size.height.toInt()).value
    )
}

@Composable
fun responsiveSize(size: Int): Dp {
    return responsiveWidth(size = size)
}

@Composable
fun responsiveTextSize(size: Int): Float {
    val screenSize = screenSize()
    val baseSize = Size.getBaseDesignSize()
    val widthFactor = size.toFloat() / baseSize.width
    return screenSize.width * widthFactor
}

@Composable
fun responsiveHeight(size: Int): Dp {
    val screenSize = screenSize()
    val baseSize = Size.getBaseDesignSize()
    val heightFactor = size.toFloat() / baseSize.height
    return (screenSize.height * heightFactor).dp
}

@Composable fun responsiveWidth(size: Int): Dp = responsiveTextSize(size).dp

@Composable fun responsiveHPaddings(size: Int): Dp = responsiveWidth(size)

@Composable fun responsiveVPaddings(size: Int): Dp = responsiveHeight(size)
