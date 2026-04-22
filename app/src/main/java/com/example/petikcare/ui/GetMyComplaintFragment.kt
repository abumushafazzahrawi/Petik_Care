package com.example.petikcare.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.databinding.FragmentGetMyComplaintBinding
import com.example.petikcare.databinding.FragmentHomeBinding
import com.example.petikcare.viewmodel.ComplaintViewModel

class GetMyComplaintFragment : Fragment() {
    private var _binding: FragmentGetMyComplaintBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ComplaintViewModel
    private lateinit var keluhanAdapter: KeluhanAdapter

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
        val repository = com.example.petikcare.di.Injection.provideRepository(requireContext())
        val factory = com.example.petikcare.viewmodel.ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory) [ComplaintViewModel::class.java]

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
            }
        )
        binding.rvKeluhanSaya.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKeluhanSaya.adapter = keluhanAdapter

        viewModel.getMyComplaint()
        viewModel.getMyComplaintSantri.observe(viewLifecycleOwner) { response ->
            val apiData = response.data

            val entities = apiData.map { item ->
                ComplaintEntity(
                    id = item.id,
                    namaSantri = item.santri.name,
                    title = item.title,
                    description = item.description,
                    status = item.status,
                    createdAt = item.createdAt,
                    handledNote = item.treatment?.note ?: "",
                    handledAt = item.handledAt,
                    medicineName = item.treatment?.medicinesGiven?.joinToString(", ") { it.name } ?: "",
                    medicineQuantity = item.treatment?.medicinesGiven?.joinToString(", ") { it.quantity.toString() } ?: ""
                )
            }

            keluhanAdapter.updateData(entities)

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