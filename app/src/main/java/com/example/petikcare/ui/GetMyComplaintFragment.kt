package com.example.petikcare.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.databinding.FragmentGetMyComplaintBinding
import com.example.petikcare.databinding.FragmentHomeBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ViewModelFactory

class GetMyComplaintFragment : Fragment() {
    private var _binding: FragmentGetMyComplaintBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ComplaintViewModel
    private lateinit var keluhanAdapter: KeluhanAdapter

    companion object {
        const val PREF_NAME = "petikCare"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGetMyComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = Injection.provideRepository(requireContext())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory) [ComplaintViewModel::class.java]

        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        val myName = sharedPref.getString("USERNAME", "User") ?: ""
        val role = "santri"
        keluhanAdapter = KeluhanAdapter(
            listKeluhan = emptyList(), role = role,
            onPendingClick = {},
            onDoneClick = {
                val bundle = Bundle().apply {
                    putString("id", it.id)
                    putString("nama", it.namaSantri)
                    putString("keluhan", it.title)
                    putString("status", it.status)
                    putString("date", it.createdAt)
                    putString("description", it.description)
                    putString("handled_at", it.handledAt)
                    putString("catatan", it.handledNote)
                    putString("obat", it.medicineName)
                    putString("quantity", it.medicineQuantity)
                }
                findNavController().navigate(R.id.detailKeluhanFragment, bundle)

            },
            onDeleteClick = { keluhan ->
                showDeleteDialog(keluhan.id)
                Toast.makeText(requireContext(), "Keluhan berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvKeluhanSaya.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKeluhanSaya.adapter = keluhanAdapter

        viewModel.getMyComplaint()
        viewModel.complaints.observe(viewLifecycleOwner) { data ->
            val filteredData = data.filter { it.namaSantri.equals(myName, ignoreCase = true) }
                .sortedByDescending { it.createdAt }

            keluhanAdapter.updateData(filteredData)

            binding.tvBelumAdaKeluhan.visibility =
                if (filteredData.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun showDeleteDialog(id: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Hapus Keluhan")
            setMessage("Apakah Anda yakin ingin menghapus keluhan ini?")
            setPositiveButton("Ya") { _, _ ->
                viewModel.deleteComplaintSantri(id)

            }
            setNegativeButton("Tidak", null)
            show()
        }
    }
}