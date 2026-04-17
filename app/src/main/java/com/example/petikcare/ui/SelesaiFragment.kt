package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.databinding.FragmentSelesaiBinding
import com.example.petikcare.ui.HomeFragment.Companion.PREF_NAME
import com.example.petikcare.viewmodel.ComplaintViewModel

class SelesaiFragment : Fragment() {
    private var _binding: FragmentSelesaiBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: KeluhanAdapter

    private val viewModel: ComplaintViewModel by viewModels({ requireParentFragment() })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelesaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = sharedPref.getString("USERNAME", null)
        val userRole = sharedPref.getString("ROLE", "santri")?: "santri"
        val role = sharedPref.getString("ROLE", "User")

        adapter = KeluhanAdapter(
            listOf(),
            role = userRole,
            onPendingClick = {},
            onDoneClick = { keluhan ->
                val bundle = Bundle().apply {
                    putString("nama", keluhan.namaSantri)
                    putString("keluhan", keluhan.title)
                    putString("status", keluhan.status)
                    putString("date", keluhan.createdAt)
                    putString("handled_at", keluhan.handledAt)
                }
                findNavController().navigate(R.id.detailKeluhanFragment, bundle)
            }
        )

        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter

        viewModel.complaints.observe(viewLifecycleOwner) { data ->
            val selesai = data?.filter { it.status == "SELESAI" } ?: emptyList()
            adapter.updateData(selesai)

            if (selesai.isEmpty()) {
                binding.tvSelesaiKosong.visibility = View.VISIBLE
                binding.rvRiwayat.visibility = View.GONE
                binding.tvSelesaiKosong.text = "Belum ada keluhan dengan status selesai"
            } else {
                binding.tvSelesaiKosong.visibility = View.GONE
                binding.rvRiwayat.visibility = View.VISIBLE
            }
        }
    }
}