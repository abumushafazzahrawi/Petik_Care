package com.example.petikcare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.adapter.RiwayatPagerAdapter
import com.example.petikcare.databinding.FragmentRiwayatKeluhanBinding
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.google.android.material.tabs.TabLayoutMediator

class RiwayatKeluhanFragment : Fragment() {
    private var _binding: FragmentRiwayatKeluhanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ComplaintViewModel by viewModels()
    private lateinit var adapter: KeluhanAdapter
    private lateinit var tabAdapter: RiwayatPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRiwayatKeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        tabAdapter = RiwayatPagerAdapter(this)
        binding.viewPager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pending"
                1 -> "Selesai"
                else -> null
            }
        }.attach()

        viewModel.getComplaints(requireContext())
    }
}