package com.ardwind.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FeedBackActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputMessage: EditText
    private lateinit var btnSubmit: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed_back)

        // Inisialisasi View
        inputName = findViewById(R.id.input_name)
        inputEmail = findViewById(R.id.input_email)
        inputMessage = findViewById(R.id.input_message)
        btnSubmit = findViewById(R.id.btn_submit)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference.child("FeedBack")
        auth = FirebaseAuth.getInstance()

        // Mengisi input_name dan input_email secara otomatis
        val currentUser = auth.currentUser
        if (currentUser != null) {
            inputName.setText(currentUser.displayName)
            inputEmail.setText(currentUser.email)
            inputName.isEnabled = false
            inputEmail.isEnabled = false
        }

        // Menangani klik tombol btn_submit
        btnSubmit.setOnClickListener {
            submitFeedback()
        }

        // Menangani klik tombol kembali
        val homeRules = findViewById<ImageView>(R.id.btnbackfeed)
        homeRules.setOnClickListener {
            navigateToHomeActivity()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun submitFeedback() {
        val name = inputName.text.toString()
        val email = inputEmail.text.toString()
        val feedback = inputMessage.text.toString()

        if (feedback.isEmpty()) {
            Toast.makeText(this, "Feedback tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackId = database.push().key
        val feedbackData = Feedback(feedbackId ?: "", name, email, feedback)

        if (feedbackId != null) {
            database.child(feedbackId).setValue(feedbackData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Feedback berhasil dikirim", Toast.LENGTH_SHORT).show()
                    inputMessage.text.clear()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengirim feedback", Toast.LENGTH_SHORT).show()
                }
        }
    }

    data class Feedback(
        var feedbackId: String = "",
        var name: String = "",
        var email: String = "",
        var feedback: String = ""
    )
}