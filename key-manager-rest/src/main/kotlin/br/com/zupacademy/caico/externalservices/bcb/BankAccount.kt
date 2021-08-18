package br.com.zupacademy.caico.externalservices.bcb

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)

enum class AccountType {
    CACC, SVGS, UNKNOWN;
}
