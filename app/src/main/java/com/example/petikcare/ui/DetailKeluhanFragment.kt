package com.example.petikcare.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.petikcare.adapter.KeluhanAdapter
import com.example.petikcare.databinding.FragmentDetailKeluhanBinding
import com.example.petikcare.di.Injection
import com.example.petikcare.utils.DateFormat
import com.example.petikcare.viewmodel.ComplaintViewModel
import com.example.petikcare.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale


class DetailKeluhanFragment : Fragment() {
    private var _binding: FragmentDetailKeluhanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: KeluhanAdapter
    private lateinit var viewModel: ComplaintViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailKeluhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("petikCare", Context.MODE_PRIVATE)
        val role = sharedPref.getString("ROLE" ,"santri")?: "santri"
        val isPengasuhan = role.equals("pengasuhan", ignoreCase = true)

        val repository = Injection.provideRepository(requireContext())
        viewModel =
            ViewModelProvider(this, ViewModelFactory(repository))[ComplaintViewModel::class.java]

        val id = arguments?.getString("id")
        val nama = arguments?.getString("nama")
        val keluhan = arguments?.getString("keluhan")
        val status = arguments?.getString("status")
        val obat = arguments?.getString("obat")
        val quantity = arguments?.getString("quantity")
        val ditangani = arguments?.getString("handled_at")
        val catatan = arguments?.getString("catatan")

        val isSelesai = status.equals("SELESAI", ignoreCase = true)

        if (role.equals("santri", ignoreCase = true)) {
            binding.btnBatalPenanganan.visibility = View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { message ->
                if (message == "Berhasil membatalkan") {
                    Toast.makeText(requireContext(), "Penanganan dibatalkan", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }



        if (ditangani != null) {
            binding.tvDitangani.visibility = View.VISIBLE
            binding.tvDitangani.text = "\uD83D\uDD52 Ditangani: ${DateFormat.formatTanggal(ditangani)}"
        }

        binding.tvNama.text = "\uD83D\uDC64 Nama: $nama"
        binding.tvKeluhan.text = "\uD83D\uDCCC keluhan: $keluhan"
        binding.tvStatus.text = "Status: $status"
        binding.tvNamaObat.text = "\uD83D\uDC8A Nama Obat: \n${obat ?: "-"}"
        binding.tvQuantity.text = "Quantity: \n${quantity ?: "-"}"
        binding.tvCatatan.text = "\uD83D\uDCDD Catatan: ${catatan ?: "-"}"

        binding.ivArrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBatalPenanganan.setOnClickListener {
            showRevertDialog(id)
        }

        binding.btnBatalPenanganan.visibility = if (isPengasuhan && isSelesai) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRevertDialog(complaintId: String?) {
        if (complaintId == null) return

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Batalkan Penanganan")
            setMessage("Apakah Anda yakin ingin membatalkan penanganan keluhan ini?")
            setPositiveButton("Ya") { dialog, _ ->
                viewModel.revertComplaint(complaintId)
                dialog.dismiss()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }
}
