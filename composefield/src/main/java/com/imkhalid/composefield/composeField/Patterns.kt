package com.techInfo.composefieldproject.composeField

enum class Patterns(var value:String,val length:Int,vararg var  pattern:String) {
    CNIC("#####-#######-#",13,"^[0-9]{5}-[0-9]{7}-[0-9]\$","^(?!([0-9])\\1{4})[0-9]{5}-[0-9]{7}-[0-9]\$"),
    MOBILE("### #######",10,"^[0-9]{3} [0-9]{7}"),
    EMAIL("",0,"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+((\\.[A-Za-z0-9]+)|(\\-[A-Za-z0-9]+)|(\\_[A-Za-z0-9]+))*(\\.[A-Za-z]{2,})$"),
    NONE("",0,"")
}