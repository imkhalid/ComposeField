package com.imkhalid.composefield.composeField

import com.imkhalid.composefield.theme.ComposeFieldTheme

/** Created by Mirza Tahir Baig. Email: me.tahirbaig@gmail.com Date: 02/12/2023 */
data class Size(val width: Float, val height: Float) {
    companion object {
        fun getBaseDesignSize(): Size {
            return ComposeFieldTheme.baseDesignSize
        }
    }
}
