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

    data class ColorOption(val name: String, val rgb: Int)

    // 常用色板（名称 + 颜色值），用于“色块 + 文字”展示
    val palette: List<ColorOption> = listOf(
        ColorOption("黑", 0xFF000000.toInt()),
        ColorOption("炭黑", 0xFF1C1C1C.toInt()),
        ColorOption("深灰", 0xFF404040.toInt()),
        ColorOption("灰", 0xFF808080.toInt()),
        ColorOption("浅灰", 0xFFB0B0B0.toInt()),
        ColorOption("银灰", 0xFFC0C0C0.toInt()),
        ColorOption("白", 0xFFFFFFFF.toInt()),
        ColorOption("米白", 0xFFF5F5DC.toInt()),
        ColorOption("奶油白", 0xFFFFFDD0.toInt()),

        ColorOption("红", 0xFFDC143C.toInt()),
        ColorOption("深红", 0xFF8B0000.toInt()),
        ColorOption("酒红", 0xFF800020.toInt()),
        ColorOption("砖红", 0xFFB22222.toInt()),

        ColorOption("橙", 0xFFFF8C00.toInt()),
        ColorOption("橘红", 0xFFFF4500.toInt()),
        ColorOption("珊瑚橙", 0xFFFF7F50.toInt()),

        ColorOption("黄", 0xFFFFD700.toInt()),
        ColorOption("姜黄", 0xFFDAA520.toInt()),
        ColorOption("卡其", 0xFFC3B091.toInt()),
        ColorOption("米色", 0xFFF5F5DC.toInt()),

        ColorOption("绿", 0xFF228B22.toInt()),
        ColorOption("军绿", 0xFF556B2F.toInt()),
        ColorOption("墨绿", 0xFF006400.toInt()),
        ColorOption("草绿", 0xFF7CFC00.toInt()),
        ColorOption("薄荷绿", 0xFF98FF98.toInt()),

        ColorOption("蓝", 0xFF1E90FF.toInt()),
        ColorOption("藏蓝", 0xFF001F3F.toInt()),
        ColorOption("海军蓝", 0xFF000080.toInt()),
        ColorOption("天蓝", 0xFF87CEEB.toInt()),
        ColorOption("湖蓝", 0xFF00BFFF.toInt()),
        ColorOption("牛仔蓝", 0xFF2F4F7F.toInt()),

        ColorOption("紫", 0xFF8A2BE2.toInt()),
        ColorOption("深紫", 0xFF4B0082.toInt()),
        ColorOption("薰衣草紫", 0xFFE6E6FA.toInt()),

        ColorOption("粉", 0xFFFF69B4.toInt()),
        ColorOption("浅粉", 0xFFFFB6C1.toInt()),
        ColorOption("玫红", 0xFFFF1493.toInt()),

        ColorOption("棕", 0xFF8B4513.toInt()),
        ColorOption("咖啡", 0xFF6F4E37.toInt()),
        ColorOption("巧克力棕", 0xFFD2691E.toInt()),
        ColorOption("驼色", 0xFFC19A6B.toInt()),

        ColorOption("青", 0xFF008B8B.toInt()),
        ColorOption("青绿", 0xFF00A86B.toInt()),
        ColorOption("青蓝", 0xFF007BA7.toInt()),

        ColorOption("金", 0xFFFFD700.toInt()),
        ColorOption("香槟金", 0xFFF7E7CE.toInt()),
        ColorOption("铜色", 0xFFB87333.toInt()),

        ColorOption("多色", 0xFF777777.toInt())
    )

    // 仅名称列表（兼容旧逻辑）
    val all: List<String> = palette.map { it.name }

    fun isNeutral(c: String) = c in setOf(
        "黑","炭黑","深灰","灰","浅灰","银灰","白","米白","奶油白","米色","卡其","驼色","牛仔蓝","藏蓝","海军蓝"
    )
}
