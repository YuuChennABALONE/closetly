package com.closetly.ui.add

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class DominantColor(val rgb: Int, val ratio: Float)

object ColorUtils {

    /**
     * 在点击点附近取一个方形区域，做轻量 k-means(k=3) 聚类，得到 1~3 个主色。
     * - minRatio：占比阈值（避免噪声小色块）
     * - minDist：颜色差异阈值（避免近似色重复）
     */
    fun extractDominantColors(
        bmp: Bitmap,
        cx: Int,
        cy: Int,
        radius: Int = 24,
        k: Int = 3,
        minRatio: Float = 0.18f,
        minDist: Float = 35f
    ): List<DominantColor> {
        val x0 = max(0, cx - radius)
        val y0 = max(0, cy - radius)
        val x1 = min(bmp.width - 1, cx + radius)
        val y1 = min(bmp.height - 1, cy + radius)

        val pixels = ArrayList<Int>((x1 - x0 + 1) * (y1 - y0 + 1))
        for (y in y0..y1) for (x in x0..x1) pixels.add(bmp.getPixel(x, y))
        if (pixels.isEmpty()) return emptyList()

        val centers = IntArray(k) { pixels[(it * pixels.size / k).coerceIn(0, pixels.size - 1)] }
        val assign = IntArray(pixels.size)

        repeat(7) {
            // assign
            for (i in pixels.indices) {
                val p = pixels[i]
                var best = 0
                var bestD = dist(p, centers[0])
                for (c in 1 until k) {
                    val d = dist(p, centers[c])
                    if (d < bestD) { bestD = d; best = c }
                }
                assign[i] = best
            }

            // update centers
            val sumR = IntArray(k)
            val sumG = IntArray(k)
            val sumB = IntArray(k)
            val cnt = IntArray(k)
            for (i in pixels.indices) {
                val c = assign[i]
                val p = pixels[i]
                sumR[c] += Color.red(p)
                sumG[c] += Color.green(p)
                sumB[c] += Color.blue(p)
                cnt[c] += 1
            }
            for (c in 0 until k) {
                if (cnt[c] == 0) continue
                centers[c] = Color.rgb(sumR[c] / cnt[c], sumG[c] / cnt[c], sumB[c] / cnt[c])
            }
        }

        val cnt = IntArray(k)
        for (i in pixels.indices) cnt[assign[i]]++
        val total = pixels.size.toFloat()

        val raw = (0 until k).map { i -> DominantColor(centers[i], cnt[i] / total) }
            .sortedByDescending { it.ratio }

        val out = mutableListOf<DominantColor>()
        for (c in raw) {
            if (c.ratio < minRatio) continue
            if (out.none { dist(it.rgb, c.rgb) < minDist }) out.add(c)
            if (out.size >= 3) break
        }
        if (out.isEmpty()) out.add(raw.first())
        return out
    }

    private fun dist(a: Int, b: Int): Float {
        val dr = (Color.red(a) - Color.red(b)).toFloat()
        val dg = (Color.green(a) - Color.green(b)).toFloat()
        val db = (Color.blue(a) - Color.blue(b)).toFloat()
        return sqrt(dr*dr + dg*dg + db*db)
    }

    /**
     * 把 RGB 映射成更“可用”的常见颜色名（近邻匹配）。
     * 你后续想更精细（比如分深浅），也可以扩展 palette。
     */
    fun nearestColorName(rgb: Int): String {
        val palette = listOf(
            "黑" to Color.rgb(0,0,0),
            "白" to Color.rgb(255,255,255),
            "灰" to Color.rgb(140,140,140),
            "深灰" to Color.rgb(80,80,80),
            "米白" to Color.rgb(245,245,235),
            "红" to Color.rgb(220,20,60),
            "酒红" to Color.rgb(128,0,32),
            "橙" to Color.rgb(255,140,0),
            "黄" to Color.rgb(255,215,0),
            "绿" to Color.rgb(34,139,34),
            "军绿" to Color.rgb(85,107,47),
            "蓝" to Color.rgb(30,144,255),
            "藏蓝" to Color.rgb(25,25,112),
            "牛仔蓝" to Color.rgb(40,70,120),
            "紫" to Color.rgb(138,43,226),
            "粉" to Color.rgb(255,105,180),
            "棕" to Color.rgb(139,69,19),
            "驼色" to Color.rgb(210,180,140),
            "卡其" to Color.rgb(195,176,145)
        )
        var best = palette[0].first
        var bestD = dist(rgb, palette[0].second)
        for (i in 1 until palette.size) {
            val d = dist(rgb, palette[i].second)
            if (d < bestD) { bestD = d; best = palette[i].first }
        }
        return best
    }
}
