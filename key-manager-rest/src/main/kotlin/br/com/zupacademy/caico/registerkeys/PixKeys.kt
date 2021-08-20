package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.exceptionsmodels.InvalidFormat
import br.com.zupacademy.caico.validators.ValidUUID
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class PixKeys(
    @ValidUUID(message = "client Id com formato inválido")
    @field:NotBlank
    val clientId: UUID,
    @field:NotNull @field:Size(max = 77)
    var key: String,
    @field:NotNull
    val typeKey: TypeKey,
    @field:NotNull
    val typeAccount: TypeAccount,
) {
    fun toTypeBcb(): String {
        return when(typeKey) {
            TypeKey.DOCUMENT  -> isCpforCpnj()
            TypeKey.EMAIL -> typeKey.name
            TypeKey.CELPHONE -> "PHONE"
            TypeKey.RANDOM -> typeKey.name
            else -> {
                throw InvalidFormat("Formato de chave inválida")
            }
        }
    }

    private fun isCpforCpnj(): String {
        if(key.matches("^[0-9]{3}.?[0-9]{3}.?[0-9]{3}-?[0-9]{2}".toRegex())){
            return "CPF"
        } else
            return "CNPJ"
    }

    fun updateKey(key: String) {
        this.key = key
    }

    @Id
    val id: UUID = UUID.randomUUID()
}
