package com.example.projetofinal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class NFixaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contasContainer: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var fabAdicionar: FloatingActionButton
    private lateinit var btnVoltar: ImageButton

    private var currentUserId: Long = -1L
    private var selectedImageUri: Uri? = null
    private var imageViewDialog: ImageView? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageViewDialog?.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.n_fixa_activity)

        currentUserId = intent.getLongExtra("USER_ID", -1L)
        if (currentUserId == -1L) {
            Toast.makeText(this, "Erro: ID de usuário não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        contasContainer = findViewById(R.id.ll_contas_container)
        tvTotal = findViewById(R.id.tv_total)
        fabAdicionar = findViewById(R.id.fab_adicionar)
        btnVoltar = findViewById(R.id.btn_voltar)

        fabAdicionar.setOnClickListener { showAddOrEditDialog(null) }
        btnVoltar.setOnClickListener { finish() }

        loadContas()
    }

    private fun loadContas() {
        contasContainer.removeAllViews()
        val contas = dbHelper.getAllContasNaoFixas(currentUserId)
        var total = 0.0

        if (contas.isEmpty()) {
            val noDataView = layoutInflater.inflate(R.layout.item_vazio, contasContainer, false)
            contasContainer.addView(noDataView)
        } else {
            for (conta in contas) {
                val view = LayoutInflater.from(this).inflate(R.layout.item_conta, contasContainer, false)
                val nomeConta = view.findViewById<TextView>(R.id.tv_nome_conta)
                val valorConta = view.findViewById<TextView>(R.id.tv_valor_conta)
                val imagemConta = view.findViewById<ImageView>(R.id.iv_conta_imagem)
                val btnEditar = view.findViewById<Button>(R.id.btn_editar)
                val btnApagar = view.findViewById<Button>(R.id.btn_apagar)

                nomeConta.text = conta.nome
                valorConta.text = formatCurrency(conta.valor)
                total += conta.valor

                if (conta.imagemUri != null) {
                    imagemConta.setImageURI(Uri.parse(conta.imagemUri))
                } else {
                    imagemConta.setImageResource(R.drawable.ic_placeholder)
                }

                btnEditar.setOnClickListener { showAddOrEditDialog(conta) }
                btnApagar.setOnClickListener { showDeleteConfirmationDialog(conta) }

                contasContainer.addView(view)
            }
        }
        tvTotal.text = "Total de Despesas: ${formatCurrency(total)}"
    }

    private fun showAddOrEditDialog(conta: ContaNaoFixa?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_conta, null)
        val etNome = dialogView.findViewById<EditText>(R.id.et_nome_conta)
        val etValor = dialogView.findViewById<EditText>(R.id.et_valor_conta)
        val btnEscolherImagem = dialogView.findViewById<Button>(R.id.btn_escolher_imagem)
        imageViewDialog = dialogView.findViewById(R.id.iv_preview_imagem)
        val dialogTitle = if (conta == null) "Adicionar Conta" else "Editar Conta"

        if (conta != null) {
            etNome.setText(conta.nome)
            etValor.setText(conta.valor.toString())
            if (conta.imagemUri != null) {
                selectedImageUri = Uri.parse(conta.imagemUri)
                imageViewDialog?.setImageURI(selectedImageUri)
            }
        } else {
            selectedImageUri = null
        }

        btnEscolherImagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton(if (conta == null) "Adicionar" else "Salvar") { dialog, _ ->
                val nome = etNome.text.toString()
                val valorStr = etValor.text.toString()

                if (nome.isNotEmpty() && valorStr.isNotEmpty()) {
                    val valor = valorStr.toDoubleOrNull()
                    if (valor == null) {
                        Toast.makeText(this, "Por favor, insira um valor numérico válido.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val novaConta = ContaNaoFixa(
                        id = conta?.id ?: 0,
                        nome = nome,
                        valor = valor,
                        imagemUri = selectedImageUri?.toString(),
                        userId = currentUserId
                    )

                    if (conta == null) {
                        dbHelper.addContaNaoFixa(novaConta)
                        Toast.makeText(this, "Conta adicionada!", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateContaNaoFixa(novaConta)
                        Toast.makeText(this, "Conta atualizada!", Toast.LENGTH_SHORT).show()
                    }
                    loadContas()
                } else {
                    Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showDeleteConfirmationDialog(conta: ContaNaoFixa) {
        AlertDialog.Builder(this)
            .setTitle("Apagar conta")
            .setMessage("Tem certeza que deseja apagar a conta '${conta.nome}'?")
            .setPositiveButton("Apagar") { _, _ ->
                dbHelper.deleteContaNaoFixa(conta)
                Toast.makeText(this, "Conta apagada.", Toast.LENGTH_SHORT).show()
                loadContas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }
}