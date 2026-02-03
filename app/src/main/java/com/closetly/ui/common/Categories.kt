package com.closetly.ui.common

object Categories {
    const val TOP = "上衣"
    const val BOTTOM = "裤子"
    const val OUTER = "外套"
    const val SHOES = "鞋"
    const val ACCESSORY = "配饰"

    val all = listOf(TOP, BOTTOM, OUTER, SHOES, ACCESSORY)
}

object Colors {
    val all = listOf("黑", "白", "灰", "蓝", "棕", "绿", "红", "黄", "紫", "粉", "米色", "牛仔蓝")
    fun isNeutral(c: String) = c in setOf("黑", "白", "灰", "米色", "牛仔蓝")
}
