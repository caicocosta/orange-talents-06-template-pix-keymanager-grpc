package br.com.zupacademy.caico.deletekeys

import br.com.zupacademy.caico.exceptionsmodels.PixKeyNotFoundException
import br.com.zupacademy.caico.externalservices.bcb.ClientBcb
import br.com.zupacademy.caico.externalservices.bcb.DeleteKeyBcbRequest
import br.com.zupacademy.caico.registerkeys.KeyRepository
import br.com.zupacademy.caico.registerkeys.PixKeys
import br.com.zupacademy.caico.validators.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import jdk.net.SocketFlow
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Validated
@Singleton
class DeleteKeyService(@Inject val keyRepository: KeyRepository, @Inject val clientBcb: ClientBcb) {

    fun delete(
        @NotNull @ValidUUID(message = "formato invalido para o id do cliente") clientId: UUID,
        @NotNull @ValidUUID(message = "pix Id com formato inválido") pixId: UUID,
    ) {

        val existingKey: PixKeys = keyRepository.findByIdAndClientId(pixId, clientId)
            .orElseThrow{PixKeyNotFoundException("Chave não encontrada, ou não pertecente a este cliente")}

        keyRepository.deleteById(pixId)

        val deleteRequest = DeleteKeyBcbRequest(
            existingKey.key,
            "60701190"
        )
        val bcbResponse = clientBcb.delete(deleteRequest, existingKey.key)

        if(bcbResponse.status != HttpStatus.OK){
            throw IllegalStateException("Erro ao tentar deletar a chave no sistema do Banco Central do Brasil")
        }
    }


}
