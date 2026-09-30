package com.example.appeleitoral

data class RespostaPesquisa(
    var tipoPesquisa: String = "",
    var itensCheckbox: List<String> = emptyList(),
    var candidatoEscolhido: String = "",
    var email: String = "",
    var telefone: String = "",
    var comentarioLivre: String = "",
    var timestamp: Long = 0L
)