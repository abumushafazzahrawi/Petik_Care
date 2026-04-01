package com.example.petikcare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.petikcare.databinding.BottomSheetKeluhanBinding
import com.example.petikcare.response_complaint.DataComplaints
import com.example.petikcare.response_complaint.MedicineGiven
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.toString

class KeluhanBottomSheet(private val keluhan: DataComplaints): BottomSheetDialogFragment() {
    private var _binding: BottomSheetKeluhanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComplaintViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetKeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set data dari klik
        binding.tvNama.text = "Nama: ${keluhan.santri.name}"
        binding.tvKeluhan.text = "Keluhan: ${keluhan.title}"

        // Observe data complaint
        viewModel.responseComplaint.observe(viewLifecycleOwner) { data ->
            data?.let {
                Toast.makeText(requireContext(), "Berhasil respond", Toast.LENGTH_SHORT).show()
                dismiss()
                viewModel.clearResponse()
            }
        }

        // Reset error saat user ketik lagi
        binding.etCatatan.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilCatatan.error = null
            }

            // Validasi spinner
            val selectObat = binding.spinnerObat.selectedItem.toString() ?: ""

            if (selectObat.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Pilih obat terlebih dahulu", Toast.LENGTH_SHORT)
                    .show()
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.btnSimpan.isEnabled = !isLoading
            }

            binding.btnSimpan.setOnClickListener {
                val note = binding.etCatatan.text.toString()

                if (note.isEmpty()) {
                    binding.tilCatatan.error = "Catatan tidak boleh kosong"
                    return@setOnClickListener
                }

                val request = buildRequest()
                val id = keluhan.id
                viewModel.respondComplaint(id, request, requireContext())
            }
        }
    }

    private fun buildRequest(): RespondRequest {
        return RespondRequest(
            note = binding.etCatatan.text.toString(),

            medicinesGiven = listOf(
                MedicineGiven(
                    name = binding.spinnerObat.selectedItem.toString(),
                    quantity = 1
                )
            )
        )
    }
}