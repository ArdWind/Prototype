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

class RegisterActivity : AppCompatActivity() {

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBackReg = findViewById<ImageView>(R.id.btnbackreg)
        btnBackReg.setOnClickListener {
            navigateToMainActivity()
        }

        val btnLogin = findViewById<Button>(R.id.regbtn1)
        btnLogin.setOnClickListener {
            navigateToLoginActivity()
        }

        // Dapatkan referensi TextView dari XML
        val regText4 = findViewById<TextView>(R.id.regtext4)

        // Set onClickListener pada TextView
        regText4.setOnClickListener {
            // Buat Intent untuk berpindah ke RegisterActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}