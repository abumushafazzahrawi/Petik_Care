package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.data.local.room.ComplaintDatabase
import com.example.petikcare.data.remote.ComplaintRepository
import com.example.petikcare.databinding.FragmentHomeBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ViewModelFactory
import com.example.retrofit.ApiConfig

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var keluhanAdapter: KeluhanAdapter
    private lateinit var viewModel: ComplaintViewModel
    private var isExpanded = false


    companion object {
        const val PREF_NAME = "petikCare"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = sharedPref.getString("USERNAME", null)
        val role = sharedPref.getString("ROLE", "User")
        val repository = Injection.provideRepository(requireContext())
        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ComplaintViewModel::class.java]
        viewModel.refreshComplaints()

        binding.tvWelcomeUser.text = "Welcome ${name ?: "User"}"
        binding.tvRole.text = "Role: $role"

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    // Opsional jika errornya karena token exoired
                    if (message.contains("Sesi habis")) {
                        viewModel.clearError()
                        logOut()
                    } else {
                        // Reset error di ViewModel agar tidak muncul berkali-kali saat rotasi layar
                        viewModel.clearError()
                    }
                }
            }

        Log.d("HOME_DEBUG", "USERNAME: $name")
        Log.d("HOME_DEBUG", "ROLE: $role")

        //Setup RecyclerView
        keluhanAdapter = KeluhanAdapter(
            listKeluhan = emptyList(), onPendingClick = { keluhan ->
                // Tampilkan bottomSheet
                val bottomSheet = KeluhanBottomSheet(keluhan)

                bottomSheet.show(parentFragmentManager, "Respond")
            }, onDoneClick = { keluhan ->
                // 1. Buat bundle untuk membawa ID Keluhan
                val bundle = Bundle().apply {
                    putString("id", keluhan.id)
                    putString("nama", keluhan.namaSantri)
                    putString("keluhan", keluhan.title)
                    putString("status", keluhan.status)
                    putString("date", keluhan.createdAt)
                    putString("description", keluhan.description)
                    putString("handled_at", keluhan.handledAt)
                    putString("catatan", keluhan.handledNote)
                    putString("obat", keluhan.medicineName)
                    putString("quantity", keluhan.medicineQuantity)
                }

                // 2. Gunakan findNavController untuk navigasi ke DetailKeluhanFragment
                findNavController().navigate(R.id.detailKeluhanFragment, bundle)

            })
        binding.rvkeluhan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvkeluhan.adapter = keluhanAdapter

        //Observe Data
        viewModel.complaints.observe(viewLifecycleOwner) { data ->

            if (data.isNullOrEmpty()) {
                keluhanAdapter.updateData(emptyList())
                return@observe
            }

            val sorted = data.sortedByDescending { it.createdAt }

            // Logicnya -> jika data tidak kosong atau ada data terbaru maka tampilkan 3 data
            val finalData = if (isExpanded) {
                sorted
            } else {
                // Kalo data tidak ada yang baru maka tampilkan 3 data lama
                sorted.take(3)
            }

            keluhanAdapter.updateData(finalData)
        }

        binding.tvShowMore.setOnClickListener {
            isExpanded = !isExpanded

            viewModel.complaints.value?.let { data ->
                val sorted = data.sortedByDescending { it.createdAt }

                if (isExpanded) {
                    keluhanAdapter.updateData(sorted)
                    binding.tvShowMore.text = "Show Less"
                } else {
                    keluhanAdapter.updateData(sorted.take(3))
                    binding.tvShowMore.text = "Show More"
                }
            }
        }

        //Observe Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //Get Data
        binding.cvObat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_obatFragment)
        }

        binding.cvRiwayatKeluhan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_riwayatKeluhanFragment)
        }
    }

    fun logOut() {
        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit {
            // 1. Hapus data session
            clear()
        }

        // 2. Pindah ke LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)

        // 3. Clear stack activity agaruser tidak bisa 'Back'ke halaman ini lagi
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        requireActivity()
            .finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}