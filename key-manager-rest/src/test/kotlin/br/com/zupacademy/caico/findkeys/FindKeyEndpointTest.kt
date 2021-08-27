package br.com.zupacademy.caico.findkeys

import br.com.zupacademy.caico.FindPixKeyRequest
import br.com.zupacademy.caico.KeyManagerFindServiceGrpc
import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.externalservices.bcb.*
import br.com.zupacademy.caico.externalservices.itau.AccountOwner
import br.com.zupacademy.caico.externalservices.itau.AccountsResponse
import br.com.zupacademy.caico.externalservices.itau.Institution
import br.com.zupacademy.caico.externalservices.itau.ItauClient
import br.com.zupacademy.caico.registerkeys.KeyRepository
import br.com.zupacademy.caico.registerkeys.PixKeys
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import org.junit.jupiter.api.assertThrows as myAsserts

@MicronautTest(transactional = false)
internal class FindKeyEndpointTest(
    private val keyRepository: KeyRepository,
    private val grpcClient: KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub,
){

    @Inject
    lateinit var itauClient: ItauClient

    @Inject
    lateinit var clientBcb: ClientBcb

    lateinit var key: PixKeys

    lateinit var createKeyBcbRequest: CreateKeyBcbRequest

    @BeforeEach
    internal fun setUp() {
        keyRepository.deleteAll()
        key = PixKeys(
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890",),
            "caico@gmail.com",
            TypeKey.EMAIL,
            TypeAccount.CONTA_CORRENTE
        )

        keyRepository.save(key)

        Mockito.`when`(itauClient
            .findAccounts(key.clientId.toString(), key.typeAccount.name))
            .thenReturn(accountResponseItau())

    }

    /*
    @Test
    internal fun `deve retornar um registro da chave cadastrada`() {
        Mockito.`when`(clientBcb
            .find(key.key))
            .thenReturn(accountResponseBcb("caico@gmail.com"))

        val response = grpcClient.find(FindPixKeyRequest.newBuilder()
            .setPixId(key.id.toString())
            .setUuidUsuario(key.clientId.toString())
            .setKey("")
            .build())

        with(response){
            assertEquals(key, "caico@gmail.com")
            assertEquals(cpf, "99999999999")
        }

    }*/

    @Test
    internal fun `deve retornar um erro de chave encontrada por nao existir o pixId no banco de dados`() {
        val error = myAsserts<StatusRuntimeException> {
            grpcClient.find(FindPixKeyRequest.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setUuidUsuario(key.clientId.toString())
                .setKey("")
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada ou não pertencente ao cliente informado", status.description)
        }

    }

    @Test
    internal fun `deve retornar um erro de chave encontrada por nao existir no banco de dados`() {
        val error = myAsserts<StatusRuntimeException> {
            grpcClient.find(FindPixKeyRequest.newBuilder()
                .setPixId(key.id.toString())
                .setUuidUsuario(key.clientId.toString())
                .setKey("testando chave invalida")
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada ou não pertencente ao cliente informado", status.description)
        }

    }

    @Test
    internal fun `deve retornar erro de chave invalida por nao existir no bcb`() {
        Mockito.`when`(clientBcb
            .find("chave invalida"))
            .thenReturn(HttpResponse.notFound())

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.find(FindPixKeyRequest.newBuilder()
                .setPixId(key.id.toString())
                .setUuidUsuario(key.clientId.toString())
                .setKey("")
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave inválida", status.description)
        }
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @MockBean(ClientBcb::class)
    fun clientBcb(): ClientBcb? {
        return Mockito.mock(ClientBcb::class.java)
    }

    fun accountResponseItau(): MutableHttpResponse<AccountsResponse> {
        return HttpResponse.ok<AccountsResponse>(
            AccountsResponse(
                agencia = "0001",
                numero = "999999",
                instituicao = Institution("ITAÚ UNIBANCO S.A.", "60701190"),
                titular = AccountOwner(
                    "Caico Costa",
                    "99999999999"
                )
            )
        )
    }

    fun accountResponseBcb(key: String): MutableHttpResponse<CreateKeyBcbResponse> {
        return HttpResponse.ok<CreateKeyBcbResponse>(
            CreateKeyBcbResponse(
                "CPF",
                key,
                BankAccount(
                    "60701190",
                    "0001",
                    "999999",
                    AccountType.CACC
                ),
                Owner(
                    OwnerType.NATURAL_PERSON,
                    "CAICO COSTA",
                    "99999999999"
                ),
                LocalDateTime.now()
            )
        )
    }

    @Factory
    class grpcFactory {
        @Bean
        fun findStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)=
            KeyManagerFindServiceGrpc.newBlockingStub(channel)
    }
}