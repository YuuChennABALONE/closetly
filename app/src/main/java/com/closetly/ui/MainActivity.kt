package com.closetly.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.closetly.R
import com.closetly.databinding.ActivityMainBinding
import com.closetly.ui.closet.ClosetFragment
import com.closetly.ui.stats.StatsFragment
import com.closetly.ui.today.TodayFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ClosetFragment())
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val f = when (item.itemId) {
                R.id.nav_closet -> ClosetFragment()
                R.id.nav_today -> TodayFragment()
                R.id.nav_stats -> StatsFragment()
                else -> null
            }
            if (f != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, f)
                    .commit()
                true
            } else false
        }
    }
}
