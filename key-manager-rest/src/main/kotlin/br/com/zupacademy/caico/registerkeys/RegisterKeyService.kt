package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.exceptionsmodels.AlreadyExistsException
import br.com.zupacademy.caico.exceptionsmodels.InvalidFormat
import br.com.zupacademy.caico.externalservices.bcb.*
import br.com.zupacademy.caico.externalservices.itau.ItauClient
import br.com.zupacademy.caico.validators.KeyTypeValidator
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegisterKeyService(
    @Inject val keyRepository: KeyRepository,
    @Inject val keyTypeValidator: KeyTypeValidator,
    @Inject val itauClient: ItauClient,
    @Inject val clientBcb: ClientBcb
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun register(@Valid key: PixKeys): PixKeys? {

         val response = itauClient.findAccounts(key.clientId.toString(), key.typeAccount.name)
         if (response.body() == null){
            throw IllegalStateException("Conta não encontrada no Itau")
         }

        if(keyRepository.existsByKey(key.key)){
            logger.info("Validando chave já cadatrada")
            throw AlreadyExistsException("Chave já cadastra")
        }

        if(key.typeKey != TypeKey.RANDOM){
            if(!keyTypeValidator.isValidFormat(key.typeKey, key.key)){
                throw InvalidFormat("Chave com formato inválida")
            }
        }
        val createdKey: PixKeys = keyRepository.save(key)

        lateinit var bcbResponse: HttpResponse<CreateKeyBcbResponse>

        try {
            val bcbRequest = CreateKeyBcbRequest.of(key, response.body())
            bcbResponse = clientBcb.create(bcbRequest)
        } catch (e: Exception) {
            throw IllegalStateException("Error ao registrar chave Pix no Banco Central do Brasil")
        }

        key.updateKey(bcbResponse.body().key)
        return createdKey
    }

}
