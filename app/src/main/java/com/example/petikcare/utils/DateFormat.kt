package com.example.petikcare.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormat {

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