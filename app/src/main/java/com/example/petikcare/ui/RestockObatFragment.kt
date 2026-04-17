package com.example.petikcare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.petikcare.databinding.FragmentRestockObatBinding
import com.example.petikcare.viewmodel.ObatViewModel


class RestockObatFragment : Fragment() {
    private var _binding: FragmentRestockObatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ObatViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRestockObatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = com.example.petikcare.di.ObatInjection.provideObatRepository(requireContext())
        val factory = com.example.petikcare.viewmodel.ObatViewModelFactory(repository)
        viewModel = androidx.lifecycle.ViewModelProvider(this, factory).get(ObatViewModel::class.java)

        val id = arguments?.getString("id") ?: ""
        val nama = arguments?.getString("nama")
        val stok = arguments?.getInt("stok")

        binding.tvNamaObat.text = "\uD83D\uDC8A Nama Obat: $nama"
        binding.tvStokSekarang.text = "\uD83D\uDCE6 Stok saat ini: ${stok.toString()}"

        binding.btnSimpan.setOnClickListener {
            val inputStok = binding.etInputStokBaru.text.toString()

            if (inputStok.isEmpty()) {
                binding.tilInputStokBaru.error = "Masukkan jumlah stok baru"
                return@setOnClickListener
            }
            viewModel.restockObat(id, inputStok.toInt())

        }
        viewModel.restockResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), "Stok berhasil diperbarui", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}