package br.com.zupacademy.caico.externalservices.itau

data class AccountsResponse (
    val titular: AccountOwner,
    val agencia: String,
    val numero: String,
    val instituicao: Institution
)

data class Institution(
    val nome: String,
    val ispb: String,
)

data class AccountOwner(
    val nome: String,
    val cpf: String
)
