package com.example.petikcare.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.example.petikcare.R
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.data.remote.Result
import com.example.petikcare.databinding.BottomSheetKeluhanBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.di.ObatInjection
import com.example.petikcare.pengasuhan.response_complaint.MedicinesList
import com.example.petikcare.pengasuhan.response_complaint.RespondRequest
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ObatViewModel
import com.example.petikcare.viewmodel.ObatViewModelFactory
import com.example.petikcare.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KeluhanBottomSheet(private val keluhan: ComplaintEntity) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetKeluhanBinding? = null
    private val binding get() = _binding!!

    private var listObat: List<ObatEntity> = listOf()

    private lateinit var viewModel: ComplaintViewModel
    private lateinit var obatViewModel: ObatViewModel
    val selectedMedicines = mutableListOf<View>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifikasi diizinkan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifikasi tidak diizinkan", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "petik care"
    }

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

        // Request permission notifikasi untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val title = requireContext().resources.getString(R.string.title)
        val message = requireContext().resources.getString(R.string.message)

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
        viewModel.responseComplaint.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { response ->

                // Navigasi langsung ke DetailKeluhanFragment
                val detailData = response.data
                val bundle = Bundle().apply {
                    putString("id", detailData.complaintId)
                    putString("nama", keluhan.namaSantri)
                    putString("keluhan", keluhan.title)
                    putString("status", detailData.status)
                    putString("handled_at", detailData.handledAt)
                    putString("catatan", detailData.treatment.note)
                    putString(
                        "obat",
                        detailData.treatment.medicinesGiven.joinToString(", ") { it.name })
                    putString(
                        "quantity",
                        detailData.treatment.medicinesGiven.joinToString(", ") { it.quantity.toString() })
                }

                createNotification(title, message, bundle)
                Toast.makeText(requireContext(), "Berhasil respond", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.detailKeluhanFragment, bundle)
                dismiss()
                viewModel.clearResponse()
            }
        }

        obatViewModel.listObat.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    listObat = result.data

                    if (listObat.isNotEmpty() && selectedMedicines.isEmpty()) {
                        tambahBarisObat()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Gagal memuat obat", Toast.LENGTH_SHORT).show()
                }
            }
        }


        obatViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTambahObat.setOnClickListener {
            tambahBarisObat()
        }

        binding.btnSimpan.setOnClickListener {
            val note = binding.etCatatan.text.toString()

            if (note.isEmpty()) {
                binding.tilCatatan.error = "Catatan tidak boleh kosong"
                return@setOnClickListener
            }

            if (selectedMedicines.isEmpty()) {
                Toast.makeText(requireContext(), "Harap tambah obat", Toast.LENGTH_SHORT).show()
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

    private fun createNotification(title: String, message: String, bundle: Bundle) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Menggunakan MainActivity sebagai target Intent (Fragment tidak bisa langsung)
        val intent = Intent(requireContext(), MainActivity::class.java)

        val pendingIntent = NavDeepLinkBuilder(requireContext())
            .setGraph(R.navigation.navigation_controller)
            .setDestination(R.id.detailKeluhanFragment)
            .setArguments(bundle)
            .createPendingIntent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_mini_petik_care)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
