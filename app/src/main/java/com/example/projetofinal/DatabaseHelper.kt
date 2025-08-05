package com.example.projetofinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_VERSION = 6
private const val DATABASE_NAME = "MindFinance.db"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Tabela de usuários
        private const val TABLE_USERS = "users"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_INCOME = "income"

        // Colunas comuns para contas
        private const val KEY_CONTA_ID = "id"
        private const val KEY_CONTA_NOME = "nome"
        private const val KEY_CONTA_VALOR = "valor"
        private const val KEY_CONTA_IMAGEM_URI = "imagem_uri"
        private const val KEY_CONTA_USER_ID = "user_id"

        // Nomes das tabelas
        private const val TABLE_CONTAS_FIXAS = "contas_fixas"
        private const val TABLE_CONTAS_NAO_FIXAS = "contas_nao_fixas"

        // Tabela de metas
        private const val TABLE_METAS = "metas"
        private const val KEY_META_ID = "id"
        private const val KEY_META_NOME = "nome"
        private const val KEY_META_VALOR_TOTAL = "valor_total"
        private const val KEY_META_VALOR_ATUAL = "valor_atual"
        private const val KEY_META_IMAGEM_URI = "imagem_uri"
        private const val KEY_META_USER_ID = "user_id"

        // Tabela de histórico
        private const val TABLE_HISTORICO = "historico_mensal"
        private const val KEY_LOG_ID = "id"
        private const val KEY_LOG_MES_ANO = "mes_ano"
        private const val KEY_LOG_SALDO = "saldo_final"
        private const val KEY_LOG_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_NAME TEXT,"
                + "$KEY_EMAIL TEXT UNIQUE,"
                + "$KEY_PASSWORD TEXT,"
                + "$KEY_INCOME REAL)")
        db?.execSQL(CREATE_USERS_TABLE)

        val CREATE_CONTAS_FIXAS_TABLE = ("CREATE TABLE $TABLE_CONTAS_FIXAS("
                + "$KEY_CONTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_CONTA_NOME TEXT,"
                + "$KEY_CONTA_VALOR REAL,"
                + "$KEY_CONTA_IMAGEM_URI TEXT,"
                + "$KEY_CONTA_USER_ID INTEGER,"
                + "FOREIGN KEY($KEY_CONTA_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
        db?.execSQL(CREATE_CONTAS_FIXAS_TABLE)

        val CREATE_CONTAS_NAO_FIXAS_TABLE = ("CREATE TABLE $TABLE_CONTAS_NAO_FIXAS("
                + "$KEY_CONTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_CONTA_NOME TEXT,"
                + "$KEY_CONTA_VALOR REAL,"
                + "$KEY_CONTA_IMAGEM_URI TEXT,"
                + "$KEY_CONTA_USER_ID INTEGER,"
                + "FOREIGN KEY($KEY_CONTA_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
        db?.execSQL(CREATE_CONTAS_NAO_FIXAS_TABLE)

        val CREATE_METAS_TABLE = ("CREATE TABLE $TABLE_METAS("
                + "$KEY_META_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_META_NOME TEXT,"
                + "$KEY_META_VALOR_TOTAL REAL,"
                + "$KEY_META_VALOR_ATUAL REAL,"
                + "$KEY_META_IMAGEM_URI TEXT,"
                + "$KEY_META_USER_ID INTEGER,"
                + "FOREIGN KEY($KEY_META_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
        db?.execSQL(CREATE_METAS_TABLE)

        val CREATE_HISTORICO_TABLE = ("CREATE TABLE $TABLE_HISTORICO("
                + "$KEY_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_LOG_MES_ANO TEXT,"
                + "$KEY_LOG_SALDO REAL,"
                + "$KEY_LOG_USER_ID INTEGER,"
                + "FOREIGN KEY($KEY_LOG_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
        db?.execSQL(CREATE_HISTORICO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val CREATE_CONTAS_FIXAS_TABLE = ("CREATE TABLE $TABLE_CONTAS_FIXAS("
                    + "$KEY_CONTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_CONTA_NOME TEXT,"
                    + "$KEY_CONTA_VALOR REAL,"
                    + "$KEY_CONTA_IMAGEM_URI TEXT)")
            db?.execSQL(CREATE_CONTAS_FIXAS_TABLE)
        }
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_CONTAS_FIXAS ADD COLUMN $KEY_CONTA_USER_ID INTEGER")
        }
        if (oldVersion < 4) {
            val CREATE_CONTAS_NAO_FIXAS_TABLE = ("CREATE TABLE $TABLE_CONTAS_NAO_FIXAS("
                    + "$KEY_CONTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_CONTA_NOME TEXT,"
                    + "$KEY_CONTA_VALOR REAL,"
                    + "$KEY_CONTA_IMAGEM_URI TEXT,"
                    + "$KEY_CONTA_USER_ID INTEGER,"
                    + "FOREIGN KEY($KEY_CONTA_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
            db?.execSQL(CREATE_CONTAS_NAO_FIXAS_TABLE)
        }
        if (oldVersion < 5) {
            val CREATE_METAS_TABLE = ("CREATE TABLE $TABLE_METAS("
                    + "$KEY_META_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_META_NOME TEXT,"
                    + "$KEY_META_VALOR_TOTAL REAL,"
                    + "$KEY_META_VALOR_ATUAL REAL,"
                    + "$KEY_META_IMAGEM_URI TEXT,"
                    + "$KEY_META_USER_ID INTEGER,"
                    + "FOREIGN KEY($KEY_META_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
            db?.execSQL(CREATE_METAS_TABLE)
        }
        if (oldVersion < 6) {
            val CREATE_HISTORICO_TABLE = ("CREATE TABLE $TABLE_HISTORICO("
                    + "$KEY_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_LOG_MES_ANO TEXT,"
                    + "$KEY_LOG_SALDO REAL,"
                    + "$KEY_LOG_USER_ID INTEGER,"
                    + "FOREIGN KEY($KEY_LOG_USER_ID) REFERENCES $TABLE_USERS($KEY_ID))")
            db?.execSQL(CREATE_HISTORICO_TABLE)
        }
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, user.name)
        contentValues.put(KEY_EMAIL, user.email)
        contentValues.put(KEY_PASSWORD, user.password)
        contentValues.put(KEY_INCOME, user.income)
        val success = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return success
    }

    fun checkUser(email: String, password: String):User? {
        val db = this.readableDatabase
        val columns = arrayOf(KEY_ID, KEY_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_INCOME)
        val selection = "$KEY_EMAIL = ? AND $KEY_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(KEY_ID)
            val nameIndex = cursor.getColumnIndex(KEY_NAME)
            val emailIndex = cursor.getColumnIndex(KEY_EMAIL)
            val passwordIndex = cursor.getColumnIndex(KEY_PASSWORD)
            val incomeIndex = cursor.getColumnIndex(KEY_INCOME)
            if (idIndex != -1 && nameIndex != -1 && emailIndex != -1 && passwordIndex != -1 && incomeIndex != -1) {
                user = User(
                    id = cursor.getInt(idIndex),
                    name = cursor.getString(nameIndex),
                    email = cursor.getString(emailIndex),
                    password = cursor.getString(passwordIndex),
                    income = cursor.getDouble(incomeIndex)
                )
            }
        }
        cursor.close()
        db.close()
        return user
    }

    fun addContaFixa(conta: ContaFixa): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CONTA_NOME, conta.nome)
        values.put(KEY_CONTA_VALOR, conta.valor)
        values.put(KEY_CONTA_IMAGEM_URI, conta.imagemUri)
        values.put(KEY_CONTA_USER_ID, conta.userId)
        val success = db.insert(TABLE_CONTAS_FIXAS, null, values)
        db.close()
        return success
    }

    fun getAllContasFixas(userId: Long): List<ContaFixa> {
        val contaList = ArrayList<ContaFixa>()
        val selectQuery = "SELECT * FROM $TABLE_CONTAS_FIXAS WHERE $KEY_CONTA_USER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val conta = ContaFixa(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CONTA_ID)),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTA_NOME)),
                    valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_CONTA_VALOR)),
                    imagemUri = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTA_IMAGEM_URI)),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CONTA_USER_ID))
                )
                contaList.add(conta)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contaList
    }

    fun getTotalContasFixas(userId: Long): Double {
        var total = 0.0
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT IFNULL(SUM($KEY_CONTA_VALOR), 0.0) as Total FROM $TABLE_CONTAS_FIXAS WHERE $KEY_CONTA_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("Total"))
        }
        cursor.close()
        db.close()
        return total
    }

    fun updateContaFixa(conta: ContaFixa): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CONTA_NOME, conta.nome)
        values.put(KEY_CONTA_VALOR, conta.valor)
        values.put(KEY_CONTA_IMAGEM_URI, conta.imagemUri)
        values.put(KEY_CONTA_USER_ID, conta.userId)
        val success = db.update(TABLE_CONTAS_FIXAS, values, "$KEY_CONTA_ID=?", arrayOf(conta.id.toString()))
        db.close()
        return success
    }

    fun deleteContaFixa(conta: ContaFixa): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CONTAS_FIXAS, "$KEY_CONTA_ID=?", arrayOf(conta.id.toString()))
        db.close()
        return success
    }

    fun addContaNaoFixa(conta: ContaNaoFixa): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CONTA_NOME, conta.nome)
        values.put(KEY_CONTA_VALOR, conta.valor)
        values.put(KEY_CONTA_IMAGEM_URI, conta.imagemUri)
        values.put(KEY_CONTA_USER_ID, conta.userId)
        val success = db.insert(TABLE_CONTAS_NAO_FIXAS, null, values)
        db.close()
        return success
    }

    fun getAllContasNaoFixas(userId: Long): List<ContaNaoFixa> {
        val contaList = ArrayList<ContaNaoFixa>()
        val selectQuery = "SELECT * FROM $TABLE_CONTAS_NAO_FIXAS WHERE $KEY_CONTA_USER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val conta = ContaNaoFixa(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CONTA_ID)),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTA_NOME)),
                    valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_CONTA_VALOR)),
                    imagemUri = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CONTA_IMAGEM_URI)),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CONTA_USER_ID))
                )
                contaList.add(conta)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contaList
    }

    fun getTotalContasNaoFixas(userId: Long): Double {
        var total = 0.0
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT IFNULL(SUM($KEY_CONTA_VALOR), 0.0) as Total FROM $TABLE_CONTAS_NAO_FIXAS WHERE $KEY_CONTA_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("Total"))
        }
        cursor.close()
        db.close()
        return total
    }

    fun updateContaNaoFixa(conta: ContaNaoFixa): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CONTA_NOME, conta.nome)
        values.put(KEY_CONTA_VALOR, conta.valor)
        values.put(KEY_CONTA_IMAGEM_URI, conta.imagemUri)
        val success = db.update(TABLE_CONTAS_NAO_FIXAS, values, "$KEY_CONTA_ID=?", arrayOf(conta.id.toString()))
        db.close()
        return success
    }

    fun deleteContaNaoFixa(conta: ContaNaoFixa): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CONTAS_NAO_FIXAS, "$KEY_CONTA_ID=?", arrayOf(conta.id.toString()))
        db.close()
        return success
    }

    fun addMeta(meta: Meta): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_META_NOME, meta.nome)
        values.put(KEY_META_VALOR_TOTAL, meta.valorTotal)
        values.put(KEY_META_VALOR_ATUAL, meta.valorAtual)
        values.put(KEY_META_IMAGEM_URI, meta.imagemUri)
        values.put(KEY_META_USER_ID, meta.userId)
        val success = db.insert(TABLE_METAS, null, values)
        db.close()
        return success
    }

    fun getAllMetas(userId: Long): List<Meta> {
        val metaList = ArrayList<Meta>()
        val selectQuery = "SELECT * FROM $TABLE_METAS WHERE $KEY_META_USER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val meta = Meta(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_META_ID)),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow(KEY_META_NOME)),
                    valorTotal = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_META_VALOR_TOTAL)),
                    valorAtual = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_META_VALOR_ATUAL)),
                    imagemUri = cursor.getString(cursor.getColumnIndexOrThrow(KEY_META_IMAGEM_URI)),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_META_USER_ID))
                )
                metaList.add(meta)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return metaList
    }

    fun updateMeta(meta: Meta): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_META_NOME, meta.nome)
        values.put(KEY_META_VALOR_TOTAL, meta.valorTotal)
        values.put(KEY_META_VALOR_ATUAL, meta.valorAtual)
        values.put(KEY_META_IMAGEM_URI, meta.imagemUri)
        val success = db.update(TABLE_METAS, values, "$KEY_META_ID=?", arrayOf(meta.id.toString()))
        db.close()
        return success
    }

    fun deleteMeta(meta: Meta): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_METAS, "$KEY_META_ID=?", arrayOf(meta.id.toString()))
        db.close()
        return success
    }

    fun addLog(log: LogMensal): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_LOG_MES_ANO, log.mesAno)
        values.put(KEY_LOG_SALDO, log.saldoFinal)
        values.put(KEY_LOG_USER_ID, log.userId)
        val success = db.insert(TABLE_HISTORICO, null, values)
        db.close()
        return success
    }

    fun getLogByMesAno(mesAno: String, userId: Long): LogMensal? {
        val db = this.readableDatabase
        var log: LogMensal? = null
        val cursor = db.query(TABLE_HISTORICO, null, "$KEY_LOG_MES_ANO = ? AND $KEY_LOG_USER_ID = ?", arrayOf(mesAno, userId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            log = LogMensal(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LOG_ID)),
                mesAno = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOG_MES_ANO)),
                saldoFinal = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LOG_SALDO)),
                userId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LOG_USER_ID))
            )
        }
        cursor.close()
        db.close()
        return log
    }

    fun updateLog(log: LogMensal): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_LOG_SALDO, log.saldoFinal)
        val success = db.update(TABLE_HISTORICO, values, "$KEY_LOG_ID=?", arrayOf(log.id.toString()))
        db.close()
        return success
    }

    fun getAllLogs(userId: Long): List<LogMensal> {
        val logList = ArrayList<LogMensal>()
        val selectQuery = "SELECT * FROM $TABLE_HISTORICO WHERE $KEY_LOG_USER_ID = ? ORDER BY $KEY_LOG_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val log = LogMensal(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LOG_ID)),
                    mesAno = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOG_MES_ANO)),
                    saldoFinal = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LOG_SALDO)),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_LOG_USER_ID))
                )
                logList.add(log)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return logList
    }
}
