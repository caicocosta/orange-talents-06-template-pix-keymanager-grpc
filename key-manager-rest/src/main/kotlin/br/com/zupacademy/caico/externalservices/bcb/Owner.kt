package br.com.zupacademy.caico.externalservices.bcb

data class Owner (
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
)

enum class OwnerType {
    NATURAL_PERSON, LEGAL_PERSON
}
