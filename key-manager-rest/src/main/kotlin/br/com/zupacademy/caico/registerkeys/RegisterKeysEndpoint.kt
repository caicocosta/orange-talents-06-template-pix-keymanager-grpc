package br.com.zupacademy.caico.registerkeys

import br.com.zupacademy.caico.KeyManagerRequest
import br.com.zupacademy.caico.KeyManagerResponse
import br.com.zupacademy.caico.KeyManagerRestServiceGrpc
import br.com.zupacademy.caico.validators.ExceptionAdvice
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ExceptionAdvice
@Singleton
class RegisterKeysEndpoint(@Inject val registerKeyService: RegisterKeyService):
    KeyManagerRestServiceGrpc.KeyManagerRestServiceImplBase() {
    override fun register(request: KeyManagerRequest, responseObserver: StreamObserver<KeyManagerResponse>?) {

        val key = request.toModel()
        val createdKey = registerKeyService.register(key)

        responseObserver?.onNext(KeyManagerResponse.newBuilder()
            .setPixId(createdKey?.id.toString())
            .build())

        responseObserver?.onCompleted()
    }
}