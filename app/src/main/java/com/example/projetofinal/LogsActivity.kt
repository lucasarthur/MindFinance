package com.example.projetofinal

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var logsContainer: LinearLayout
    private lateinit var btnVoltar: ImageButton
    private lateinit var tvSaldoAtualInfo: TextView
    private lateinit var btnSalvarMesAtual: Button

    private var currentUserId: Long = -1L
    private var saldoMensal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logs_activity)

        currentUserId = intent.getLongExtra("USER_ID", -1L)
        saldoMensal = intent.getDoubleExtra("SALDO_MENSAL", 0.0)

        if (currentUserId == -1L) {
            Toast.makeText(this, "Erro: ID de usuário não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        logsContainer = findViewById(R.id.ll_logs_container)
        btnVoltar = findViewById(R.id.btn_voltar)
        tvSaldoAtualInfo = findViewById(R.id.tv_saldo_atual_info)
        btnSalvarMesAtual = findViewById(R.id.btn_salvar_mes_atual)

        tvSaldoAtualInfo.text = "Saldo do mês atual: ${formatCurrency(saldoMensal)}"

        btnVoltar.setOnClickListener { finish() }
        btnSalvarMesAtual.setOnClickListener { salvarMesAtual() }

        loadLogs()
    }

    private fun loadLogs() {
        logsContainer.removeAllViews()
        val logs = dbHelper.getAllLogs(currentUserId)

        if (logs.isEmpty()) {
            val noDataView = TextView(this).apply {
                text = "Nenhum histórico salvo."
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 64, 0, 64)
            }
            logsContainer.addView(noDataView)
        } else {
            for (log in logs) {
                val view = LayoutInflater.from(this).inflate(R.layout.item_log, logsContainer, false)
                val tvMesAno = view.findViewById<TextView>(R.id.tv_mes_ano)
                val tvSaldoLog = view.findViewById<TextView>(R.id.tv_saldo_log)

                tvMesAno.text = log.mesAno
                tvSaldoLog.text = formatCurrency(log.saldoFinal)

                if (log.saldoFinal < 0) {
                    tvSaldoLog.setTextColor(Color.RED)
                } else {
                    tvSaldoLog.setTextColor(Color.parseColor("#4CAF50"))
                }

                logsContainer.addView(view)
            }
        }
    }

    private fun salvarMesAtual() {
        val sdf = SimpleDateFormat("MMMM/yyyy", Locale("pt", "BR"))
        val mesAnoAtual = sdf.format(Date()).replaceFirstChar { it.uppercase() }

        val logExistente = dbHelper.getLogByMesAno(mesAnoAtual, currentUserId)

        if (logExistente != null) {
            AlertDialog.Builder(this)
                .setTitle("Atualizar Histórico")
                .setMessage("Um saldo para $mesAnoAtual já foi salvo. Deseja atualizá-lo com o valor atual de ${formatCurrency(saldoMensal)}?")
                .setPositiveButton("Atualizar") { _, _ ->
                    val logAtualizado = logExistente.copy(saldoFinal = saldoMensal)
                    dbHelper.updateLog(logAtualizado)
                    Toast.makeText(this, "Histórico atualizado!", Toast.LENGTH_SHORT).show()
                    loadLogs()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        } else {
            val novoLog = LogMensal(id = 0, mesAno = mesAnoAtual, saldoFinal = saldoMensal, userId = currentUserId)
            dbHelper.addLog(novoLog)
            Toast.makeText(this, "Saldo de $mesAnoAtual salvo no histórico!", Toast.LENGTH_SHORT).show()
            loadLogs()
        }
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }
}
