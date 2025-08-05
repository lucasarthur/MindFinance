package com.example.projetofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etRenda: EditText
    private lateinit var btnCriar: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        etNome = findViewById(R.id.et_nome)
        etEmail = findViewById(R.id.et_email)
        etSenha = findViewById(R.id.et_senha)
        etRenda = findViewById(R.id.et_renda)
        btnCriar = findViewById(R.id.btn_criar)
        dbHelper = DatabaseHelper(this)

        btnCriar.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        // Pega os valores dos campos de texto
        val name = etNome.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etSenha.text.toString().trim()
        val incomeStr = etRenda.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || incomeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val income = incomeStr.toDoubleOrNull()
        if (income == null) {
            Toast.makeText(this, "Por favor, insira um valor de renda válido", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(name = name, email = email, password = password, income = income)

        val status = dbHelper.addUser(user)

        if (status > -1) {
            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
            etNome.text.clear()
            etEmail.text.clear()
            etSenha.text.clear()
            etRenda.text.clear()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Erro ao criar a conta. O e-mail já pode estar em uso.", Toast.LENGTH_LONG).show()
        }
    }
}