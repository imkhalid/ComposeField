package com.imkhalid.composefield.composeField

/**
 * Created by Mirza Tahir Baig.
 * Email: me.tahirbaig@gmail.com
 * Date: 02/12/2023
 */
data class Size(
    val width: Float,
    val height: Float
){
    companion object{
        fun getBaseDesignSize(): Size {
            return Size(width = 414f, height = 896f)
        }
    }
}
