package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.petikcare.R
import com.example.petikcare.databinding.FragmentEditObatBinding
import com.example.petikcare.viewmodel.ObatViewModel


class EditObatFragment : Fragment() {
    private var _binding: FragmentEditObatBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ObatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditObatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository =
            com.example.petikcare.di.ObatInjection.provideObatRepository(requireContext())
        val factory = com.example.petikcare.viewmodel.ObatViewModelFactory(repository)
        viewModel =
            androidx.lifecycle.ViewModelProvider(this, factory).get(ObatViewModel::class.java)

        val id = arguments?.getString("id") ?: ""
        val nama = arguments?.getString("nama") ?: ""
        val sediaan = arguments?.getString("sediaan") ?: ""

        binding.tvNamaObat.text = "\uD83D\uDC8A Nama obat saat ini: $nama"
        binding.etNamaObatBaru.setText(nama)

        val listSediaan = listOf(
            "tablet",
            "kapsul",
            "pil",
            "suppositoria",
            "ovula",
            "salep",
            "krim",
            "gel",
            "sirup",
            "suspensi"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listSediaan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerItemObat.adapter = adapter

        val posisiAwal = listSediaan.indexOf(sediaan)
        if (posisiAwal >= 0) binding.spinnerItemObat.setSelection(posisiAwal)

        binding.btnSimpan.setOnClickListener {
            val namaObat = binding.etNamaObatBaru.text.toString()
            val sediaanObat = binding.spinnerItemObat.selectedItem.toString()

            if (namaObat.isEmpty()) {
                binding.tilNamaObatBaru.error = "Masukkan nama obat baru"
                return@setOnClickListener
            }
            viewModel.editObat(id, namaObat, sediaanObat)
        }
        viewModel.editResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), "Data berhasil diubah", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }

        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}