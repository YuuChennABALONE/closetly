package com.closetly.ui.today

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.closetly.databinding.FragmentTodayBinding
import com.closetly.ui.common.AppViewModelFactory
import java.io.File

class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: TodayViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this, AppViewModelFactory(requireActivity().application))
            .get(TodayViewModel::class.java)

        // default selection
        binding.btnDaily.isChecked = true
        binding.btnMild.isChecked = true

        binding.btnGenerate.setOnClickListener {
            val temp = when {
                binding.btnCold.isChecked -> "冷"
                binding.btnHot.isChecked -> "热"
                else -> "适中"
            }
            vm.generate(temp)
        }

        binding.btnWorn.setOnClickListener {
            if (vm.suggestion.value == null) {
                Toast.makeText(requireContext(), "先生成一次搭配", Toast.LENGTH_SHORT).show()
            } else {
                vm.markWorn()
                Toast.makeText(requireContext(), "已记录", Toast.LENGTH_SHORT).show()
            }
        }

        vm.suggestion.observe(viewLifecycleOwner) { s ->
            if (s == null) {
                binding.tip.text = getString(com.closetly.R.string.not_enough_items)
                binding.imgTop.setImageDrawable(null)
                binding.imgBottom.setImageDrawable(null)
                binding.imgShoes.setImageDrawable(null)
            } else {
                binding.tip.text = "${s.top.category}·${s.top.color} + ${s.bottom.category}·${s.bottom.color} + ${s.shoes.category}·${s.shoes.color}" +
                        (s.outer?.let { " + 外套·${it.color}" } ?: "")

                setImage(binding.imgTop, s.top.imagePath)
                setImage(binding.imgBottom, s.bottom.imagePath)
                setImage(binding.imgShoes, s.shoes.imagePath)
            }
        }
    }

    private fun setImage(iv: android.widget.ImageView, path: String) {
        val f = File(path)
        if (f.exists()) iv.setImageURI(Uri.fromFile(f)) else iv.setImageDrawable(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
