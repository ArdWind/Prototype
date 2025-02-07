package com.ardwind.prototype

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReportActivity : AppCompatActivity() {

    data class ReportItem(
        val tanggalBooking: String?,
        val userName: String?,
        val jamMulai: String?,
        val jamSelesai: String?,
        val ruangan: String?,
        val picBooking: String?,
        val judulEvent: String?,
        val jumlahPeserta: Int?
    )

    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewReport: RecyclerView
    private lateinit var reportAdapter: ReportAdapter
    private val reportList = mutableListOf<ReportItem>()
    private lateinit var searchEditText: EditText
    private lateinit var calendarImageView: ImageView
    private val calendar = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var btnExportPdf: Button
    private var selectedDate: Date? = null

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        // Inisialisasi View
        recyclerViewReport = findViewById(R.id.recyclerViewReport)
        searchEditText = findViewById(R.id.searchEditText)
        calendarImageView = findViewById(R.id.calendarImageView)
        btnExportPdf = findViewById(R.id.btnExportPdf)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference.child("bookings")

        // Konfigurasi RecyclerView
        recyclerViewReport.layoutManager = LinearLayoutManager(this)
        reportAdapter = ReportAdapter(reportList)
        recyclerViewReport.adapter = reportAdapter

        // Tambahkan ItemDecoration untuk Header dan Jarak
        recyclerViewReport.addItemDecoration(SpaceItemDecoration(10)) // Jarak 10px

        // Setup Calendar
        setupCalendar()

        // Load Bookings
        loadBookings()

        // Setup Export PDF
        btnExportPdf.setOnClickListener {
            createFile()
        }

        // Setup Click Listeners
        findViewById<ImageView>(R.id.btnbackreport).setOnClickListener { navigateToHomeActivity() }
        setupSearchEditText()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupCalendar() {
        calendarImageView.setOnClickListener {
            showDatePickerDialog()
        }
        searchEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = calendar.time
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                val formattedDate = dateFormat.format(selectedDate)
                searchEditText.setText(formattedDate)
                filterBookings()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun loadBookings() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newReportList = mutableListOf<ReportItem>()
                for (dataSnapshot in snapshot.children) {
                    val booking = dataSnapshot.getValue(FormActivity.Booking::class.java)
                    booking?.let {
                        newReportList.add(
                            ReportItem(
                                it.tanggalBooking,
                                it.userName,
                                it.jamMulai,
                                it.jamSelesai,
                                it.ruangan,
                                it.picBooking,
                                it.judulEvent,
                                it.jumlahPeserta
                            )
                        )
                    }
                }
                reportList.clear()
                reportList.addAll(newReportList)
                filterBookings()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ReportActivity", "Error loading bookings: ${error.message}")
            }
        })
    }

    private fun setupSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterBookings() // Filter setiap kali teks berubah
            }
        })
    }

    private fun filterBookings() {
        val filteredList = mutableListOf<ReportItem>()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val searchText = searchEditText.text.toString().trim()

        for (item in reportList) {
            val bookingDate: Date? = try {
                dateFormat.parse(item.tanggalBooking)
            } catch (e: Exception) {
                Log.e("ReportActivity", "Error parsing date: ${e.message}")
                null
            }

            if (selectedDate != null) {
                // Jika tanggal dipilih, hanya filter berdasarkan tanggal
                if (bookingDate != null && isSameDay(selectedDate!!, bookingDate)) {
                    filteredList.add(item)
                }
            } else {
                // Jika tidak ada tanggal yang dipilih, filter berdasarkan pencarian teks
                if (searchText.isEmpty() ||
                    item.tanggalBooking?.contains(searchText, ignoreCase = true) == true ||
                    item.userName?.contains(searchText, ignoreCase = true) == true ||
                    item.jamMulai?.contains(searchText, ignoreCase = true) == true ||item.jamSelesai?.contains(searchText, ignoreCase = true) == true ||
                    item.ruangan?.contains(searchText, ignoreCase = true) == true ||
                    item.picBooking?.contains(searchText, ignoreCase = true) == true ||
                    item.judulEvent?.contains(searchText, ignoreCase = true) == true ||
                    item.jumlahPeserta?.toString()?.contains(searchText, ignoreCase = true) == true
                ) {
                    filteredList.add(item)
                }
            }
        }
        reportAdapter.updateData(filteredList)
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return fmt.format(date1) == fmt.format(date2)
    }

    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    exportToPdf(uri)
                }
            }
        }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "LaporanBooking.pdf")
        }
        createFileLauncher.launch(intent)
    }

    private fun exportToPdf(uri: Uri) {
        if (reportList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val dataPaint = Paint()

        // Ambil teks dari searchEditText
        val searchText = searchEditText.text.toString().trim()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

        // Filter data berdasarkan tanggal jika searchEditText tidak kosong
        val filteredReportList = mutableListOf<ReportItem>()
        for (item in reportList) {
            val bookingDate: Date? = try {
                dateFormat.parse(item.tanggalBooking)
            } catch (e: Exception) {
                Log.e("ReportActivity", "Error parsing date: ${e.message}")
                null
            }
            if (selectedDate != null) {
                if (bookingDate != null && isSameDay(selectedDate!!, bookingDate)) {
                    filteredReportList.add(item)
                }
            } else {
                if (searchText.isEmpty() ||
                    item.tanggalBooking?.contains(searchText, ignoreCase = true) == true ||
                    item.userName?.contains(searchText, ignoreCase = true) == true ||
                    item.jamMulai?.contains(searchText, ignoreCase = true) == true ||
                    item.jamSelesai?.contains(searchText, ignoreCase = true) == true ||
                    item.ruangan?.contains(searchText, ignoreCase = true) == true ||
                    item.picBooking?.contains(searchText, ignoreCase = true) == true ||
                    item.judulEvent?.contains(searchText, ignoreCase = true) == true ||
                    item.jumlahPeserta?.toString()?.contains(searchText, ignoreCase = true) == true
                ) {
                    filteredReportList.add(item)
                }
            }
        }
        if (filteredReportList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data yang cocok untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }

        // Ukuran Kertas A4 (595 x 842)
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

        // Margin
        val margin = 40

        // Header
        val headerY = 150

        // Data
        var y = 200
        val dataSpacing = 30

        // Judul
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.textSize = 18f
        titlePaint.color = ContextCompat.getColor(this, R.color.black)

        // Header Tabel
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 10f
        paint.color = ContextCompat.getColor(this, R.color.black)

        // Data Tabel
        dataPaint.textAlign = Paint.Align.LEFT
        dataPaint.textSize = 10f
        dataPaint.color = ContextCompat.getColor(this, R.color.black)

        var currentPage = 1
        var currentItemIndex = 0
        var canvas: Canvas

        try {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream == null) {
                Toast.makeText(this, "Gagal membuka file untuk ditulis", Toast.LENGTH_SHORT).show()
                return
            }
            while (currentItemIndex < filteredReportList.size) {
                val page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas

                // Judul
                canvas.drawText("Laporan Booking", pageWidth / 2f, 50f, titlePaint)

                // Header Tabel
                canvas.drawText("Tgl Booking", (margin + 80).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Pembuat", margin.toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Mulai", (margin + 160).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Selesai", (margin + 220).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Ruangan", (margin + 280).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("PIC", (margin + 340).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Event", (margin + 400).toFloat(), headerY.toFloat(), paint)
                canvas.drawText("Peserta", (margin + 480).toFloat(), headerY.toFloat(), paint)

                y = headerY + 30

                while (currentItemIndex < filteredReportList.size) {
                    val item = filteredReportList[currentItemIndex]

                    // Data
                    canvas.drawText(item.tanggalBooking ?: "", (margin + 80).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.userName ?: "", margin.toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.jamMulai ?: "", (margin + 160).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.jamSelesai ?: "", (margin + 220).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.ruangan ?: "", (margin + 280).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.picBooking ?: "", (margin + 340).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.judulEvent ?: "", (margin + 400).toFloat(), y.toFloat(), dataPaint)
                    canvas.drawText(item.jumlahPeserta?.toString() ?: "", (margin + 480).toFloat(), y.toFloat(), dataPaint)

                    y += dataSpacing
                    currentItemIndex++

                    // Cek apakah halaman penuh
                    if (y >= pageHeight - margin) {
                        pdfDocument.finishPage(page)
                        currentPage++
                        val newPage = pdfDocument.startPage(pageInfo)
                        canvas = newPage.canvas
                        // Judul
                        canvas.drawText("Laporan Booking", pageWidth / 2f, 50f, titlePaint)

                        // Header Tabel
                        canvas.drawText("Tgl Booking", (margin + 80).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Pembuat", margin.toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Mulai", (margin + 160).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Selesai", (margin + 220).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Ruangan", (margin + 280).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("PIC", (margin + 340).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Event", (margin + 400).toFloat(), headerY.toFloat(), paint)
                        canvas.drawText("Peserta", (margin + 480).toFloat(),headerY.toFloat(), paint)
                        y =headerY + 30
                        break
                    }
                }

                pdfDocument.finishPage(page)
                currentPage++
            }
            pdfDocument.writeTo(outputStream)
            Toast.makeText(this, "PDF berhasil disimpan", Toast.LENGTH_LONG).show()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    // ItemDecoration untuk Jarak
    inner class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = space
        }
    }
}