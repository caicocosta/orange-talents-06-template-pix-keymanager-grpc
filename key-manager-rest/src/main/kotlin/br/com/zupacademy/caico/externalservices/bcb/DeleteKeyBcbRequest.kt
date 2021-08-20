package br.com.zupacademy.caico.externalservices.bcb

data class DeleteKeyBcbRequest(
    val key: String,
    val participant: String
)
