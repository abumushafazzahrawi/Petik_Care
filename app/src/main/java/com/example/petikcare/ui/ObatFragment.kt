package com.example.petikcare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.ObatAdapter
import com.example.petikcare.data.local.room.ObatDatabase
import com.example.petikcare.data.remote.ObatRepository
import com.example.petikcare.data.remote.Result
import com.example.petikcare.databinding.FragmentObatBinding
import com.example.petikcare.pengasuhan.response_obat.ObatRequest
import com.example.petikcare.viewmodel.ObatViewModel
import com.example.petikcare.viewmodel.ObatViewModelFactory
import com.example.retrofit.ApiConfig
import com.google.android.material.textfield.TextInputEditText


class ObatFragment : Fragment() {
    private var _binding: FragmentObatBinding? = null
    private val binding get() = _binding!!
    private lateinit var obatAdapter: ObatAdapter

    private lateinit var viewModel : ObatViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentObatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = ObatDatabase.getInstance(requireContext())
        val dao = db.obatDao()
        val repository = ObatRepository(ApiConfig.getApiService(requireContext()), dao)
        val factory = ObatViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ObatViewModel::class.java]


        binding.btnFabAdd.setOnClickListener {
            createDialog()
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.rvObat.layoutManager = LinearLayoutManager(requireContext())
        obatAdapter = ObatAdapter(listOf())
        binding.rvObat.adapter = obatAdapter

        viewModel.listObat.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressbar.visibility = View.GONE
                    obatAdapter.updateData(result.data)
                }

                is Result.Error -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun createDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_obat, null)

        val etNama  = dialogView.findViewById<TextInputEditText>(R.id.et_nama_obat)
        val etDeskripsi = dialogView.findViewById<TextInputEditText>(R.id.et_deskripsi)
        val etStok = dialogView.findViewById<TextInputEditText>(R.id.et_stok)
        val spinnerSediaan = dialogView.findViewById<Spinner>(R.id.spinner_item_obat)

        val listSediaan = listOf(
            "Obat tetes",
            "Aerosol",
            "Tablet",
            "Gel",
            "Krim",
            "Suspensi",
            "Injeksi",
            "Sirup",
            "Ovula",
            "Infus",
            "Kapsul",
            "Suppositoria",
            "Emulsi",
            "Salep",
            "Pil"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listSediaan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSediaan.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Obat")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->

                val request = ObatRequest(
                    nama_obat = etNama.text.toString(),
                    deskripsi = etDeskripsi.text.toString(),
                    stok = etStok.text.toString().toIntOrNull() ?: 0,
                    sediaan = spinnerSediaan.selectedItem.toString()
                )
                viewModel.createObat(request)
                Toast.makeText(requireContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}