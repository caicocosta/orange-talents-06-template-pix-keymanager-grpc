package br.com.zupacademy.caico.externalservices.bcb

import java.time.LocalDateTime

data class CreateKeyBcbResponse (
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateKeyBcbResponse

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}