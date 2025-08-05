package com.example.projetofinal

data class ContaFixa(
    val id: Long,
    var nome: String,
    var valor: Double,
    var imagemUri: String?,
    val userId: Long
)
