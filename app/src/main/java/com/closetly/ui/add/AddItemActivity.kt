package com.closetly.ui.add

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.closetly.data.ClothingItem
import com.closetly.databinding.ActivityAddItemBinding
import com.closetly.ui.common.AppViewModelFactory
import com.closetly.ui.common.Colors
import com.google.android.material.chip.Chip
import java.io.File
import java.util.Calendar
import java.util.UUID
import kotlin.math.roundToInt

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var vm: AddItemViewModel

    private var selectedUri: Uri? = null
    private var previewBitmap: Bitmap? = null

    private var currentItem: ClothingItem? = null
    private val selectedColors = linkedSetOf<String>()
    private var purchaseAt: Long? = null

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri = uri
            binding.preview.setImageURI(uri)
            previewBitmap = decodeBitmap(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this, AppViewModelFactory(application))
            .get(AddItemViewModel::class.java)

        // 类别：离线“向量相似度检索”提示
        val idx = CategoryIndex.get(this)
        binding.categoryEdit.setAdapter(VectorCategoryAdapter(this, idx))

        // 图片点击取色
        binding.preview.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val bmp = previewBitmap ?: return@setOnTouchListener false
                val (bx, by) = mapTouchToBitmap(
                    event.x, event.y,
                    binding.preview.width, binding.preview.height,
                    bmp.width, bmp.height
                )
                val colors = ColorUtils.extractDominantColors(bmp, bx, by)
                colors.forEach { c -> addColorChip(ColorUtils.nearestColorName(c.rgb)) }
                true
            } else {
                false
            }
        }

        binding.btnPick.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnAddColor.setOnClickListener {
            val c = binding.colorEdit.text?.toString()?.trim().orEmpty()
            if (c.isNotBlank()) {
                addColorChip(c)
                binding.colorEdit.setText("")
            }
        }

        binding.btnPalette.setOnClickListener { openPaletteDialog() }
        binding.purchaseEdit.setOnClickListener { openDatePicker() }

        // 编辑模式：从衣橱页点击进入
        val editId = intent.getLongExtra("item_id", -1L)
        if (editId > 0) {
            binding.title.text = "编辑衣物"
            binding.btnDelete.visibility = android.view.View.VISIBLE
            vm.load(editId) { item ->
                if (item == null) {
                    Toast.makeText(this, "未找到该衣物记录", Toast.LENGTH_SHORT).show()
                    finish()
                    return@load
                }
                currentItem = item
                bindItem(item)
            }
        }

        binding.btnSave.setOnClickListener {
            val item = buildItemOrNull() ?: return@setOnClickListener
            val old = currentItem
            if (old == null) {
                vm.add(item) {
                    Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                vm.update(item) {
                    // 若换了照片，删除旧文件
                    if (old.imagePath != item.imagePath) runCatching { File(old.imagePath).delete() }
                    Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val item = currentItem ?: return@setOnClickListener
            AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setMessage("删除后不可恢复（图片文件也会一并删除）。")
                .setPositiveButton("删除") { _, _ ->
                    vm.delete(item) {
                        runCatching { File(item.imagePath).delete() }
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun bindItem(item: ClothingItem) {
        val f = File(item.imagePath)
        if (f.exists()) {
            val uri = Uri.fromFile(f)
            selectedUri = uri
            binding.preview.setImageURI(uri)
            previewBitmap = decodeBitmap(uri)
        } else {
            selectedUri = null
            previewBitmap = null
            binding.preview.setImageDrawable(null)
        }

        binding.categoryEdit.setText(item.category, false)

        selectedColors.clear()
        binding.colorChips.removeAllViews()
        item.color.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { addColorChip(it) }

        purchaseAt = item.purchaseAt
        binding.purchaseEdit.setText(item.purchaseAt?.let { tsToYmd(it) }.orEmpty())
        binding.materialEdit.setText(item.material.orEmpty())
        binding.brandEdit.setText(item.brand.orEmpty())
        binding.sizeEdit.setText(item.size.orEmpty())
        binding.priceEdit.setText(item.price?.toString().orEmpty())
        binding.noteEdit.setText(item.note.orEmpty())
    }

    private fun buildItemOrNull(): ClothingItem? {
        val category = binding.categoryEdit.text?.toString()?.trim().orEmpty()
        if (category.isBlank()) {
            Toast.makeText(this, "类别为必填项", Toast.LENGTH_SHORT).show()
            return null
        }

        val colors = selectedColors.joinToString(",")
        if (colors.isBlank()) {
            Toast.makeText(this, "颜色为必填项：可点图片自动取色，或从色板/手动添加", Toast.LENGTH_SHORT).show()
            return null
        }

        val existing = currentItem
        val imgPath: String = if (existing != null && selectedUri?.scheme == "file" && File(existing.imagePath).exists()) {
            // 编辑时未换图：保留原图路径
            existing.imagePath
        } else {
            val uri = selectedUri ?: run {
                Toast.makeText(this, "请先选择照片", Toast.LENGTH_SHORT).show()
                return null
            }
            copyIntoAppStorage(uri) ?: run {
                Toast.makeText(this, "保存图片失败，请重试", Toast.LENGTH_SHORT).show()
                return null
            }
        }

        val material = binding.materialEdit.text?.toString()?.trim().takeIf { !it.isNullOrBlank() }
        val brand = binding.brandEdit.text?.toString()?.trim().takeIf { !it.isNullOrBlank() }
        val size = binding.sizeEdit.text?.toString()?.trim().takeIf { !it.isNullOrBlank() }
        val note = binding.noteEdit.text?.toString()?.trim().takeIf { !it.isNullOrBlank() }
        val price = binding.priceEdit.text?.toString()?.trim()?.toDoubleOrNull()

        return if (existing == null) {
            ClothingItem(
                imagePath = imgPath,
                category = category,
                color = colors,
                purchaseAt = purchaseAt,
                material = material,
                brand = brand,
                size = size,
                price = price,
                note = note
            )
        } else {
            existing.copy(
                imagePath = imgPath,
                category = category,
                color = colors,
                purchaseAt = purchaseAt,
                material = material,
                brand = brand,
                size = size,
                price = price,
                note = note
            )
        }
    }

    private fun addColorChip(colorName: String) {
        val c = colorName.trim()
        if (c.isEmpty()) return
        if (!selectedColors.add(c)) return

        val chip = Chip(this).apply {
            text = c
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedColors.remove(c)
                binding.colorChips.removeView(this)
            }
        }
        binding.colorChips.addView(chip)
    }

    private fun openPaletteDialog() {
        val items = Colors.all.filter { it != "多色" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("选择颜色（可重复打开多选）")
            .setItems(items) { _, which ->
                addColorChip(items[which])
            }
            .setNegativeButton("关闭", null)
            .show()
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        purchaseAt?.let { cal.timeInMillis = it }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            purchaseAt = c.timeInMillis
            binding.purchaseEdit.setText(tsToYmd(purchaseAt!!))
        }, y, m, d).show()
    }

    private fun tsToYmd(ts: Long): String {
        val c = Calendar.getInstance().apply { timeInMillis = ts }
        val y = c.get(Calendar.YEAR)
        val m = c.get(Calendar.MONTH) + 1
        val d = c.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(y, m, d)
    }

    private fun decodeBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= 28) {
                val src = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                    decoder.isMutableRequired = false
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (_: Exception) { null }
    }

    private fun copyIntoAppStorage(uri: Uri): String? {
        return try {
            val dir = File(filesDir, "images").apply { mkdirs() }
            val outFile = File(dir, "${UUID.randomUUID()}.jpg")
            contentResolver.openInputStream(uri).use { input ->
                if (input == null) return null
                outFile.outputStream().use { output -> input.copyTo(output) }
            }
            outFile.absolutePath
        } catch (_: Exception) { null }
    }

    /**
     * 把 ImageView 上的触点坐标映射到 Bitmap 像素坐标（适配 centerCrop）。
     */
    private fun mapTouchToBitmap(
        touchX: Float,
        touchY: Float,
        viewW: Int,
        viewH: Int,
        bmpW: Int,
        bmpH: Int
    ): Pair<Int, Int> {
        if (viewW == 0 || viewH == 0) return 0 to 0
        val sx = viewW.toFloat() / bmpW.toFloat()
        val sy = viewH.toFloat() / bmpH.toFloat()
        val scale = maxOf(sx, sy) // centerCrop
        val scaledW = bmpW * scale
        val scaledH = bmpH * scale
        val dx = (viewW - scaledW) / 2f
        val dy = (viewH - scaledH) / 2f
        val bx = ((touchX - dx) / scale).roundToInt().coerceIn(0, bmpW - 1)
        val by = ((touchY - dy) / scale).roundToInt().coerceIn(0, bmpH - 1)
        return bx to by
    }
}
