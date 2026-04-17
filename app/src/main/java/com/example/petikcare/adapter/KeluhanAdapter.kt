package com.example.petikcare.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.databinding.MenuKeluhanBinding
import com.example.petikcare.response_complaint.DataComplaints
import java.text.SimpleDateFormat
import java.util.Locale

class KeluhanAdapter(
    var listKeluhan: List<ComplaintEntity>,
    private val role: String,
    private val onPendingClick: (ComplaintEntity) -> Unit,
    private val onDoneClick: (ComplaintEntity) -> Unit
): RecyclerView.Adapter<KeluhanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MenuKeluhanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MenuKeluhanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keluhan = listKeluhan[position]
        holder.binding.tvNama.text = "\uD83D\uDC64 Nama: ${keluhan.namaSantri}"
        holder.binding.tvKeluhan.text = "\uD83D\uDCCC Keluhan: ${keluhan.title}"
        holder.binding.tvDate.text = "\uD83D\uDD52 Dibuat: ${formatTanggal(keluhan.createdAt)}"
        holder.binding.tvDeskripsi.text = "\uD83D\uDCDD Deskripsi: ${keluhan.description}"
        holder.binding.tvStatus.text = "Status: ${keluhan.status}"
        val status = keluhan.status
        when(status) {
            "PENDING" ->
                holder.binding.tvStatus.setTextColor(Color.RED)
            "SELESAI" ->
                holder.binding.tvStatus.setTextColor(Color.GREEN)
        }

        holder.binding.btnDetail.setOnClickListener {
            if (role == "pengasuhan") {

                if (keluhan.status == "PENDING") {
                    onPendingClick(keluhan)
                } else {
                    onDoneClick(keluhan)
            }
            } else {
                Toast.makeText(holder.itemView.context, "Akses ditolak: hanya pengasuhan yang bisa melihat detail keluhan",
                    Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding.btnDetail.visibility = if (role == "pengasuhan") View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = listKeluhan.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<ComplaintEntity>) {
        listKeluhan = newData
        notifyDataSetChanged()
    }

    fun formatTanggal(dateString: String): String {
        return try {

            //Intinya kita ngasih tau ke Android cara membaca data dari API
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            // API ngasih "2026-03-16T05:39:34.000Z"
            //Kita bilang ke android “Kalau nanti ada tanggal, bentuknya kayak gini ya—tolong ngertiin
            input.timeZone = java.util.TimeZone.getTimeZone("UTC")
            // parse -> ubah tipe data dari string ke date -> Ini loh stringnya, tolong ubah jadi Date beneran”
            val date = input.parse(dateString)

            // Format ulang
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

            if (date != null) {
                outputFormat.format(date)
            } else {
                "-"
            }
        } catch (e: Exception) {
            dateString
        }
        //Sekarang tampilin dengan gaya yang manusia gunakan sehari-hari
    }
}