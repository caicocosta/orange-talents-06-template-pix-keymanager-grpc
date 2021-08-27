package br.com.zupacademy.caico.listkeys

import br.com.zupacademy.caico.FindPixKeyDetailsRequest
import br.com.zupacademy.caico.FindPixKeyDetailsResponse
import br.com.zupacademy.caico.KeyManagerDetailsServiceGrpc
import br.com.zupacademy.caico.registerkeys.KeyRepository
import br.com.zupacademy.caico.validators.ExceptionAdvice
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ExceptionAdvice
@Singleton
class ListAllKeysEndpoint(
    @Inject val keyRepository: KeyRepository,
) : KeyManagerDetailsServiceGrpc.KeyManagerDetailsServiceImplBase() {

    override fun findDetails(
        request: FindPixKeyDetailsRequest,
        responseObserver: StreamObserver<FindPixKeyDetailsResponse>
    ) {

        if(request.uuidUsuario.isNullOrBlank()){
            throw IllegalArgumentException("o ID do usuário não pode ser nulo ou vazio")
        }

        val keys = keyRepository.findByClientId(UUID.fromString(request.uuidUsuario)).map{
            FindPixKeyDetailsResponse.Key.newBuilder()
                .setPixId(it.key.toString())
                .setTypeKey(it.typeKey)
                .setTypeAccount(it.typeAccount)
                .build()
        }

        responseObserver.onNext(FindPixKeyDetailsResponse.newBuilder()
            .setClientId(request.uuidUsuario)
            .addAllKeys(keys)
            .build())

        responseObserver.onCompleted()

    }
}