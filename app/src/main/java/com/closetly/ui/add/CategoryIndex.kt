package com.closetly.ui.add

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

/**
 * 轻量离线“向量检索”：
 * - 以字符 trigram 的 hash 计数作为 embedding（256维）
 * - 余弦相似度 TopK
 *
 * 优点：无需模型、无网络、速度快、能满足“输入联想 + 相似度检索”的交互体验。
 */
class CategoryIndex private constructor(
    private val labels: List<String>,
    private val vecs: List<FloatArray>
) {
    fun topK(query: String, k: Int = 30): List<String> {
        val q = query.trim()
        if (q.isEmpty()) return labels.take(k)
        val qv = embed(q)
        val scored = labels.indices
            .map { i -> i to dot(qv, vecs[i]) }
            .sortedByDescending { it.second }
        return scored.take(k).map { labels[it.first] }
    }

    companion object {
        private const val DIM = 256
        @Volatile private var INSTANCE: CategoryIndex? = null

        fun get(context: Context): CategoryIndex =
            INSTANCE ?: synchronized(this) { INSTANCE ?: load(context).also { INSTANCE = it } }

        private fun load(context: Context): CategoryIndex {
            val labels = mutableListOf<String>()
            context.assets.open("categories_zh.txt").use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).forEachLine { line ->
                    val t = line.trim()
                    if (t.isNotEmpty()) labels.add(t)
                }
            }
            val vecs = labels.map { embed(it) }
            return CategoryIndex(labels, vecs)
        }

        private fun embed(text: String): FloatArray {
            val v = FloatArray(DIM)
            val s = text.lowercase()
            val padded = "  $s  "
            for (i in 0 until padded.length - 2) {
                val tri = padded.substring(i, i + 3)
                val idx = (tri.hashCode() and Int.MAX_VALUE) % DIM
                v[idx] += 1f
            }
            var norm = 0f
            for (x in v) norm += x * x
            norm = sqrt(norm)
            if (norm > 1e-6f) for (i in v.indices) v[i] /= norm
            return v
        }

        private fun dot(a: FloatArray, b: FloatArray): Float {
            var s = 0f
            for (i in a.indices) s += a[i] * b[i]
            return s
        }
    }
}
