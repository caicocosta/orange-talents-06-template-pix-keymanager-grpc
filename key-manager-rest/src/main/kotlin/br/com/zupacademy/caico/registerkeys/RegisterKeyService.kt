package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.exceptionsmodels.AlreadyExistsException
import br.com.zupacademy.caico.exceptionsmodels.InvalidFormat
import br.com.zupacademy.caico.externalservices.ItauClient
import br.com.zupacademy.caico.validators.KeyTypeValidator
import io.micronaut.validation.Validated
import org.hibernate.exception.ConstraintViolationException
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
    @Inject val itauClient: ItauClient
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun register(@Valid key: PixKeys): PixKeys? {

        try {
            val response = itauClient.findAccounts(key.clientId.toString(), key.typeAccount.name)
            if (response.body() == null){
                throw IllegalStateException("Conta não encontrada no Itau")
            }
        } catch (e: Exception){
            throw Exception("Erro ao se conectar ao servidor")
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
        return createdKey
    }

}
