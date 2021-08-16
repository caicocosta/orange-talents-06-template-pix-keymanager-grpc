package br.com.zupacademy.caico.validators

import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.exceptionsmodels.InvalidFormat
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Inject
import org.junit.jupiter.api.assertThrows as myAsserts

@MicronautTest
internal class KeyTypeValidatorTest(){

    @Inject
    lateinit var keyTypeValidator: KeyTypeValidator

    @Test
    internal fun `deve retornar false para email com formato invalido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.EMAIL, "caico@gmail.")

        assertTrue(!response)
    }

    @Test
    internal fun `deve retornar true para email com formato valido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.EMAIL, "caico@gmail.com")

        assertTrue(response)
    }

    @Test
    internal fun `deve retornar false para cpf com formato invalido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.DOCUMENT, "000.000.000-")

        assertTrue(!response)
    }

    @Test
    internal fun `deve retornar true para cpf com formato valido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.DOCUMENT, "000.000.000-00")

        assertTrue(response)
    }

    @Test
    internal fun `deve retornar false para cnpj com formato invalido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.DOCUMENT, "78.561.251/001-32")

        assertTrue(!response)
    }

    @Test
    internal fun `deve retornar true para cnpj com formato valido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.DOCUMENT, "78.561.251/0001-32")

        assertTrue(response)
    }

    @Test
    internal fun `deve retornar false para celular com formato invalido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.CELPHONE, "38999999999")

        assertTrue(!response)
    }

    @Test
    internal fun `deve retornar true para celular com formato valido`() {

        val response = keyTypeValidator.isValidFormat(TypeKey.CELPHONE, "+5538999999999")

        assertTrue(response)
    }

    @Test
    internal fun `deve retornar um erro de tipo desconhecido`() {

        val error = myAsserts<InvalidFormat> {
            keyTypeValidator.isValidFormat(TypeKey.UNKNOWN, "+5538999999999")
        }

        with(error){
            assertEquals("Tipo de chave desconhecido", message)
        }
    }
}