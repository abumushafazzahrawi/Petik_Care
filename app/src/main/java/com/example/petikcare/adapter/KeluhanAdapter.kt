package com.example.petikcare.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petikcare.databinding.MenuKeluhanBinding
import com.example.petikcare.response_complaint.DataComplaints
import java.text.SimpleDateFormat
import java.util.Locale

class KeluhanAdapter(
    var listKeluhan: List<DataComplaints>,
    private val onPendingClick: (DataComplaints) -> Unit,
    private val onDoneClick: (DataComplaints) -> Unit
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
        holder.binding.tvNama.text = "\uD83D\uDC64 Nama: ${keluhan.santri.name}"
        holder.binding.tvKeluhan.text = "\uD83D\uDCCC Keluhan: ${keluhan.title}"
        holder.binding.tvDate.text = "\uD83D\uDD52 Dibuat: ${formatTanggal(keluhan.createdAt)}"
        holder.binding.tvStatus.text = "Status: ${keluhan.status}"
        val status = keluhan.status
        when(status) {
            "PENDING" ->
                holder.binding.tvStatus.setTextColor(Color.RED)
            "SELESAI" ->
                holder.binding.tvStatus.setTextColor(Color.GREEN)
        }

        holder.binding.btnDetail.setOnClickListener {
            if (keluhan.status == "PENDING") {
                onPendingClick(keluhan)
            } else {
                onDoneClick(keluhan)
            }
        }

    }

    override fun getItemCount(): Int = listKeluhan.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<DataComplaints>) {
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