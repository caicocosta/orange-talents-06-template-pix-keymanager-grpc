package br.com.zupacademy.caico.externalservices.bcb

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BankAccount

        if (participant != other.participant) return false
        if (branch != other.branch) return false
        if (accountNumber != other.accountNumber) return false
        if (accountType != other.accountType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = participant.hashCode()
        result = 31 * result + branch.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + accountType.hashCode()
        return result
    }
}

enum class AccountType {
    CACC, SVGS, UNKNOWN;
}
