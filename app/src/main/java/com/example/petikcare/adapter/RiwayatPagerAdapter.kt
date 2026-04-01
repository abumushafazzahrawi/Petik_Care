package com.example.petikcare.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.petikcare.ui.PendingFragment
import com.example.petikcare.ui.SelesaiFragment

class RiwayatPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingFragment()
            1 -> SelesaiFragment()
            else -> PendingFragment()
        }
    }

    override fun getItemCount(): Int = 2
}
