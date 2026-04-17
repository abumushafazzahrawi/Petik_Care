package com.example.petikcare.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petikcare.R
import com.example.petikcare.data.local.entity.ObatEntity
import com.example.petikcare.databinding.MenuObatBinding
import com.example.petikcare.response_obat.DataGetObat
import com.example.petikcare.ui.RestockObatFragment
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.navigation.findNavController

class ObatAdapter(var listObat: List<ObatEntity>) : RecyclerView.Adapter<ObatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MenuObatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MenuObatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ObatAdapter.ViewHolder, position: Int) {
        val Obat = listObat[position]
        holder.binding.tvNamaObat.text = "\uD83D\uDC8A Nama Obat: ${Obat.name}"
        holder.binding.tvDeskripsi.text = "\uD83D\uDCDD Deskripsi: ${Obat.description}"
        holder.binding.tvStok.text = "\uD83D\uDCE6 Stok: ${Obat.stock}"
        holder.binding.tvDibuat.text = "\uD83D\uDD52 Dibuat: ${formatTanggal(Obat.createdAt)}"
        holder.binding.tvUpdate.text = "\uD83D\uDD52 Diupdate: ${formatTanggal(Obat.updatedAt)}"

        val stok = Obat.stock
        if (stok < 5) {
            holder.binding.tvStok.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_light))
        } else if (stok < 10) {
            holder.binding.tvStok.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_light))
        } else {
            holder.binding.tvStok.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_light))
        }
        holder.binding.btnRestockObat.setOnClickListener {
            val bundle = Bundle().apply {
                putString("id", Obat.id)
                putString("nama", Obat.name)
                putInt("stok", Obat.stock)
            }

            val navController =
                holder.itemView.findNavController()
            navController.navigate(R.id.action_obatFragment_to_restockObatFragment, bundle)
        }
        holder.binding.btnEditObat.setOnClickListener {
            val bundle = Bundle().apply {
                putString("id", Obat.id)
                putString("nama", Obat.name)
                putString("sediaan", Obat.description)
            }

            val navController =
                holder.itemView.findNavController()
            navController.navigate(R.id.action_obatFragment_to_editObatFragment, bundle)

        }
    }

    override fun getItemCount(): Int = listObat.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<ObatEntity>) {
        listObat = newData
        notifyDataSetChanged()
    }
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