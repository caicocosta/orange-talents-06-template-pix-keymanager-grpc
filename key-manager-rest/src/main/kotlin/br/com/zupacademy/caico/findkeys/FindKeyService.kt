package br.com.zupacademy.caico.findkeys

import br.com.zupacademy.caico.exceptionsmodels.PixKeyNotFoundException
import br.com.zupacademy.caico.externalservices.bcb.ClientBcb
import br.com.zupacademy.caico.externalservices.itau.ItauClient
import br.com.zupacademy.caico.registerkeys.KeyRepository
import br.com.zupacademy.caico.registerkeys.PixKeys
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindKeyService(
    @Inject val keyRepository: KeyRepository,
    @Inject val clientBcb: ClientBcb,
    @Inject val itauClient: ItauClient,
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findKey(clientId: UUID, pixId: UUID, key: String): FindKeyResponse {

        val existingKey: PixKeys = if(key == "") {
            keyRepository.findByIdAndClientId(pixId, clientId).orElseThrow {
                throw PixKeyNotFoundException("Chave não encontrada ou não pertencente ao cliente informado")
            }
        } else {
            keyRepository.findByKey(key).orElseThrow {
                throw PixKeyNotFoundException("Chave não encontrada ou não pertencente ao cliente informado")
            }
        }

        val bcbKey = clientBcb.find(existingKey.key)

        if (bcbKey.status != HttpStatus.OK) {
            throw PixKeyNotFoundException("Chave inválida")
        }

        val accountDetails = itauClient.findAccounts(existingKey.clientId.toString(), existingKey.typeAccount.name)

        if (accountDetails.status != HttpStatus.OK) {
            throw PixKeyNotFoundException("Chave inválida")
        }


        return FindKeyResponse(
            existingKey.typeKey,
            existingKey.key,
            accountDetails.body()!!,
            bcbKey.body().bankAccount.accountType!!
        )

    }
}
