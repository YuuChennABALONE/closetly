package com.closetly.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.closetly.databinding.FragmentStatsBinding
import com.closetly.ui.common.AppViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: StatsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this, AppViewModelFactory(requireActivity().application))
            .get(StatsViewModel::class.java)

        vm.state.observe(viewLifecycleOwner) { s ->
            binding.summary.text = "总衣物数：${s.total}"

            binding.dup.text = if (s.dups.isEmpty()) {
                "重复购买：暂无明显重复（按“类别+颜色”统计）"
            } else {
                "重复购买（按“类别+颜色”）：\n" + s.dups.joinToString("\n") { "• ${it.category}·${it.color} × ${it.c}" }
            }

            binding.topWorn.text = if (s.topWorn.isEmpty()) {
                "常穿：暂无"
            } else {
                "常穿 Top5：\n" + s.topWorn.joinToString("\n") { "• ${it.category}·${it.color}（${it.wornCount} 次）" }
            }

            binding.idle.text = if (s.idle.isEmpty()) {
                "闲置（60天+）：暂无"
            } else {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                "闲置（60天+）Top5：\n" + s.idle.joinToString("\n") {
                    val d = it.lastWornAt?.let { t -> df.format(Date(t)) } ?: "从未穿过"
                    "• ${it.category}·${it.color}（上次：$d）"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
