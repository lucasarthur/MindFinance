package com.example.projetofinal

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
import kotlin.math.abs

class MetaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var metasContainer: LinearLayout
    private lateinit var tvFaltante: TextView
    private lateinit var fabAdicionar: FloatingActionButton
    private lateinit var btnVoltar: ImageButton

    private var currentUserId: Long = -1L
    private var saldoMensal: Double = 0.0
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
        setContentView(R.layout.meta_activity)

        currentUserId = intent.getLongExtra("USER_ID", -1L)
        saldoMensal = intent.getDoubleExtra("SALDO_MENSAL", 0.0)

        if (currentUserId == -1L) {
            Toast.makeText(this, "ID de usuário não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        metasContainer = findViewById(R.id.ll_metas_container)
        tvFaltante = findViewById(R.id.tv_faltante)
        fabAdicionar = findViewById(R.id.fab_adicionar)
        btnVoltar = findViewById(R.id.btn_voltar)

        fabAdicionar.setOnClickListener { showAddOrEditDialog(null) }
        btnVoltar.setOnClickListener { finish() }

        loadMetas()
    }

    private fun loadMetas() {
        metasContainer.removeAllViews()
        val metas = dbHelper.getAllMetas(currentUserId)
        var totalFaltante = 0.0

        if (metas.isEmpty()) {
            val noDataView = layoutInflater.inflate(R.layout.item_vazio, metasContainer, false)
            metasContainer.addView(noDataView)
        } else {
            for (meta in metas) {
                totalFaltante += (meta.valorTotal - meta.valorAtual)
                val view = LayoutInflater.from(this).inflate(R.layout.item_meta, metasContainer, false)

                val nomeMeta = view.findViewById<TextView>(R.id.tv_nome_meta)
                val progressoMeta = view.findViewById<TextView>(R.id.tv_progresso_meta)
                val imagemMeta = view.findViewById<ImageView>(R.id.iv_meta_imagem)
                val btnEditar = view.findViewById<Button>(R.id.btn_editar)
                val btnApagar = view.findViewById<Button>(R.id.btn_apagar)

                nomeMeta.text = meta.nome
                progressoMeta.text = "${formatCurrency(meta.valorAtual)} / ${formatCurrency(meta.valorTotal)}"

                if (meta.imagemUri != null) {
                    imagemMeta.setImageURI(Uri.parse(meta.imagemUri))
                } else {
                    imagemMeta.setImageResource(R.drawable.ic_placeholder)
                }

                btnEditar.setOnClickListener { showAddOrEditDialog(meta) }
                btnApagar.setOnClickListener { showDeleteConfirmationDialog(meta) }

                metasContainer.addView(view)
            }
        }

        val valorFinalFaltante = if (saldoMensal >= 0) {
            totalFaltante - saldoMensal
        } else {
            totalFaltante + abs(saldoMensal)
        }

        tvFaltante.text = "Falta: ${formatCurrency(valorFinalFaltante)}"
    }

    private fun showAddOrEditDialog(meta: Meta?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_meta, null)
        val etNome = dialogView.findViewById<EditText>(R.id.et_nome_meta)
        val etValorTotal = dialogView.findViewById<EditText>(R.id.et_valor_total_meta)
        val btnEscolherImagem = dialogView.findViewById<Button>(R.id.btn_escolher_imagem)
        imageViewDialog = dialogView.findViewById(R.id.iv_preview_imagem)
        val dialogTitle = if (meta == null) "Adicionar Objetivo" else "Editar Objetivo"

        if (meta != null) {
            etNome.setText(meta.nome)
            etValorTotal.setText(meta.valorTotal.toString())
            if (meta.imagemUri != null) {
                selectedImageUri = Uri.parse(meta.imagemUri)
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
            .setPositiveButton(if (meta == null) "Adicionar" else "Salvar") { dialog, _ ->
                val nome = etNome.text.toString()
                val valorTotalStr = etValorTotal.text.toString()

                if (nome.isNotEmpty() && valorTotalStr.isNotEmpty()) {
                    val valorTotal = valorTotalStr.toDoubleOrNull()
                    if (valorTotal == null) {
                        Toast.makeText(this, "Por favor, insira um valor total válido.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val novaMeta = Meta(
                        id = meta?.id ?: 0,
                        nome = nome,
                        valorTotal = valorTotal,
                        valorAtual = meta?.valorAtual ?: 0.0,
                        imagemUri = selectedImageUri?.toString(),
                        userId = currentUserId
                    )

                    if (meta == null) {
                        dbHelper.addMeta(novaMeta)
                        Toast.makeText(this, "Objetivo adicionado!", Toast.LENGTH_SHORT).show()
                    } else {
                        dbHelper.updateMeta(novaMeta)
                        Toast.makeText(this, "Objetivo atualizado!", Toast.LENGTH_SHORT).show()
                    }
                    loadMetas()
                } else {
                    Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showDeleteConfirmationDialog(meta: Meta) {
        AlertDialog.Builder(this)
            .setTitle("Apagar Objetivo")
            .setMessage("Tem certeza que deseja apagar o objetivo '${meta.nome}'?")
            .setPositiveButton("Apagar") { _, _ ->
                dbHelper.deleteMeta(meta)
                Toast.makeText(this, "Objetivo apagado.", Toast.LENGTH_SHORT).show()
                loadMetas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }
}
