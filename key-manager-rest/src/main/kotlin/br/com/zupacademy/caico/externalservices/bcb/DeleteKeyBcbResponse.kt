package br.com.zupacademy.caico.externalservices.bcb

import java.time.LocalDateTime

data class DeleteKeyBcbResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)
