package com.example.petikcare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.petikcare.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.databinding.BottomSheetKeluhanBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.di.ObatInjection
import com.example.petikcare.response_complaint.MedicinesList
import com.example.petikcare.response_complaint.RespondRequest
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ObatViewModel
import com.example.petikcare.viewmodel.ObatViewModelFactory
import com.example.petikcare.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.toString

class KeluhanBottomSheet(private val keluhan: ComplaintEntity): BottomSheetDialogFragment() {
    private var _binding: BottomSheetKeluhanBinding? = null
    private val binding get() = _binding!!

    private var listObat: List<ObatEntity> = listOf()

    private lateinit var viewModel: ComplaintViewModel
    private lateinit var obatViewModel: ObatViewModel
    val selectedMedicines = mutableListOf<View>()

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
        binding.tvNama.text = "Nama: ${keluhan.namaSantri}"
        binding.tvKeluhan.text = "Keluhan: ${keluhan.title}"

        val repository = Injection.provideRepository(requireContext())
        val obatReposotiry = ObatInjection.provideObatRepository(requireContext())

        val complaintFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, complaintFactory)[ComplaintViewModel::class.java]

        val obatFactory = ObatViewModelFactory(obatReposotiry)
        obatViewModel = ViewModelProvider(this, obatFactory)[ObatViewModel::class.java]

        // Observe data complaint
        viewModel.responseComplaint.observe(viewLifecycleOwner) { data ->
            data?.let {
                Toast.makeText(requireContext(), "Berhasil respond", Toast.LENGTH_SHORT).show()
                dismiss()
                viewModel.clearResponse()
            }
        }

        obatViewModel.getAllObat()
        obatViewModel.listObat.observe(viewLifecycleOwner) { event ->
            val obatList = event.getContentIfNotHandled() ?: return@observe
            listObat = obatList

            if (selectedMedicines.isEmpty()) {
                tambahBarisObat()
            }
        }

        binding.btnTambahObat.setOnClickListener {
            tambahBarisObat()

        }

        binding.btnSimpan.setOnClickListener {
            val note = binding.etCatatan.text.toString()

            if (note.isEmpty()) {
                Toast.makeText(requireContext(), "Data obat belun tersedia", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (selectedMedicines.isEmpty()) {
                Toast.makeText(requireContext(), "Harap tambah obat", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val request = buildRequest()
            val id = keluhan.id
            viewModel.respondComplaint(id, request)
        }
    }

    private fun tambahBarisObat() {
        val itemView =
            layoutInflater.inflate(R.layout.item_obat_input, binding.containerObat, false)

        val spinner = itemView.findViewById<Spinner>(R.id.spinner_item_obat)
        val namaObatList = listObat.map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namaObatList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        itemView.findViewById<View>(R.id.btn_delete).setOnClickListener {
            binding.containerObat.removeView(itemView)
            selectedMedicines.remove(itemView)
        }

        binding.containerObat.addView(itemView)
        selectedMedicines.add(itemView)
    }

    private fun buildRequest(): RespondRequest {
        val medicineMap = mutableMapOf<String, Int>()

        for (view in selectedMedicines) {
            val spinner = view.findViewById<Spinner>(R.id.spinner_item_obat)
            val etQty = view.findViewById<EditText>(R.id.et_quantity)

            val selectedIndex = spinner.selectedItemPosition
            val qty = etQty.text.toString().toIntOrNull() ?: 1

            if (listObat.isNotEmpty()) {
                val medicineId = listObat[selectedIndex].id
                medicineMap[medicineId] = (medicineMap[medicineId] ?: 0) + qty
            }
        }

        val medicineList = medicineMap.map { (id, quantity) ->
            MedicinesList(medicineId = id, quantity = quantity)
        }

        return RespondRequest(
            status = "SELESAI",
            catatan = binding.etCatatan.text.toString(),
            medicines = medicineList
        )
    }
}