package br.com.zupacademy.caico.findkeys

import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.externalservices.bcb.AccountType
import br.com.zupacademy.caico.externalservices.itau.AccountsResponse

data class FindKeyResponse(
    val typeKey: TypeKey,
    val key: String,
    val account: AccountsResponse,
    val accountType: AccountType
) {
}