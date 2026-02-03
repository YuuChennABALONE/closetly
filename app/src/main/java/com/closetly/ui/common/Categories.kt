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
    // 常用色板（可继续扩展）
    val all = listOf("黑", "炭黑", "深灰", "灰", "浅灰", "银灰", "白", "米白", "奶油白", "红", "深红", "酒红", "砖红", "橙", "橘红", "珊瑚橙", "黄", "姜黄", "卡其", "米色", "绿", "军绿", "墨绿", "草绿", "薄荷绿", "蓝", "藏蓝", "海军蓝", "天蓝", "湖蓝", "牛仔蓝", "紫", "深紫", "薰衣草紫", "粉", "浅粉", "玫红", "棕", "咖啡", "巧克力棕", "驼色", "青", "青绿", "青蓝", "金", "香槟金", "铜色", "多色")
    fun isNeutral(c: String) = c in setOf("黑","炭黑","深灰","灰","浅灰","银灰","白","米白","奶油白","米色","卡其","驼色","牛仔蓝","藏蓝")
}
