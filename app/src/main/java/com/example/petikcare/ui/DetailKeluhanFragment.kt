package com.example.petikcare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.databinding.FragmentDetailKeluhanBinding
import com.example.petikcare.viewmodel.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class DetailKeluhanFragment : Fragment() {
    private var _binding: FragmentDetailKeluhanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: KeluhanAdapter
    private val viewModel: ComplaintViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailKeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nama = arguments?.getString("nama")
        val keluhan = arguments?.getString("keluhan")
        val status = arguments?.getString("status")
        val ditangani = arguments?.getString("handled_at")

        if (ditangani != null) {
            binding.tvDitangani.visibility = View.VISIBLE
            binding.tvDitangani.text = "\uD83D\uDD52 Ditangani: ${formatTanggal(ditangani)}"
        }

        binding.tvNama.text = "\uD83D\uDC64 Nama: $nama"
        binding.tvKeluhan.text = "\uD83D\uDCCC keluhan: $keluhan"
        binding.tvStatus.text = "Status: $status"

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatTanggal(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString // Fallback kalau error
        }
    }

}