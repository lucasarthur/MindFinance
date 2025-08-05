package com.example.projetofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnCriar: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        inputEmail = findViewById(R.id.input_email)
        inputPassword = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btn_login)
        btnCriar = findViewById(R.id.btn_criar)
        dbHelper = DatabaseHelper(this)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnCriar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, insira e-mail e senha", Toast.LENGTH_SHORT).show()
            return
        }

        val user = dbHelper.checkUser(email, password)

        if (user != null) {
            Toast.makeText(this, "Login feito! Bem-vindo, ${user.name}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("USER_ID", user.id ?: -1)
            intent.putExtra("USER_NAME", user.name)
            intent.putExtra("USER_INCOME", user.income)

            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_LONG).show()
        }
    }
}