package com.ardwind.prototype

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListActivity : AppCompatActivity() {

    private lateinit var recyclerViewList: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var listAdapter: ListAdapter
    private val bookingList = mutableListOf<FormActivity.Booking>()
    private val filteredBookingList = mutableListOf<FormActivity.Booking>()
    private lateinit var searchEditText: EditText
    private lateinit var calendarImageView: ImageView
    private val calendar = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog
    private var selectedDate: Date? = null
    // Menggunakan Locale.ENGLISH untuk format tanggal yang konsisten
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btnbacklist).setOnClickListener { navigateToHomeActivity() }
        findViewById<FloatingActionButton>(R.id.fabAddList).setOnClickListener { navigateToFormActivity() }
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFormActivity() {
        val intent = Intent(this, FormActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)

        // Inisialisasi View
        recyclerViewList = findViewById(R.id.recyclerViewlist)
        searchEditText = findViewById(R.id.searchEditText)
        calendarImageView = findViewById(R.id.calendarImageView)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference.child("bookings")

        // Konfigurasi RecyclerView
        recyclerViewList.layoutManager = LinearLayoutManager(this)
        listAdapter = ListAdapter(filteredBookingList)
        recyclerViewList.adapter = listAdapter

        setupClickListeners()
        loadBookings()
        setupCalendar()
        setupSearchEditText()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadBookings() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newBookingList = mutableListOf<FormActivity.Booking>()
                for (dataSnapshot in snapshot.children) {
                    val booking = dataSnapshot.getValue(FormActivity.Booking::class.java)
                    booking?.let {
                        // Memastikan tanggal yang diambil dari Firebase diformat dengan benar
                        it.tanggalBooking = formatDate(it.tanggalBooking)
                        newBookingList.add(it)
                    }
                }
                bookingList.clear()
                bookingList.addAll(newBookingList)
                filterBookings()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListActivity", "Error loading bookings: ${error.message}")
            }
        })
    }
    // Fungsi untuk memformat tanggal ke "dd MMM yyyy" dengan Locale.ENGLISH
    private fun formatDate(dateString: String?): String? {
        if (dateString == null) return null
        return try {
            val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            dateFormat.format(date)
        } catch (e: Exception) {
            Log.e("ListActivity", "Error parsing date: ${e.message}")
            null
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

    private fun setupSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterBookings()
            }
        })
    }

    private fun filterBookings() {
        val searchText = searchEditText.text.toString().trim()
        val tempFilteredList = mutableListOf<FormActivity.Booking>()

        for (booking in bookingList) {
            val bookingDate: Date? = try {
                dateFormat.parse(booking.tanggalBooking)
            } catch (e: Exception) {
                Log.e("ListActivity", "Error parsing date: ${e.message}")
                null
            }

            if (selectedDate != null) {
                // Jika tanggal dipilih, hanya filter berdasarkan tanggal
                if (bookingDate != null && isSameDay(selectedDate!!, bookingDate)) {
                    tempFilteredList.add(booking)
                }
            } else {
                // Jika tidak ada tanggal yang dipilih, filter berdasarkan pencarian teks
                if (searchText.isEmpty() ||
                    booking.tanggalBooking?.contains(searchText, ignoreCase = true) == true ||
                    booking.userName?.contains(searchText, ignoreCase = true) == true ||
                    booking.jamMulai?.contains(searchText, ignoreCase = true) == true ||
                    booking.jamSelesai?.contains(searchText, ignoreCase = true) == true ||
                    booking.ruangan?.contains(searchText, ignoreCase = true) == true ||
                    booking.picBooking?.contains(searchText, ignoreCase = true) == true ||
                    booking.judulEvent?.contains(searchText, ignoreCase = true) == true ||
                    booking.jumlahPeserta?.toString()?.contains(searchText, ignoreCase = true) == true
                ) {
                    tempFilteredList.add(booking)
                }
            }
        }

        filteredBookingList.clear()
        filteredBookingList.addAll(tempFilteredList)
        listAdapter.notifyDataSetChanged()
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}