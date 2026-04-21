package com.example.petikcare.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.petikcare.R
import com.example.petikcare.databinding.FragmentCreateComplaintSantriBinding
import com.example.petikcare.santri.RequestCreateComplaints
import com.example.petikcare.viewmodel.ComplaintViewModel


class CreateComplaintSantriFragment : Fragment() {
    private var _binding: FragmentCreateComplaintSantriBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ComplaintViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateComplaintSantriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = com.example.petikcare.di.Injection.provideRepository(requireContext())
        val factory = com.example.petikcare.viewmodel.ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ComplaintViewModel::class.java]

        viewModel.message.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSimpanKeluhan.isEnabled = !isLoading

        }

        viewModel.createComplaintSantri.observe(viewLifecycleOwner) {

        }

        binding.btnSimpanKeluhan.setOnClickListener {
            val keluhan = binding.etKeluhan.text.toString()
            val keterangan = binding.etKeterangan.text.toString()

            if (keluhan.isEmpty()) {
                binding.tilKeluhan.error = "Masukkan keluhan"
                return@setOnClickListener
            }
            if (keterangan.isEmpty()) {
                binding.tilKeterangan.error = "Masukkan keterangan"
                return@setOnClickListener
            }

            val request = RequestCreateComplaints(keluhan, keterangan)
            viewModel.createComplaintSantri(request)
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}