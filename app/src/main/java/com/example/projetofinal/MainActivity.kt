package com.example.projetofinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvSaldo: TextView
    private lateinit var btnHistorico: MaterialButton
    private lateinit var btnGerenciarFixas: MaterialButton
    private lateinit var btnGerenciarNaoFixas: MaterialButton
    private lateinit var btnGerenciarMetas: MaterialButton

    private lateinit var ivFixa1: ImageView
    private lateinit var tvFixa1: TextView
    private lateinit var ivFixa2: ImageView
    private lateinit var tvFixa2: TextView
    private lateinit var ivFixa3: ImageView
    private lateinit var tvFixa3: TextView
    private lateinit var ivFixa4: ImageView
    private lateinit var tvFixa4: TextView

    private lateinit var ivNaoFixa1: ImageView
    private lateinit var tvNaoFixa1: TextView
    private lateinit var ivNaoFixa2: ImageView
    private lateinit var tvNaoFixa2: TextView
    private lateinit var ivNaoFixa3: ImageView
    private lateinit var tvNaoFixa3: TextView
    private lateinit var ivNaoFixa4: ImageView
    private lateinit var tvNaoFixa4: TextView

    private lateinit var ivMeta1: ImageView
    private lateinit var tvMeta1: TextView
    private lateinit var ivMeta2: ImageView
    private lateinit var tvMeta2: TextView
    private lateinit var ivMeta3: ImageView
    private lateinit var tvMeta3: TextView
    private lateinit var ivMeta4: ImageView
    private lateinit var tvMeta4: TextView

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var userIncome: Double = 0.0
    private var saldoMensal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)
        userIncome = intent.getDoubleExtra("USER_INCOME", 0.0)
        val userName = intent.getStringExtra("USER_NAME")

        initViews()

        if (!userName.isNullOrEmpty()) {
            tvGreeting.text = "Olá, $userName"
        }

        setupButtonClickListeners()
    }

    override fun onResume() {
        super.onResume()
        atualizarTudo()
    }

    private fun initViews() {
        tvGreeting = findViewById(R.id.tv_greeting)
        tvSaldo = findViewById(R.id.tv_saldo)

        btnHistorico = findViewById(R.id.btn_historico)
        btnGerenciarFixas = findViewById(R.id.btn_gerenciar_fixas)
        btnGerenciarNaoFixas = findViewById(R.id.btn_gerenciar_nao_fixas)
        btnGerenciarMetas = findViewById(R.id.btn_gerenciar_metas)

        ivFixa1 = findViewById(R.id.iv_fixa_1)
        tvFixa1 = findViewById(R.id.tv_fixa_1)
        ivFixa2 = findViewById(R.id.iv_fixa_2)
        tvFixa2 = findViewById(R.id.tv_fixa_2)
        ivFixa3 = findViewById(R.id.iv_fixa_3)
        tvFixa3 = findViewById(R.id.tv_fixa_3)
        ivFixa4 = findViewById(R.id.iv_fixa_4)
        tvFixa4 = findViewById(R.id.tv_fixa_4)

        ivNaoFixa1 = findViewById(R.id.iv_nao_fixa_1)
        tvNaoFixa1 = findViewById(R.id.tv_nao_fixa_1)
        ivNaoFixa2 = findViewById(R.id.iv_nao_fixa_2)
        tvNaoFixa2 = findViewById(R.id.tv_nao_fixa_2)
        ivNaoFixa3 = findViewById(R.id.iv_nao_fixa_3)
        tvNaoFixa3 = findViewById(R.id.tv_nao_fixa_3)
        ivNaoFixa4 = findViewById(R.id.iv_nao_fixa_4)
        tvNaoFixa4 = findViewById(R.id.tv_nao_fixa_4)

        ivMeta1 = findViewById(R.id.iv_meta_1)
        tvMeta1 = findViewById(R.id.tv_meta_1)
        ivMeta2 = findViewById(R.id.iv_meta_2)
        tvMeta2 = findViewById(R.id.tv_meta_2)
        ivMeta3 = findViewById(R.id.iv_meta_3)
        tvMeta3 = findViewById(R.id.tv_meta_3)
        ivMeta4 = findViewById(R.id.iv_meta_4)
        tvMeta4 = findViewById(R.id.tv_meta_4)
    }

    private fun atualizarTudo() {
        val totalDespesasFixas = dbHelper.getTotalContasFixas(userId.toLong())
        val totalDespesasNaoFixas = dbHelper.getTotalContasNaoFixas(userId.toLong())
        saldoMensal = userIncome - totalDespesasFixas - totalDespesasNaoFixas

        val formatadorDeMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        tvSaldo.text = "Saldo: ${formatadorDeMoeda.format(saldoMensal)}"

        atualizarPreviewContasFixas()
        atualizarPreviewContasNaoFixas()

        if (saldoMensal > 0) {
            var saldoRestante = saldoMensal
            val metas = dbHelper.getAllMetas(userId.toLong())
            for (meta in metas) {
                if (saldoRestante <= 0) break

                val valorFaltante = meta.valorTotal - meta.valorAtual
                if (valorFaltante > 0) {
                    val valorAPagar = Math.min(saldoRestante, valorFaltante)
                    meta.valorAtual += valorAPagar
                    dbHelper.updateMeta(meta)
                    saldoRestante -= valorAPagar
                }
            }
        }

        atualizarPreviewMetas()
    }

    private fun atualizarPreviewContasFixas() {
        val contasFixas = dbHelper.getAllContasFixas(userId.toLong())
        val imageViews = listOf(ivFixa1, ivFixa2, ivFixa3, ivFixa4)
        val textViews = listOf(tvFixa1, tvFixa2, tvFixa3, tvFixa4)

        for (i in imageViews.indices) {
            if (i < contasFixas.size) {
                val conta = contasFixas[i]
                textViews[i].text = conta.nome
                if (conta.imagemUri != null) {
                    imageViews[i].setBackgroundColor(getColor(android.R.color.transparent))
                    imageViews[i].setImageURI(Uri.parse(conta.imagemUri))
                } else {
                    imageViews[i].setImageResource(0)
                    imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
                }
            } else {
                textViews[i].text = "VAZIO"
                imageViews[i].setImageResource(0)
                imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
            }
        }
    }

    private fun atualizarPreviewContasNaoFixas() {
        val contasNaoFixas = dbHelper.getAllContasNaoFixas(userId.toLong())
        val imageViews = listOf(ivNaoFixa1, ivNaoFixa2, ivNaoFixa3, ivNaoFixa4)
        val textViews = listOf(tvNaoFixa1, tvNaoFixa2, tvNaoFixa3, tvNaoFixa4)

        for (i in imageViews.indices) {
            if (i < contasNaoFixas.size) {
                val conta = contasNaoFixas[i]
                textViews[i].text = conta.nome
                if (conta.imagemUri != null) {
                    imageViews[i].setBackgroundColor(getColor(android.R.color.transparent))
                    imageViews[i].setImageURI(Uri.parse(conta.imagemUri))
                } else {
                    imageViews[i].setImageResource(0)
                    imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
                }
            } else {
                textViews[i].text = "VAZIO"
                imageViews[i].setImageResource(0)
                imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
            }
        }
    }

    private fun atualizarPreviewMetas() {
        val metas = dbHelper.getAllMetas(userId.toLong())
        val imageViews = listOf(ivMeta1, ivMeta2, ivMeta3, ivMeta4)
        val textViews = listOf(tvMeta1, tvMeta2, tvMeta3, tvMeta4)

        for (i in imageViews.indices) {
            if (i < metas.size) {
                val meta = metas[i]
                textViews[i].text = meta.nome
                if (meta.imagemUri != null) {
                    imageViews[i].setBackgroundColor(getColor(android.R.color.transparent))
                    imageViews[i].setImageURI(Uri.parse(meta.imagemUri))
                } else {
                    imageViews[i].setImageResource(0)
                    imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
                }
            } else {
                textViews[i].text = "VAZIO"
                imageViews[i].setImageResource(0)
                imageViews[i].setBackgroundColor(getColor(R.color.placeholder_grey))
            }
        }
    }

    private fun setupButtonClickListeners() {
        if (userId == -1) {
            Toast.makeText(this, "Erro fatal: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
            return
        }

        btnHistorico.setOnClickListener {
            val intent = Intent(this, LogsActivity::class.java)
            intent.putExtra("USER_ID", userId.toLong())
            intent.putExtra("SALDO_MENSAL", saldoMensal)
            startActivity(intent)
        }

        btnGerenciarFixas.setOnClickListener {
            val intent = Intent(this, FixaActivity::class.java)
            intent.putExtra("USER_ID", userId.toLong())
            startActivity(intent)
        }

        btnGerenciarNaoFixas.setOnClickListener {
            val intent = Intent(this, NFixaActivity::class.java)
            intent.putExtra("USER_ID", userId.toLong())
            startActivity(intent)
        }

        btnGerenciarMetas.setOnClickListener {
            val intent = Intent(this, MetaActivity::class.java)
            intent.putExtra("USER_ID", userId.toLong())
            intent.putExtra("SALDO_MENSAL", saldoMensal)
            startActivity(intent)
        }
    }
}
