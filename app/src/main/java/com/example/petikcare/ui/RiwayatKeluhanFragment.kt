package com.example.petikcare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.adapter.RiwayatPagerAdapter
import com.example.petikcare.data.local.room.ComplaintDao
import com.example.petikcare.data.local.room.ComplaintDatabase
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.petikcare.databinding.FragmentRiwayatKeluhanBinding
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ViewModelFactory
import com.example.retrofit.ApiConfig
import com.google.android.material.tabs.TabLayoutMediator

class RiwayatKeluhanFragment : Fragment() {
    private var _binding: FragmentRiwayatKeluhanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ComplaintViewModel
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

        val db = ComplaintDatabase.getInstance(requireContext())
        val dao = db.complaintDao()
        val repository = ComplaintRepository(ApiConfig.getApiService(requireContext()), dao, requireContext())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[ComplaintViewModel::class.java]


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
    }
}