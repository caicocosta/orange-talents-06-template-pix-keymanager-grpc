package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.KeyManagerRequest
import br.com.zupacademy.caico.TypeKey
import java.util.*

fun KeyManagerRequest.toModel() : PixKeys{
    return PixKeys(
        clientId = UUID.fromString(uuidUsuario),
        typeKey = typeKey,
        key = key,
        typeAccount = typeAccount
    )
}