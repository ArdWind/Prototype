package com.ardwind.prototype // Pastikan package ini sama dengan BookActivity.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(private val bookingList: List<FormActivity.Booking>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggalBooking: TextView = itemView.findViewById(R.id.textTanggalBooking)
        val jamMulai: TextView = itemView.findViewById(R.id.textJamMulai)
        val jamSelesai: TextView = itemView.findViewById(R.id.textJamSelesai)
        val ruangan: TextView = itemView.findViewById(R.id.textRuangan)
        val picBooking: TextView = itemView.findViewById(R.id.textPicBooking)
        val judulEvent: TextView = itemView.findViewById(R.id.textJudulEvent)
        val jumlahPeserta: TextView = itemView.findViewById(R.id.textJumlahPeserta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBooking = bookingList[position]
        holder.tanggalBooking.text = currentBooking.tanggalBooking
        holder.jamMulai.text = currentBooking.jamMulai
        holder.jamSelesai.text = currentBooking.jamSelesai
        holder.ruangan.text = currentBooking.ruangan
        holder.picBooking.text = currentBooking.picBooking
        holder.judulEvent.text = currentBooking.judulEvent
        holder.jumlahPeserta.text = currentBooking.jumlahPeserta.toString()
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}