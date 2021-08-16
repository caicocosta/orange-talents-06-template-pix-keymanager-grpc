package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.KeyManagerRequest
import br.com.zupacademy.caico.KeyManagerRestServiceGrpc
import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.externalservices.AccountsResponse
import br.com.zupacademy.caico.externalservices.ItauClient
import br.com.zupacademy.caico.validators.KeyTypeValidator
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import org.junit.jupiter.api.assertThrows as myAsserts

@MicronautTest(transactional = false)
internal class RegisterKeysEndpointTest(
    private val keyRepository: KeyRepository,
    private val grpcClient: KeyManagerRestServiceGrpc.KeyManagerRestServiceBlockingStub
){

    @Inject
    lateinit var itauClient: ItauClient

    @Inject
    lateinit var keyTypeValidator: KeyTypeValidator

    lateinit var key: PixKeys

    companion object{
        val client_id = UUID.randomUUID()
    }

    @BeforeEach
    internal fun setUp() {
        keyRepository.deleteAll()
        key = PixKeys(
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890",),
            "caico@gmail.com",
            TypeKey.EMAIL,
            TypeAccount.CONTA_CORRENTE
        )

        Mockito.`when`(itauClient
            .findAccounts(key.clientId.toString(), key.typeAccount.name))
            .thenReturn(accountResponseItau())
    }

    @Test
    internal fun `deve cadastrar nova chave pix`() {

        val response = grpcClient.register(KeyManagerRequest.newBuilder()
            .setUuidUsuario(key.clientId.toString())
            .setTypeKey(key.typeKey)
            .setKey(key.key)
            .setTypeAccount(key.typeAccount)
            .build())

        with(response){
            assertNotNull(pixId)
        }
    }

    @Test
    internal fun `nao deve cadastrar uma chave pix que já existe`() {
        keyRepository.save(key);

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(key.clientId.toString())
                .setTypeKey(key.typeKey)
                .setKey(key.key)
                .setTypeAccount(key.typeAccount)
                .build())
            }
            with(error){
                assertEquals(Status.ALREADY_EXISTS.code, status.code)
                assertEquals("Chave já cadastra", status.description)
            }
    }


    @Test
    internal fun `nao deve cadastrar chave em que a conta nao exista no servidor do itau`() {

        val nonExistentkey = PixKeys(
            UUID.randomUUID(),
            "email@teste.com.br",
            TypeKey.EMAIL,
            TypeAccount.CONTA_CORRENTE
        )

        Mockito.`when`(itauClient
            .findAccounts(nonExistentkey.clientId.toString(), nonExistentkey.typeAccount.name))
            .thenReturn(HttpResponse.notFound())

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(nonExistentkey.clientId.toString())
                .setTypeKey(nonExistentkey.typeKey)
                .setKey(nonExistentkey.key)
                .setTypeAccount(nonExistentkey.typeAccount)
                .build())
        }
        with(error){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Conta não encontrada no Itau", status.description)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave pix com cliendid invalido`() {

        val wrongId = "fadkfoakd-fadfsa"

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(wrongId)
                .setTypeKey(key.typeKey)
                .setKey(key.key)
                .setTypeAccount(key.typeAccount)
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Invalid UUID string: $wrongId", status.description)
        }
    }

    @Test
    internal fun `deve retornar erro por informar cpf invalido`() {

        val document = "000.000.000-"

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(key.clientId.toString())
                .setTypeKey(TypeKey.DOCUMENT)
                .setKey(document)
                .setTypeAccount(key.typeAccount)
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave com formato inválida", status.description)
        }
    }

    @Test
    internal fun `deve retornar erro por informar celular invalido`() {

        val celphone = "38999999939"

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(key.clientId.toString())
                .setTypeKey(TypeKey.CELPHONE)
                .setKey(celphone)
                .setTypeAccount(key.typeAccount)
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave com formato inválida", status.description)
        }
    }

    @Test
    internal fun `deve retornar erro por informar email invalido`() {

        val email = "caico@gmail."

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(key.clientId.toString())
                .setTypeKey(TypeKey.EMAIL)
                .setKey(email)
                .setTypeAccount(key.typeAccount)
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave com formato inválida", status.description)
        }
    }

    @Test
    internal fun `deve cadastrar nova chave pix com chave aleatoria`() {
        val response = grpcClient.register(KeyManagerRequest.newBuilder()
            .setUuidUsuario(key.clientId.toString())
            .setTypeKey(TypeKey.RANDOM)
            .setKey(key.key)
            .setTypeAccount(key.typeAccount)
            .build())

        val randomKey = keyRepository.findById(UUID.fromString(response.pixId))
            .orElseThrow()

        with(randomKey){
            assertNotNull(randomKey)
            //println("Caico: $key")
           // assertTrue(key.matches("/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$/i\n".toRegex()))
        }
    }

    @Test
    internal fun `deve retornar erro por informar um tipo de chave invalido`() {

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.register(KeyManagerRequest.newBuilder()
                .setUuidUsuario(key.clientId.toString())
                .setTypeKey(TypeKey.UNKNOWN)
                .setKey(key.key)
                .setTypeAccount(key.typeAccount)
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Tipo de chave desconhecido", status.description)
        }
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    fun accountResponseItau(): MutableHttpResponse<AccountsResponse>? {
        return HttpResponse.ok<AccountsResponse>(
            AccountsResponse(
                agencia = "0001",
                numero = "999999",
            )
        )
    }

    @Factory
    class grpcFactory {
        @Bean
        fun registeStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)=
            KeyManagerRestServiceGrpc.newBlockingStub(channel)
    }
}
