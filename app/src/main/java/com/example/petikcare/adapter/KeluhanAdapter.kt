package com.example.petikcare.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.petikcare.data.local.entity.ComplaintEntity
import com.example.petikcare.databinding.MenuKeluhanBinding
import com.example.petikcare.utils.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class KeluhanAdapter(
    var listKeluhan: List<ComplaintEntity>,
    private val role: String,
    private val onPendingClick: (ComplaintEntity) -> Unit,
    private val onDoneClick: (ComplaintEntity) -> Unit,
    private val onDeleteClick: (ComplaintEntity) -> Unit
): RecyclerView.Adapter<KeluhanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MenuKeluhanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = MenuKeluhanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keluhan = listKeluhan[position]
        holder.binding.tvNama.text = "\uD83D\uDC64 Nama: ${keluhan.namaSantri}"
        holder.binding.tvKeluhan.text = "\uD83D\uDCCC Keluhan: ${keluhan.title}"
        holder.binding.tvDate.text =
            "\uD83D\uDD52 Dibuat: ${DateFormat.formatTanggal(keluhan.createdAt)}"
        holder.binding.tvDeskripsi.text = "\uD83D\uDCDD Deskripsi: ${keluhan.description}"
        holder.binding.tvStatus.text = "Status: ${keluhan.status}"
        val status = keluhan.status
        when (status) {
            "PENDING" ->
                holder.binding.tvStatus.setTextColor(Color.RED)

            "SELESAI" ->
                holder.binding.tvStatus.setTextColor("#1C594F".toColorInt())

            else ->
                holder.binding.tvStatus.setTextColor(Color.BLACK)
        }

        holder.binding.btnDetail.setOnClickListener {
            val isPengasuhan = role.equals("pengasuhan", ignoreCase = true)
            val isPending = keluhan.status.equals("PENDING", ignoreCase = true)

            if (isPengasuhan && isPending) {
                onPendingClick(keluhan)
            } else {
                onDoneClick(keluhan)
            }
        }


        val isSantri = role.equals("santri", ignoreCase = true)
        val isPending = keluhan.status.equals("PENDING", ignoreCase = true)

        if (isSantri && isPending) {
            holder.binding.btnDelete.visibility = View.VISIBLE
            holder.binding.btnDetail.visibility = View.GONE
        } else {
            holder.binding.btnDelete.visibility = View.GONE
            holder.binding.btnDetail.visibility = View.VISIBLE
        }
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(keluhan)
        }
        true
    }

    override fun getItemCount(): Int = listKeluhan.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<ComplaintEntity>) {
        listKeluhan = newData
        notifyDataSetChanged()
    }
}