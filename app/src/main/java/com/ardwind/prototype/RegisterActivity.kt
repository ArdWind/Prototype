package com.ardwind.prototype

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var editFullName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPasswordConf: EditText
    private lateinit var btnReg: Button
    private lateinit var progressDialog: ProgressDialog
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        setupWindowInsets()
        initializeViews()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        editFullName = findViewById(R.id.regtextbox1)
        editEmail = findViewById(R.id.regtextbox2)
        editPassword = findViewById(R.id.regtextbox3)
        editPasswordConf = findViewById(R.id.regtextbox4)
        btnReg = findViewById(R.id.regbtn1)
        progressDialog = ProgressDialog(this).apply {
            setTitle("Logging")
            setMessage("Silahkan tunggu...")
        }
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btnbackreg).setOnClickListener {
            navigateToMainActivity()
        }

        btnReg.setOnClickListener {
            if (editFullName.text.isNotEmpty() && editEmail.text.isNotEmpty() && editPassword.text.isNotEmpty()) {
                if (editPassword.text.toString() == editPasswordConf.text.toString()) {
                    prosesRegister()
                } else {
                    Toast.makeText(this, "Konfirmasi password harus sama!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Isi data register dengan lengkap!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.regtext4).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun prosesRegister() {
        val fullName = editFullName.text.toString()
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()

        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userUpdateProfile = userProfileChangeRequest {
                        displayName = fullName
                    }
                    val user = task.result.user
                    user!!.updateProfile(userUpdateProfile)
                        .addOnCompleteListener {
                            progressDialog.dismiss()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Pendaftaran tidak berhasil!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    progressDialog.dismiss()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Pendaftaran tidak berhasil!", Toast.LENGTH_SHORT).show()
            }
    }
}