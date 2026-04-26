package com.example.petikcare.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.petikcare.databinding.FragmentProfilBinding
import androidx.core.view.isGone
import android.Manifest
import androidx.appcompat.app.AlertDialog
import coil.decode.SvgDecoder
import coil.load
import com.example.petikcare.R

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PREF_NAME = "petikCare"
    }

//    private val galleryLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            uri?.let {
//                binding.ivProfil.setImageURI(it)
//            }
//        }
//
//    // 2. Minta izin ke user (RUNTIME PERMISSION)
//    private val requestCameraPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                openCamera()
//            } else {
//                Toast.makeText(requireContext(), "Izin Kamera ditolak", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    // 4. Fungsi buka kamera
//    private val cameraLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as Bitmap
//                // Tampilkan imagevIew
//                binding.ivProfil.setImageBitmap(imageBitmap)
//            }
//        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString("USERNAME", "user") ?: "user"
        val avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=$username"
        val nama = sharedPref.getString("USERNAME", null)
        val role = sharedPref.getString("ROLE", "User")

        binding.tvNama.text = nama
        binding.tvRole.text = role

        binding.btnLogout.setOnClickListener {
            createDialogLogOut()
        }

//        binding.cvCardProfil.setOnClickListener {
//            binding.cvEditFoto.animate().apply {
//                duration = 200
//                if (binding.cvEditFoto.isGone) {
//                    binding.cvEditFoto.visibility = View.VISIBLE
//                    alpha(1f)
//                } else {
//                    alpha(0f).withEndAction {
//                        binding.cvEditFoto.visibility = View.GONE
//                    }
//                }
//            }
//        }

        binding.ivProfil.load(avatarUrl) {
            decoderFactory { result, options, _ -> SvgDecoder(result.source, options) }
            placeholder(R.drawable.ic_profile) // Gambar sementara
            error(R.drawable.ic_profile) // Gambar jika error
        }
    }

    private fun createDialogLogOut() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Tutup Aplikasi")
            setMessage("Apakah anda yakin ingin keluar dari aplikasi ini?")
            setPositiveButton("Ya") { _, _, ->
                logOut()
            }
            setNegativeButton("Tidak", null)
            show()
        }
    }

    private fun logOut() {
        val sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit {

            // 1. Hapus data session
            clear()
        }

        // 2. Pindah ke LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)

        // 3. Clear stack activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        requireActivity()
            .finish()
    }
}


//        binding.cvEditFoto.setOnClickListener {
//            showImagePickerDialog()
//        }
//    }

//    private fun showImagePickerDialog() {
//        val options = arrayOf("Kamera", "Galeri")
//
//        android.app.AlertDialog.Builder(requireContext())
//            .setTitle("Pilih Foto")
//            .setItems(options) { _, which ->
//
//                when (which) {
//                    0 -> checkCameraPermissionAndOpen()
//                    1 -> openGallery()
//                }
//            }
//            .show()
//    }


//    private fun openGallery() {
//        galleryLauncher.launch("image/*")
//    }
//
//    // 3. Cek sebelum buka kamera
//    fun checkCameraPermissionAndOpen() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            openCamera()
//        } else {
//            requestCameraPermission.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//
//    private fun openCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        cameraLauncher.launch(intent)


