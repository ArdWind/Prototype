package com.ardwind.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBackLog = findViewById<ImageView>(R.id.btnbacklog)
        btnBackLog.setOnClickListener {
            navigateToMainActivity()
        }

        // Dapatkan referensi TextView dari XML
        val logText3 = findViewById<TextView>(R.id.logtext3)

        // Set onClickListener pada TextView
        logText3.setOnClickListener {
            // Buat Intent untuk berpindah ke RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Initialize button and set click listener to login
        val btnLogin = findViewById<Button>(R.id.logbtn1)
        btnLogin.setOnClickListener {
            navigateToHomeActivity()
        }
    }
}