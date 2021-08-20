package br.com.zupacademy.caico.externalservices.bcb

import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.externalservices.itau.AccountsResponse
import br.com.zupacademy.caico.registerkeys.PixKeys
import io.micronaut.core.annotation.Introspected

@Introspected
data class CreateKeyBcbRequest(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
){
    companion object {
        fun of(pixKey: PixKeys, account: AccountsResponse): CreateKeyBcbRequest {
            return CreateKeyBcbRequest(
                keyType = pixKey.toTypeBcb(),
                key = pixKey.key,
                bankAccount = BankAccount(
                    participant = account.instituicao.ispb,
                    branch = account.agencia,
                    accountNumber = account.numero,
                    accountType = if (pixKey.typeAccount == TypeAccount.CONTA_CORRENTE) AccountType.CACC else AccountType.SVGS
                ),
                owner = Owner(
                    type = OwnerType.NATURAL_PERSON,
                    name = account.titular.nome,
                    taxIdNumber = account.titular.cpf
                )
            )
        }
    }
}
