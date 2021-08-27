package br.com.zupacademy.caico.listkeys

import br.com.zupacademy.caico.FindPixKeyDetailsRequest
import br.com.zupacademy.caico.KeyManagerDetailsServiceGrpc
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import org.junit.jupiter.api.assertThrows as myAsserts

@MicronautTest
internal class ListAllKeysEndpointTest(
    private val keyRepository: KeyRepository,
    private val grpcClient: KeyManagerDetailsServiceGrpc.KeyManagerDetailsServiceBlockingStub
){

    lateinit var client: UUID


    @BeforeEach
    internal fun setUp() {
        client = UUID.randomUUID()
        val key1 = PixKeys(client, "caico@gmail.com", TypeKey.EMAIL, TypeAccount.CONTA_CORRENTE)
        val key2 = PixKeys(client, "99999999999", TypeKey.DOCUMENT, TypeAccount.CONTA_CORRENTE)
        val key3 = PixKeys(client, "+5538999880099", TypeKey.CELPHONE, TypeAccount.CONTA_POUPANCA)
        val key4 = PixKeys(client, UUID.randomUUID().toString(), TypeKey.RANDOM, TypeAccount.CONTA_POUPANCA)

        keyRepository.save(key1)
        keyRepository.save(key2)
        keyRepository.save(key3)
        keyRepository.save(key4)

    }

    @AfterEach
    internal fun tearDown() {
        keyRepository.deleteAll()
    }

    @Test
    internal fun `deve retornar as chaves cadastradas`() {
        val response = grpcClient.findDetails(FindPixKeyDetailsRequest.newBuilder()
            .setUuidUsuario(client.toString())
            .build())

        with(response){
            assertNotNull(clientId)
            assertEquals(4, keysCount)
        }

    }

    @Test
    internal fun `deve retornar erro de cliend id nulo ou branco`() {

        val error = myAsserts<StatusRuntimeException> {
            grpcClient.findDetails(FindPixKeyDetailsRequest.newBuilder()
                .setUuidUsuario("").build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("o ID do usuário não pode ser nulo ou vazio", status.description)
        }
    }

    @Test
    internal fun `deve retornar uma lista vazia de chaves caso nao exista nenhuma cadastrada`() {
        val response = grpcClient.findDetails(FindPixKeyDetailsRequest.newBuilder()
            .setUuidUsuario(UUID.randomUUID().toString())
            .build())

        with(response){
            assertNotNull(clientId)
            assertEquals(0, keysCount)
        }
    }

    @Factory
    class grpcFactory {
        @Bean
        fun detailsStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)=
            KeyManagerDetailsServiceGrpc.newBlockingStub(channel)
    }
}