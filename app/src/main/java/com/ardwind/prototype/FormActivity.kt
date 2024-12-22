package com.ardwind.prototype

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Intent

class FormActivity : AppCompatActivity() {

    data class Booking(
        var tanggalBooking: String? = null,
        var jamMulai: String? = null,
        var jamSelesai: String? = null,
        var ruangan: String? = null,
        var picBooking: String? = null,
        var judulEvent: String? = null,
        var jumlahPeserta: Int? = null,
        var timestamp: Any? = ServerValue.TIMESTAMP, // Gunakan ServerValue.TIMESTAMP
        var userName: String? = null // Atau userId: String? = null
    )

    private lateinit var editTglBooking: EditText
    private lateinit var editJamMulai: EditText
    private lateinit var editJamSelesai: EditText
    private lateinit var spinnerRuangan: Spinner
    private lateinit var editPicBooking: EditText
    private lateinit var editJudulEvent: EditText
    private lateinit var editJumlahPeserta: EditText
    private lateinit var formbtn1: Button
    private lateinit var formbtn2: Button

    // Firebase
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi View
        editTglBooking = findViewById(R.id.editTglBooking)
        editJamMulai = findViewById(R.id.editJamMulai)
        editJamSelesai = findViewById(R.id.editJamSelesai)
        spinnerRuangan = findViewById(R.id.spinnerRuangan)
        editPicBooking = findViewById(R.id.editPicBooking)
        editJudulEvent = findViewById(R.id.editJudulEvent)
        editJumlahPeserta = findViewById(R.id.editJumlahPeserta)
        formbtn1 = findViewById(R.id.formbtn1)
        formbtn2 = findViewById(R.id.formbtn2)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference.child("bookings")
        auth = FirebaseAuth.getInstance()

        // Set Listener untuk tombol 2 (calcel)
        formbtn2.setOnClickListener {
            clearForm()
            navigateUpTo(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Set Listener untuk Tanggal Booking
        editTglBooking.setOnClickListener {
            showDatePickerDialog()
        }

        // Set Listener untuk Jam Mulai
        editJamMulai.setOnClickListener {
            showTimePickerDialog(editJamMulai)
        }

        // Set Listener untuk Jam Selesai
        editJamSelesai.setOnClickListener {
            showTimePickerDialog(editJamSelesai)
        }

        // Set Listener untuk Button Submit
        formbtn1.setOnClickListener {
            saveDataToFirebase()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                editTglBooking.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                editText.setText(timeFormat.format(selectedTime.time))
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun saveDataToFirebase() {
        // Ambil data dari form
        val tanggalBooking = editTglBooking.text.toString()
        val jamMulai = editJamMulai.text.toString()
        val jamSelesai = editJamSelesai.text.toString()
        val ruangan = spinnerRuangan.selectedItem.toString()
        val picBooking = editPicBooking.text.toString()
        val judulEvent = editJudulEvent.text.toString()
        val jumlahPeserta = editJumlahPeserta.text.toString().toIntOrNull() ?: 0

        // Validasi data (pastikan tidak kosong)
        if (tanggalBooking.isEmpty() || jamMulai.isEmpty() || jamSelesai.isEmpty() ||
            ruangan.isEmpty() || picBooking.isEmpty() || judulEvent.isEmpty() || jumlahPeserta == 0
        ) {
            Toast.makeText(this, "Harap isi semua data dengan benar!", Toast.LENGTH_SHORT).show()
            return
        }

        if (auth.currentUser == null) {
            Toast.makeText(this, "Anda belum login!", Toast.LENGTH_SHORT).show()
            return
        }
        // Dapatkan ID pengguna yang sedang login
        val currentUserName = auth.currentUser?.displayName
        // Atau, jika Anda menyimpan nama pengguna:
        // val currentUserId = auth.currentUser?.uid

        // Buat objek Booking
        val booking = Booking(
            tanggalBooking,
            jamMulai,
            jamSelesai,
            ruangan,
            picBooking,
            judulEvent,
            jumlahPeserta,
            userName = currentUserName // Atau userId = currentUserId
        )

        // Simpan ke Firebase
        val bookingId = database.push().key // Generate ID unik
        Log.d("FormActivity", "bookingId: $bookingId") // Tambahkan log ini
        if (bookingId != null) {
            database.child(bookingId).setValue(booking)
                .addOnSuccessListener {
                    Log.d("FormActivity", "Data berhasil disimpan ke Firebase")
                    Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    clearForm()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e("FormActivity", "Gagal menyimpan data ke Firebase", exception)
                    Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FormActivity", "Proses penyimpanan selesai dengan sukses")
                    } else {
                        Log.e("FormActivity", "Proses penyimpanan gagal", task.exception)
                    }
                }
        } else {
            Log.e("FormActivity", "bookingId is null!")
        }
    }

    private fun clearForm() {
        editTglBooking.text.clear()
        editJamMulai.text.clear()
        editJamSelesai.text.clear()
        spinnerRuangan.setSelection(0) // Reset ke item pertama
        editPicBooking.text.clear()
        editJudulEvent.text.clear()
        editJumlahPeserta.text.clear()
    }
}