package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.databinding.FragmentPendingBinding
import com.example.petikcare.ui.HomeFragment.Companion.PREF_NAME
import com.example.petikcare.viewmodel.ComplaintViewModel

class PendingFragment : Fragment() {
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComplaintViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = sharedPref.getString("USERNAME", null)
        val userRole = sharedPref.getString("ROLE", "santri")?: "santri"
        val role = sharedPref.getString("ROLE", "User")

        val adapter = KeluhanAdapter(
            listOf(),
            role = userRole,
            onPendingClick = { keluhan ->
                // Tampilkan bottomSheet
                val bottomSheet = KeluhanBottomSheet(keluhan)

                bottomSheet.show(parentFragmentManager, "respond")
            },
            onDoneClick = {},
            onDeleteClick = {}
        )
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter

        viewModel.complaints.observe(viewLifecycleOwner) { data ->
            Log.d("PENDING_DEBUG", "Data complaints: $data")
            val pending = data?.filter { it.status == "PENDING" } ?: emptyList() // logicnya -> jika ada data maka tamplikan, jika tidak ada tampilkan daftar kosong saja
            adapter.updateData(pending)

            if (pending.isEmpty()) {
                binding.tvPendingKosong.visibility = View.VISIBLE
                binding.rvRiwayat.visibility = View.GONE
                binding.tvPendingKosong.text = "Belum ada keluhan yang perlu ditangani"
            } else {
                binding.tvPendingKosong.visibility = View.GONE
                binding.rvRiwayat.visibility = View.VISIBLE
            }
        }

    }
}