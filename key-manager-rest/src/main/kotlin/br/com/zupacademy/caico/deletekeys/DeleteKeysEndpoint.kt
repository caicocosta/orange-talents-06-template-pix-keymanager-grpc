package br.com.zupacademy.caico.deletekeys

import br.com.zupacademy.caico.DeletePixKeyRequest
import br.com.zupacademy.caico.DeletePixKeyResponse
import br.com.zupacademy.caico.KeyManagerRestServiceGrpc
import br.com.zupacademy.caico.validators.ExceptionAdvice
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ExceptionAdvice
@Singleton
class DeleteKeysEndpoint(@Inject val deleteKeyService: DeleteKeyService): KeyManagerRestServiceGrpc.KeyManagerRestServiceImplBase() {

    override fun delete(request: DeletePixKeyRequest, responseObserver: StreamObserver<DeletePixKeyResponse>?) {

        deleteKeyService.delete(
           UUID.fromString(request.uuidUsuario),
           UUID.fromString(request.pixId))

        responseObserver?.onNext(DeletePixKeyResponse.newBuilder()
            .setUuidUsuario(request.uuidUsuario)
            .build())

        responseObserver?.onCompleted()
    }

}