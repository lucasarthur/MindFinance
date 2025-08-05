package com.example.projetofinal

data class Meta(
    val id: Long,
    var nome: String,
    var valorTotal: Double,
    var valorAtual: Double,
    var imagemUri: String?,
    val userId: Long
)