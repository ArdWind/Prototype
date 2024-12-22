package com.ardwind.prototype

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
                        newBookingList.add(it)
                    }
                }
                bookingList.clear()
                bookingList.addAll(newBookingList)
                filterBookings(null, searchEditText.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListActivity", "Error loading bookings: ${error.message}")
            }
        })
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
                val selectedDate = calendar.time
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate)
                searchEditText.setText(formattedDate)
                filterBookings(selectedDate, null)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun filterBookings(date: Date?, dateString: String?) {
        filteredBookingList.clear()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        if (date != null) {
            for (booking in bookingList) {
                val bookingDate: Date? = dateFormat.parse(booking.tanggalBooking)
                if (bookingDate != null && isSameDay(date, bookingDate)) {
                    filteredBookingList.add(booking)
                }
            }
        } else if (!dateString.isNullOrEmpty()) {
            try {
                val inputDate: Date? = dateFormat.parse(dateString)
                if (inputDate != null) {
                    for (booking in bookingList) {
                        val bookingDate: Date? = dateFormat.parse(booking.tanggalBooking)
                        if (bookingDate != null && isSameDay(inputDate, bookingDate)) {
                            filteredBookingList.add(booking)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ListActivity", "Error parsing date: ${e.message}")
            }
        } else {
            filteredBookingList.addAll(bookingList)
        }
        listAdapter.notifyDataSetChanged()
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return fmt.format(date1) == fmt.format(date2)
    }
}