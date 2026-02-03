package com.closetly.ui.closet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.closetly.databinding.FragmentClosetBinding
import com.closetly.ui.add.AddItemActivity
import com.closetly.ui.common.AppViewModelFactory

class ClosetFragment : Fragment() {

    private var _binding: FragmentClosetBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: ClosetViewModel
    private lateinit var adapter: ClothingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClosetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this, AppViewModelFactory(requireActivity().application))
            .get(ClosetViewModel::class.java)

        adapter = ClothingAdapter()
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycler.adapter = adapter

        binding.searchEdit.doAfterTextChanged { vm.setQuery(it?.toString().orEmpty()) }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddItemActivity::class.java))
        }

        vm.filtered.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
