package br.com.zupacademy.caico.externalservices.bcb

import java.time.LocalDateTime

data class CreateKeyBcbResponse (
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)