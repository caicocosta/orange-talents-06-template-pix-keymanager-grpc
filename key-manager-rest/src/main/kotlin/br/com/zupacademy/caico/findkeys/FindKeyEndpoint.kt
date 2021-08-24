package br.com.zupacademy.caico.findkeys

import br.com.zupacademy.caico.*
import br.com.zupacademy.caico.validators.ExceptionAdvice
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ExceptionAdvice
@Singleton
class FindKeyEndpoint(@Inject val findKeyService: FindKeyService) : KeyManagerFindServiceGrpc.KeyManagerFindServiceImplBase() {

    override fun find(request: FindPixKeyRequest, responseObserver: StreamObserver<FindPixKeyResponse>?) {

        val clientId = if(request.uuidUsuario != "") UUID.fromString(request.uuidUsuario) else UUID.randomUUID()
        val pixId = if(request.pixId != "") UUID.fromString(request.pixId) else UUID.randomUUID()

        val keyFound = findKeyService.findKey(
            clientId,
            pixId,
            request.key)

        responseObserver?.onNext(FindPixKeyResponse.newBuilder()
            .setPixId(request.pixId)
            .setClientId(request.uuidUsuario)
            .setTypeKey(keyFound.typeKey)
            .setKey(keyFound.key)
            .setNome(keyFound.account.titular.nome)
            .setCpf(keyFound.account.titular.cpf)
            .setAccount(
                Accounts.newBuilder()
                    .setInstituicao(keyFound.account.instituicao.nome)
                    .setAgencia(keyFound.account.agencia)
                    .setNumero(keyFound.account.numero)
                    .setTypeAccount(TypeAccount.CONTA_CORRENTE)
            .build())
            .build())

        responseObserver?.onCompleted()

    }
}