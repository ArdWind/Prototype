package com.ardwind.prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private val bookingList: List<FormActivity.Booking>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggalBooking: TextView = itemView.findViewById(R.id.textTanggalBooking)
        val jamMulai: TextView = itemView.findViewById(R.id.textJamMulai)
        val jamSelesai: TextView = itemView.findViewById(R.id.textJamSelesai)
        val ruangan: TextView = itemView.findViewById(R.id.textRuangan)
        val picBooking: TextView = itemView.findViewById(R.id.textPicBooking)
        val judulEvent: TextView = itemView.findViewById(R.id.textJudulEvent)
        val jumlahPeserta: TextView = itemView.findViewById(R.id.textJumlahPeserta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
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