package br.com.zupacademy.caico.deletekeys

import br.com.zupacademy.caico.DeletePixKeyRequest
import br.com.zupacademy.caico.KeyManagerDeleteServiceGrpc
import br.com.zupacademy.caico.TypeAccount
import br.com.zupacademy.caico.TypeKey
import br.com.zupacademy.caico.registerkeys.KeyRepository
import br.com.zupacademy.caico.registerkeys.PixKeys
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import org.junit.jupiter.api.assertThrows as myAsserts

@MicronautTest(transactional = false)
internal class DeleteKeysEndpointTest(
    private val keyRepository: KeyRepository,
    private val grpcClient: KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub
){

    lateinit var key: PixKeys

    @BeforeEach
    internal fun setUp() {
        key = PixKeys(
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890",),
        "caico@gmail.com",
            TypeKey.EMAIL,
            TypeAccount.CONTA_CORRENTE
        )
        keyRepository.save(key)
    }

    @AfterEach
    internal fun tearDown() {
        keyRepository.deleteAll()
    }

    @Test
    internal fun `deve removar um registro no banco`() {

        val response = grpcClient.delete(DeletePixKeyRequest.newBuilder()
            .setUuidUsuario(key.clientId.toString())
            .setPixId(key.id.toString())
            .build())

        with(response){
            assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", response.uuidUsuario)
        }
    }

    @Test
    internal fun `deve retornar erro de chave nao encontrada ou nao pertecente ao cliente`() {

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.delete(DeletePixKeyRequest.newBuilder()
                .setUuidUsuario("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setPixId(UUID.randomUUID().toString())
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("Chave não encontrada, ou não pertecente a este cliente", error.status.description)
        }
    }

    @Test
    internal fun `deve retornar erro por nao ser pertecente ao cliente `() {

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.delete(DeletePixKeyRequest.newBuilder()
                .setUuidUsuario("4f23e002-238d-4d0f-a202-6a431434173d")
                .setPixId(key.id.toString())
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("Chave não encontrada, ou não pertecente a este cliente", error.status.description)
        }
    }

    @Test
    internal fun `deve retornar erro de uuid invalido pelo clientid`() {
        val error = myAsserts<StatusRuntimeException> {
            grpcClient.delete(DeletePixKeyRequest.newBuilder()
                .setUuidUsuario("caico")
                .setPixId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("Invalid UUID string: caico", error.status.description)
        }
    }

    @Test
    internal fun `deve retornar erro de uuid invalido pelo pixid`() {
        val error = myAsserts<StatusRuntimeException> {
            grpcClient.delete(DeletePixKeyRequest.newBuilder()
                .setUuidUsuario("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setPixId("caico")
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("Invalid UUID string: caico", error.status.description)
        }
    }

    @Test
    internal fun `deve retornar erro por enviar parametro nulo`() {
        val error = myAsserts<StatusRuntimeException> {
            grpcClient.delete(DeletePixKeyRequest.newBuilder()
                .setUuidUsuario("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("Invalid UUID string: ", error.status.description)
        }
    }

    @Factory
    class grpcFactory {
        @Bean
        fun deleteStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)=
            KeyManagerDeleteServiceGrpc.newBlockingStub(channel)
    }
}