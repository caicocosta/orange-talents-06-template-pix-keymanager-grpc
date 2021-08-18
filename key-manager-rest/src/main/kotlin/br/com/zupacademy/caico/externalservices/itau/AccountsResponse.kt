package br.com.zupacademy.caico.externalservices.itau

data class AccountsResponse (
    val titular: AccountOwner,
    val agencia: String,
    val numero: String
)

data class AccountOwner(
    val nome: String,
    val cpf: String
)
