package com.ardwind.prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookActivity : AppCompatActivity() {

    private lateinit var recyclerViewBook: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var bookAdapter: BookAdapter
    private val bookingList = mutableListOf<FormActivity.Booking>()

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btnbackbook).setOnClickListener { navigateToHomeActivity() }
        findViewById<FloatingActionButton>(R.id.fabAddBook).setOnClickListener { navigateToFormActivity() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book)

        // Inisialisasi View
        recyclerViewBook = findViewById(R.id.recyclerViewBook)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference.child("bookings")
        auth = FirebaseAuth.getInstance()

        // Konfigurasi RecyclerView
        recyclerViewBook.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(bookingList)
        recyclerViewBook.adapter = bookAdapter

        setupClickListeners()
        loadBookings()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadBookings() {
        // Dapatkan nama pengguna yang sedang login
        val currentUserName = auth.currentUser?.displayName

        if (currentUserName != null) {
            // Query Firebase untuk data yang sesuai dengan nama pengguna
            database.orderByChild("userName").equalTo(currentUserName)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        bookingList.clear()
                        for (dataSnapshot in snapshot.children) {
                            val booking = dataSnapshot.getValue(FormActivity.Booking::class.java)
                            booking?.let {
                                bookingList.add(it)
                            }
                        }
                        bookAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("BookActivity", "Error loading bookings: ${error.message}")
                    }
                })
        } else {
            Log.w("BookActivity", "Current user name is null.")
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun navigateToFormActivity() {
        startActivity(Intent(this, FormActivity::class.java))
    }
}