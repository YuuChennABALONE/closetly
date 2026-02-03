package com.closetly.ui.add

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.closetly.data.ClothingItem
import com.closetly.databinding.ActivityAddItemBinding
import com.closetly.ui.common.AppViewModelFactory
import com.closetly.ui.common.Categories
import com.closetly.ui.common.Colors
import java.io.File
import java.util.UUID

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private lateinit var vm: AddItemViewModel

    private var selectedUri: Uri? = null

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri = uri
            binding.preview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this, AppViewModelFactory(application))
            .get(AddItemViewModel::class.java)

        binding.categoryEdit.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, Categories.all))
        binding.colorEdit.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, Colors.all))

        binding.btnPick.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSave.setOnClickListener {
            val uri = selectedUri
            val category = binding.categoryEdit.text?.toString()?.trim().orEmpty()
            val color = binding.colorEdit.text?.toString()?.trim().orEmpty()

            if (uri == null || category.isBlank() || color.isBlank()) {
                Toast.makeText(this, "请先选择照片，并填写类别/颜色", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedPath = copyIntoAppStorage(uri)
            if (savedPath == null) {
                Toast.makeText(this, "保存图片失败，请重试", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.add(ClothingItem(imagePath = savedPath, category = category, color = color)) {
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun copyIntoAppStorage(uri: Uri): String? {
        return try {
            val dir = File(filesDir, "images").apply { mkdirs() }
            val outFile = File(dir, "${UUID.randomUUID()}.jpg")

            contentResolver.openInputStream(uri).use { input ->
                if (input == null) return null
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
