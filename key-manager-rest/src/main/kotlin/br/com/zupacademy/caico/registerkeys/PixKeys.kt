package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.validators.ValidUUID
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class PixKeys(
    @ValidUUID
    @field:NotBlank
    val clientId: UUID,
    val key: String,
    @field:NotNull
    val typeKey: TypeKey,
    @field:NotNull
    val typeAccount: TypeAccount,
) {
    @Id
    val id: UUID = UUID.randomUUID()
}
