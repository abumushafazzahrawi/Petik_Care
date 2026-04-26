package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.decode.SvgDecoder
import coil.load
import com.example.petikcare.R
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.databinding.FragmentHomeBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ViewModelFactory

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val name = sharedPref.getString("USERNAME", null)
        val role = sharedPref.getString("ROLE", "santri") ?: "santri"
        val username  = sharedPref.getString("USERNAME", "User") ?: "user"
        val avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=$username"

        val searchAdapter = KeluhanAdapter(
            listKeluhan = emptyList(),
            role = role,
            onPendingClick = { keluhan ->
                KeluhanBottomSheet(keluhan).show(parentFragmentManager, "Respond")
            },
            onDoneClick = { keluhan ->
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

            },
            onDeleteClick = { keluhan ->
                showDeleteDialog(keluhan.id)
            }
        )

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = searchAdapter

        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val query = s.toString().lowercase()

                val allData = viewModel.complaints.value ?: emptyList()

                val filteredList = allData.filter {
                    it.namaSantri.lowercase().contains(query) ||
                            it.title.lowercase().contains(query)
                }

                searchAdapter.updateData(filteredList)
            }

        })

        if (role == "santri") {
            binding.searchBar.visibility = View.GONE
        } else {
            binding.searchBar.visibility = View.VISIBLE
        }

        val repository = Injection.provideRepository(requireContext())
        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ComplaintViewModel::class.java]
        viewModel.refreshComplaints()

        binding.tvWelcomeUser.text = "Welcome ${name ?: "User"} \uD83D\uDC4B"
        binding.tvRole.text = "Role: $role"

        if (role.equals("pengasuhan", ignoreCase = true)) {
            binding.tvPengasuhan.visibility = View.VISIBLE
            binding.tvSantri.visibility = View.GONE
            binding.cvRiwayatKeluhan.visibility = View.VISIBLE
            binding.cvObat.visibility = View.VISIBLE
            binding.cvBuatKeluhan.visibility = View.GONE
            binding.cvLihatKeluhanSaya.visibility = View.GONE
        } else {
            binding.tvSantri.visibility = View.VISIBLE
            binding.tvPengasuhan.visibility = View.GONE
            binding.cvBuatKeluhan.visibility = View.VISIBLE
            binding.cvLihatKeluhanSaya.visibility = View.VISIBLE
            binding.cvRiwayatKeluhan.visibility = View.GONE
            binding.cvObat.visibility = View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Opsional jika errornya karena token exoired
                if (message.contains("Sesi habis", ignoreCase = true)) {
                    logOut()
                } else {
                    // Reset error di ViewModel agar tidak muncul berkali-kali saat rotasi layar
                    viewModel.clearError()
                }
            }
        }

        binding.ivProfil.load(avatarUrl) {
            decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
            placeholder(R.drawable.ic_profile) // Gambar sementara
            error(R.drawable.ic_profile) // Gambar jika error
        }

        binding.cvLihatKeluhanSaya.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_getMyComplaintFragment)
        }



        Log.d("HOME_DEBUG", "USERNAME: $name")
        Log.d("HOME_DEBUG", "ROLE: $role")

        //Setup RecyclerView
        keluhanAdapter = KeluhanAdapter(
            listKeluhan = emptyList(),
            role = role,
            onPendingClick = { keluhan ->
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

            }, onDeleteClick = { keluhan ->
                showDeleteDialog(keluhan.id)
            }
        )
        binding.rvkeluhan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvkeluhan.adapter = keluhanAdapter

        //Observe Data
        viewModel.complaints.observe(viewLifecycleOwner) { data ->
            if (data.isNullOrEmpty()) {
                keluhanAdapter.updateData(emptyList())
                binding.tvShowMore.visibility = View.GONE
                return@observe
            }

            val sorted = data.sortedByDescending { it.createdAt ?: "" }

            // Logicnya -> jika data tidak kosong atau ada data terbaru maka tampilkan 3 data
            val finalData = if (isExpanded) sorted else sorted.take(3)
            keluhanAdapter.updateData(finalData)

            binding.tvShowMore.visibility = if (data.size > 3) View.VISIBLE else View.GONE
            binding.tvShowMore.text = if (isExpanded) "Show Less" else "Show More"

            binding.rvkeluhan.post {
                binding.rvkeluhan.requestLayout()
            }
        }

        binding.tvShowMore.setOnClickListener {
            isExpanded = !isExpanded
            // Trigger observe ulang untuk memperbarui tampilan
            viewModel.complaints.value?.let { data ->
                val sorted = data.sortedByDescending { it.createdAt?: "" }
                val finalData = if (isExpanded) sorted else sorted.take(3)

                keluhanAdapter.updateData(finalData)
                binding.tvShowMore.text = if (isExpanded) "Show Less" else "Show More"

                binding.rvkeluhan.post {
                    binding.rvkeluhan.requestLayout()
                }
            }
        }

        //Observe Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        //Get Data
        binding.cvObat.setOnClickListener {
            if (role.equals("pengasuhan", ignoreCase = true)) {
                findNavController().navigate(R.id.action_homeFragment_to_obatFragment)
            } else {
                showAccessDeniedSnackbar("Akses ditolak: menu ini hanya bisa diakses oleh pengasuhan")
            }
        }

        binding.cvRiwayatKeluhan.setOnClickListener {
            if (role.equals("pengasuhan", ignoreCase = true)) {
                findNavController().navigate(R.id.action_homeFragment_to_riwayatKeluhanFragment)
            } else {
                showAccessDeniedSnackbar("Akses ditolak: menu ini hanya bisa diakses oleh pengasuhan")
            }
        }

        binding.cvBuatKeluhan.setOnClickListener {
            if (role.equals("santri", ignoreCase = true)) {
                findNavController().navigate(R.id.action_homeFragment_to_createComplaintSantriFragment)
            } else {
                showAccessDeniedSnackbar("Akses ditolak: menu ini hanya bisa diakses oleh santri")
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAccessDeniedSnackbar(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun logOut() {
        val sharedPref = requireContext().getSharedPreferences(ProfilFragment.PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit {
            clear()
        }
        val intent = Intent(requireContext(), LoginActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        requireActivity()
            .finish()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}