package com.example.petikcare.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import com.example.petikcare.R
import com.example.petikcare.databinding.FragmentProfilBinding
import androidx.core.view.isGone

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PREF_NAME = "petikCare"
    }


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
        val nama = sharedPref.getString("USERNAME", null)
        val role = sharedPref.getString("ROLE", "User")

        binding.tvNama.text = nama
        binding.tvRole.text = role

        binding.btnLogout.setOnClickListener {
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

        binding.cvCardProfil.setOnClickListener {
            binding.cvEditFoto.animate().apply {
                duration = 200
                if (binding.cvEditFoto.isGone) {
                    binding.cvEditFoto.visibility = View.VISIBLE
                    alpha(1f)
                } else {
                    alpha(0f).withEndAction {
                        binding.cvEditFoto.visibility = View.GONE
                    }
                }
            }
        }
    }
}