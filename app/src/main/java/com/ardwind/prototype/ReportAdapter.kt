package com.ardwind.prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private var reportList: List<ReportActivity.ReportItem>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggalBookingTextView: TextView = itemView.findViewById(R.id.tanggalBookingTextView)
        val pembuatTextView: TextView = itemView.findViewById(R.id.pembuatTextView)
        val jamMulaiTextView: TextView = itemView.findViewById(R.id.jamMulaiTextView)
        val jamSelesaiTextView: TextView = itemView.findViewById(R.id.jamSelesaiTextView)
        val ruanganTextView: TextView = itemView.findViewById(R.id.ruanganTextView)
        val picBookingTextView: TextView = itemView.findViewById(R.id.picBookingTextView)
        val judulEventTextView: TextView = itemView.findViewById(R.id.judulEventTextView)
        val jumlahPesertaTextView: TextView = itemView.findViewById(R.id.jumlahPesertaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val currentItem = reportList[position]
        holder.tanggalBookingTextView.text = currentItem.tanggalBooking ?: ""
        holder.pembuatTextView.text = currentItem.userName ?: ""
        holder.jamMulaiTextView.text = currentItem.jamMulai ?: ""
        holder.jamSelesaiTextView.text = currentItem.jamSelesai ?: ""
        holder.ruanganTextView.text = currentItem.ruangan ?: ""
        holder.picBookingTextView.text = currentItem.picBooking ?: ""
        holder.judulEventTextView.text = currentItem.judulEvent ?: ""
        holder.jumlahPesertaTextView.text = currentItem.jumlahPeserta?.toString() ?: ""
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    fun updateData(newReportList: List<ReportActivity.ReportItem>) {
        reportList = newReportList
        notifyDataSetChanged()
    }

    val currentList: List<ReportActivity.ReportItem>
        get() = reportList
}